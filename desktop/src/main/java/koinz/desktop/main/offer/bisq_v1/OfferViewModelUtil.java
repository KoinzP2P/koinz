/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.desktop.main.offer.bisq_v1;

import koinz.desktop.util.DisplayUtils;
import koinz.desktop.util.GUIUtil;

import koinz.core.locale.Res;
import koinz.core.monetary.Volume;
import koinz.core.offer.OfferUtil;
import koinz.core.util.VolumeUtil;
import koinz.core.util.coin.CoinFormatter;

import org.bitcoinj.core.Coin;

import java.util.Optional;

// Shared utils for ViewModels
public class OfferViewModelUtil {
    public static String getTradeFeeWithFiatEquivalent(OfferUtil offerUtil,
                                                       Coin tradeFee,
                                                       boolean isCurrencyForMakerFeeBtc,
                                                       CoinFormatter formatter) {
        Optional<Volume> optionalBtcFeeInFiat = offerUtil.getFeeInUserFiatCurrency(tradeFee,
                isCurrencyForMakerFeeBtc,
                formatter);

        return DisplayUtils.getFeeWithFiatAmount(tradeFee, optionalBtcFeeInFiat, formatter);
    }

    public static String getTradeFeeWithFiatEquivalentAndPercentage(OfferUtil offerUtil,
                                                                    Coin tradeFee,
                                                                    Coin tradeAmount,
                                                                    boolean isCurrencyForMakerFeeBtc,
                                                                    CoinFormatter formatter,
                                                                    Coin minTradeFee) {
        if (isCurrencyForMakerFeeBtc) {
            String feeAsBtc = formatter.formatCoinWithCode(tradeFee);
            String percentage;
            if (!tradeFee.isGreaterThan(minTradeFee)) {
                percentage = Res.get("guiUtil.requiredMinimum")
                        .replace("(", "")
                        .replace(")", "");
            } else {
                percentage = GUIUtil.getPercentage(tradeFee, tradeAmount) +
                        " " + Res.get("guiUtil.ofTradeAmount");
            }
            return offerUtil.getFeeInUserFiatCurrency(tradeFee,
                    isCurrencyForMakerFeeBtc,
                    formatter)
                    .map(VolumeUtil::formatAverageVolumeWithCode)
                    .map(feeInFiat -> Res.get("feeOptionWindow.btcFeeWithFiatAndPercentage", feeAsBtc, feeInFiat, percentage))
                    .orElseGet(() -> Res.get("feeOptionWindow.btcFeeWithPercentage", feeAsBtc, percentage));
        } else {
            // For BSQ we use the fiat equivalent only. Calculating the % value would be more effort.
            // We could calculate the BTC value if the BSQ fee and use that...
            return OfferViewModelUtil.getTradeFeeWithFiatEquivalent(offerUtil,
                    tradeFee,
                    false,
                    formatter);
        }
    }
}
