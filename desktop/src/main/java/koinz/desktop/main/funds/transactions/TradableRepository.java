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

package koinz.desktop.main.funds.transactions;

import koinz.core.offer.OpenOfferManager;
import koinz.core.trade.ClosedTradableManager;
import koinz.core.trade.TradeManager;
import koinz.core.trade.bisq_v1.FailedTradesManager;
import koinz.core.trade.bsq_swap.BsqSwapTradeManager;
import koinz.core.trade.model.Tradable;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

@Singleton
public class TradableRepository {
    private final OpenOfferManager openOfferManager;
    private final TradeManager tradeManager;
    private final ClosedTradableManager closedTradableManager;
    private final FailedTradesManager failedTradesManager;
    private final BsqSwapTradeManager bsqSwapTradeManager;

    @Inject
    TradableRepository(OpenOfferManager openOfferManager,
                       TradeManager tradeManager,
                       ClosedTradableManager closedTradableManager,
                       FailedTradesManager failedTradesManager,
                       BsqSwapTradeManager bsqSwapTradeManager) {
        this.openOfferManager = openOfferManager;
        this.tradeManager = tradeManager;
        this.closedTradableManager = closedTradableManager;
        this.failedTradesManager = failedTradesManager;
        this.bsqSwapTradeManager = bsqSwapTradeManager;
    }

    public Set<Tradable> getAll() {
        return ImmutableSet.<Tradable>builder()
                .addAll(openOfferManager.getObservableList())
                .addAll(tradeManager.getObservableList())
                .addAll(closedTradableManager.getObservableList())
                .addAll(failedTradesManager.getObservableList())
                .addAll(bsqSwapTradeManager.getObservableList())
                .build();
    }
}
