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

package koinz.core.trade.protocol.bsq_swap.tasks.buyer;

import koinz.core.trade.model.bsq_swap.BsqSwapTrade;
import koinz.core.trade.protocol.bsq_swap.tasks.BsqSwapTask;
import koinz.core.trade.statistics.TradeStatistics3;

import koinz.common.app.Capability;
import koinz.common.taskrunner.TaskRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishTradeStatistics extends BsqSwapTask {
    @SuppressWarnings({"unused"})
    public PublishTradeStatistics(TaskRunner<BsqSwapTrade> taskHandler, BsqSwapTrade bsqSwapTrade) {
        super(taskHandler, bsqSwapTrade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            protocolModel.getP2PService().findPeersCapabilities(trade.getTradingPeerNodeAddress())
                    .filter(capabilities -> capabilities.containsAll(Capability.TRADE_STATISTICS_3))
                    .ifPresentOrElse(capabilities -> {
                                TradeStatistics3 tradeStatistics = TradeStatistics3.from(trade);
                                if (tradeStatistics.isValid()) {
                                    log.info("Publishing trade statistics");
                                    protocolModel.getP2PService().addPersistableNetworkPayload(tradeStatistics, true);
                                } else {
                                    log.warn("Trade statistics are invalid. We do not publish. {}", tradeStatistics);
                                }

                                complete();
                            },
                            () -> {
                                log.info("Our peer does not has updated yet, so they will publish the trade statistics. " +
                                        "To avoid duplicates we do not publish from our side.");
                                complete();
                            });
        } catch (Throwable t) {
            failed(t);
        }
    }
}
