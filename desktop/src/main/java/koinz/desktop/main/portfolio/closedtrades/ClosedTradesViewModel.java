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

package koinz.desktop.main.portfolio.closedtrades;

import koinz.desktop.common.model.ActivatableWithDataModel;
import koinz.desktop.common.model.ViewModel;

import koinz.core.monetary.Volume;
import koinz.core.trade.ClosedTradableFormatter;

import org.bitcoinj.core.Coin;

import com.google.inject.Inject;

import java.util.Map;

public class ClosedTradesViewModel extends ActivatableWithDataModel<ClosedTradesDataModel> implements ViewModel {
    private final ClosedTradableFormatter closedTradableFormatter;

    @Inject
    public ClosedTradesViewModel(ClosedTradesDataModel dataModel, ClosedTradableFormatter closedTradableFormatter) {
        super(dataModel);

        this.closedTradableFormatter = closedTradableFormatter;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Used in ClosedTradesSummaryWindow
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Coin getTotalTradeAmount() {
        return dataModel.getTotalAmount();
    }

    public String getTotalAmountWithVolume(Coin totalTradeAmount) {
        return dataModel.getVolumeInUserFiatCurrency(totalTradeAmount)
                .map(volume -> closedTradableFormatter.getTotalAmountWithVolumeAsString(totalTradeAmount, volume))
                .orElse("");
    }

    public Map<String, String> getTotalVolumeByCurrency() {
        return closedTradableFormatter.getTotalVolumeByCurrencyAsString(dataModel.getListAsTradables());
    }

    public String getTotalTxFee(Coin totalTradeAmount) {
        Coin totalTxFee = dataModel.getTotalTxFee();
        return closedTradableFormatter.getTotalTxFeeAsString(totalTradeAmount, totalTxFee);
    }

    public String getTotalTradeFeeInBtc(Coin totalTradeAmount) {
        Coin totalTradeFee = dataModel.getTotalTradeFee(true);
        return closedTradableFormatter.getTotalTradeFeeInBtcAsString(totalTradeAmount, totalTradeFee);
    }

    public String getTotalTradeFeeInBsq(Coin totalTradeAmount) {
        return dataModel.getVolume(totalTradeAmount, "USD")
                .filter(v -> v.getValue() > 0)
                .map(tradeAmountVolume -> {
                    Coin totalTradeFee = dataModel.getTotalTradeFee(false);
                    Volume bsqVolumeInUsd = dataModel.getBsqVolumeInUsdWithAveragePrice(totalTradeFee); // with 4 decimal
                    return closedTradableFormatter.getTotalTradeFeeInBsqAsString(totalTradeFee,
                            tradeAmountVolume,
                            bsqVolumeInUsd);
                })
                .orElse("");
    }
}
