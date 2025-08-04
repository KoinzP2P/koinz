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

package koinz.core.trade.statistics;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.monetary.Price;
import koinz.core.monetary.Volume;

import koinz.common.util.MathUtils;

import org.bitcoinj.core.Coin;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.Immutable;

@Immutable
@EqualsAndHashCode
@ToString
@Slf4j
public final class TradeStatisticsForJson {
    public final String currency;
    public final long tradePrice;
    public final long tradeAmount;
    public final long tradeDate;
    public final String paymentMethod;

    // primaryMarket fields are based on industry standard where primaryMarket is always in the focus (in the app BTC is always in the focus - will be changed in a larger refactoring once)
    public String currencyPair;

    public long primaryMarketTradePrice;
    public long primaryMarketTradeAmount;
    public long primaryMarketTradeVolume;

    public TradeStatisticsForJson(TradeStatistics3 tradeStatistics) {
        this.currency = tradeStatistics.getCurrency();
        this.paymentMethod = tradeStatistics.getPaymentMethodId();
        this.tradePrice = tradeStatistics.getPrice();
        this.tradeAmount = tradeStatistics.getAmount();
        this.tradeDate = tradeStatistics.getDateAsLong();

        try {
            Price tradePrice = getTradePrice();
            if (CurrencyUtil.isCryptoCurrency(currency)) {
                currencyPair = currency + "/" + Res.getBaseCurrencyCode();
                primaryMarketTradePrice = tradePrice.getValue();
                primaryMarketTradeAmount = getTradeVolume() != null ?
                        getTradeVolume().getValue() :
                        0;
                primaryMarketTradeVolume = getTradeAmount().getValue();
            } else {
                currencyPair = Res.getBaseCurrencyCode() + "/" + currency;
                // we use precision 4 for fiat based price but on the markets api we use precision 8 so we scale up by 10000
                primaryMarketTradePrice = (long) MathUtils.scaleUpByPowerOf10(tradePrice.getValue(), 4);
                primaryMarketTradeAmount = getTradeAmount().getValue();
                // we use precision 4 for fiat but on the markets api we use precision 8 so we scale up by 10000
                primaryMarketTradeVolume = getTradeVolume() != null ?
                        (long) MathUtils.scaleUpByPowerOf10(getTradeVolume().getValue(), 4) :
                        0;
            }
        } catch (Throwable t) {
            log.error(t.getMessage());
            t.printStackTrace();
        }
    }

    public Price getTradePrice() {
        return Price.valueOf(currency, tradePrice);
    }

    public Coin getTradeAmount() {
        return Coin.valueOf(tradeAmount);
    }

    public Volume getTradeVolume() {
        try {
            return getTradePrice().getVolumeByAmount(getTradeAmount());
        } catch (Throwable t) {
            return Volume.parse("0", currency);
        }
    }
}
