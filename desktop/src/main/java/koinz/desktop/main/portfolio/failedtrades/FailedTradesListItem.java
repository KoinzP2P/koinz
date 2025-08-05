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

package koinz.desktop.main.portfolio.failedtrades;

import koinz.desktop.util.DisplayUtils;
import koinz.desktop.util.filtering.FilterableListItem;
import koinz.desktop.util.filtering.FilteringUtils;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferDirection;
import koinz.core.trade.bisq_v1.FailedTradesManager;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.util.FormattingUtils;
import koinz.core.util.VolumeUtil;
import koinz.core.util.coin.CoinFormatter;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

class FailedTradesListItem implements FilterableListItem {
    @Getter
    private final Trade trade;
    private final CoinFormatter btcFormatter;
    private final FailedTradesManager failedTradesManager;

    FailedTradesListItem(Trade trade, CoinFormatter btcFormatter, FailedTradesManager failedTradesManager) {
        this.trade = trade;
        this.btcFormatter = btcFormatter;
        this.failedTradesManager = failedTradesManager;
    }

    public String getDateAsString() {
        return DisplayUtils.formatDateTime(trade.getDate());
    }

    public String getMarketLabel() {
        return CurrencyUtil.getCurrencyPair(trade.getOffer().getCurrencyCode());
    }

    public String getAmountAsString() {
        return btcFormatter.formatCoin(trade.getAmount());
    }

    public String getPriceAsString() {
        return FormattingUtils.formatPrice(trade.getPrice());
    }

    public String getVolumeAsString() {
        return VolumeUtil.formatVolumeWithCode(trade.getVolume());
    }

    public String getDirectionLabel() {
        Offer offer = trade.getOffer();
        OfferDirection direction = failedTradesManager.wasMyOffer(offer) ? offer.getDirection() : offer.getMirroredDirection();
        return DisplayUtils.getDirectionWithCode(direction, trade.getOffer().getCurrencyCode());
    }

    public String getState() {
        return Res.get("portfolio.failed.Failed");
    }

    @Override
    public boolean match(String filterString) {
        if (filterString.isEmpty()) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getDateAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getMarketLabel(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getPriceAsString(), filterString)) {
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
        if (FilteringUtils.match(getTrade().getOffer(), filterString)) {
            return true;
        }
        return FilteringUtils.match(getTrade(), filterString);
    }
}
