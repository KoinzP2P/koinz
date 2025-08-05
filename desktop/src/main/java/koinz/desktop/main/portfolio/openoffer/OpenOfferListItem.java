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

package koinz.desktop.main.portfolio.openoffer;

import koinz.desktop.util.DisplayUtils;
import koinz.desktop.util.filtering.FilterableListItem;
import koinz.desktop.util.filtering.FilteringUtils;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.monetary.Price;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferDirection;
import koinz.core.offer.OpenOffer;
import koinz.core.offer.OpenOfferManager;
import koinz.core.util.FormattingUtils;
import koinz.core.util.PriceUtil;
import koinz.core.util.VolumeUtil;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * We could remove that wrapper if it is not needed for additional UI only fields.
 */
class OpenOfferListItem implements FilterableListItem {
    @Getter
    private final OpenOffer openOffer;
    private final CoinFormatter btcFormatter;
    private final BsqFormatter bsqFormatter;
    private final OpenOfferManager openOfferManager;


    OpenOfferListItem(OpenOffer openOffer,
                      CoinFormatter btcFormatter,
                      BsqFormatter bsqFormatter,
                      OpenOfferManager openOfferManager) {
        this.openOffer = openOffer;
        this.btcFormatter = btcFormatter;
        this.bsqFormatter = bsqFormatter;
        this.openOfferManager = openOfferManager;
    }

    public Offer getOffer() {
        return openOffer.getOffer();
    }

    public String getDateAsString() {
        return DisplayUtils.formatDateTime(getOffer().getDate());
    }

    public String getMarketDescription() {
        return CurrencyUtil.getCurrencyPair(getOffer().getCurrencyCode());
    }

    public String getPriceAsString() {
        Price price = getOffer().getPrice();
        if (price != null) {
            return FormattingUtils.formatPrice(price);
        } else {
            return Res.get("shared.na");
        }
    }

    public Double getPriceDeviationAsDouble() {
        Offer offer = getOffer();
        return PriceUtil.offerPercentageToDeviation(offer).orElse(0d);
    }

    public String getPriceDeviationAsString() {
        Offer offer = getOffer();
        return PriceUtil.offerPercentageToDeviation(offer)
                .map(FormattingUtils::formatPercentagePrice)
                .orElse("");
    }

    public String getPaymentMethodAsString() {
        return getOffer().getPaymentMethodNameWithCountryCode();
    }

    public String getVolumeAsString() {
        return VolumeUtil.formatVolume(getOffer(), false, 0) + " " + getOffer().getCurrencyCode();
    }

    public String getAmountAsString() {
        return DisplayUtils.formatAmount(getOffer(), btcFormatter);
    }

    public String getDirectionLabel() {
        Offer offer = getOffer();
        OfferDirection direction = openOfferManager.isMyOffer(offer) ? offer.getDirection() : offer.getMirroredDirection();
        return DisplayUtils.getDirectionWithCode(direction, getOffer().getCurrencyCode());
    }

    public String getMakerFeeAsString() {
        Offer offer = getOffer();
        return offer.isCurrencyForMakerFeeBtc() ?
                btcFormatter.formatCoinWithCode(offer.getMakerFee()) :
                bsqFormatter.formatCoinWithCode(offer.getMakerFee());
    }

    public boolean isNotPublished() {
        return openOffer.isDeactivated() || (getOffer().isBsqSwapOffer() && openOffer.isBsqSwapOfferHasMissingFunds());
    }

    public String getTriggerPriceAsString() {
        Offer offer = getOffer();
        long triggerPrice = openOffer.getTriggerPrice();
        if (!offer.isUseMarketBasedPrice() || triggerPrice <= 0) {
            return Res.get("shared.na");
        } else {
            return PriceUtil.formatMarketPrice(triggerPrice, offer.getCurrencyCode());
        }
    }

    String getMakerFeeTxId() {
        String makerFeeTxId = getOffer().getOfferFeePaymentTxId();
        return makerFeeTxId != null ? makerFeeTxId : "";
    }

    @Override
    public boolean match(String filterString) {
        if (filterString.isEmpty()) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getDateAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getMarketDescription(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getPriceAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getPriceDeviationAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getPaymentMethodAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getVolumeAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getAmountAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getDirectionLabel(), filterString)) {
            return true;
        }
        return FilteringUtils.match(getOffer(), filterString);
    }
}
