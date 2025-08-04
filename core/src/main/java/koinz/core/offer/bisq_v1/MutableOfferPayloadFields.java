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

package koinz.core.offer.bisq_v1;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * The set of editable OfferPayload fields.
 */
@Getter
@Setter
public final class MutableOfferPayloadFields {

    private final long fixedPrice; // Must be 0 when marketPriceMargin = true (on server).
    private final double marketPriceMargin;
    private final boolean useMarketBasedPrice;
    private final String baseCurrencyCode;
    private final String counterCurrencyCode;
    private final String paymentMethodId;
    private final String makerPaymentAccountId;
    private final long maxTradeLimit;
    private final long maxTradePeriod;
    @Nullable
    private final String countryCode;
    @Nullable
    private final List<String> acceptedCountryCodes;
    @Nullable
    private final String bankId;
    @Nullable
    private final List<String> acceptedBankIds;
    @Nullable
    private final Map<String, String> extraDataMap;

    public MutableOfferPayloadFields(OfferPayload offerPayload) {
        this(offerPayload.getPrice(),
                offerPayload.getMarketPriceMargin(),
                offerPayload.isUseMarketBasedPrice(),
                offerPayload.getBaseCurrencyCode(),
                offerPayload.getCounterCurrencyCode(),
                offerPayload.getPaymentMethodId(),
                offerPayload.getMakerPaymentAccountId(),
                offerPayload.getMaxTradeLimit(),
                offerPayload.getMaxTradePeriod(),
                offerPayload.getCountryCode(),
                offerPayload.getAcceptedCountryCodes(),
                offerPayload.getBankId(),
                offerPayload.getAcceptedBankIds(),
                offerPayload.getExtraDataMap());
    }

    public MutableOfferPayloadFields(long fixedPrice,
                                     double marketPriceMargin,
                                     boolean useMarketBasedPrice,
                                     String baseCurrencyCode,
                                     String counterCurrencyCode,
                                     String paymentMethodId,
                                     String makerPaymentAccountId,
                                     long maxTradeLimit,
                                     long maxTradePeriod,
                                     @Nullable String countryCode,
                                     @Nullable List<String> acceptedCountryCodes,
                                     @Nullable String bankId,
                                     @Nullable List<String> acceptedBankIds,
                                     @Nullable Map<String, String> extraDataMap) {
        this.fixedPrice = fixedPrice;
        this.marketPriceMargin = marketPriceMargin;
        this.useMarketBasedPrice = useMarketBasedPrice;
        this.baseCurrencyCode = baseCurrencyCode;
        this.counterCurrencyCode = counterCurrencyCode;
        this.paymentMethodId = paymentMethodId;
        this.makerPaymentAccountId = makerPaymentAccountId;
        this.maxTradeLimit = maxTradeLimit;
        this.maxTradePeriod = maxTradePeriod;
        this.countryCode = countryCode;
        this.acceptedCountryCodes = acceptedCountryCodes;
        this.bankId = bankId;
        this.acceptedBankIds = acceptedBankIds;
        this.extraDataMap = extraDataMap;
    }

    @Override
    public String toString() {
        return "MutableOfferPayloadFields{" + "\n" +
                "  fixedPrice=" + fixedPrice + "\n" +
                ", marketPriceMargin=" + marketPriceMargin + "\n" +
                ", useMarketBasedPrice=" + useMarketBasedPrice + "\n" +
                ", baseCurrencyCode='" + baseCurrencyCode + '\'' + "\n" +
                ", counterCurrencyCode='" + counterCurrencyCode + '\'' + "\n" +
                ", paymentMethodId='" + paymentMethodId + '\'' + "\n" +
                ", makerPaymentAccountId='" + makerPaymentAccountId + '\'' + "\n" +
                ", maxTradeLimit='" + maxTradeLimit + '\'' + "\n" +
                ", maxTradePeriod='" + maxTradePeriod + '\'' + "\n" +
                ", countryCode='" + countryCode + '\'' + "\n" +
                ", acceptedCountryCodes=" + acceptedCountryCodes + "\n" +
                ", bankId='" + bankId + '\'' + "\n" +
                ", acceptedBankIds=" + acceptedBankIds + "\n" +
                ", extraDataMap=" + extraDataMap + "\n" +
                '}';
    }
}
