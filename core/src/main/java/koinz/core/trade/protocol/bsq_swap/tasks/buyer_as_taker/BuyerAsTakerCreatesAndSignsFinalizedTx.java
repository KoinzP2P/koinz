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

package koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_taker;

import koinz.core.trade.model.bsq_swap.BsqSwapTrade;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.BuyerCreatesAndSignsFinalizedTx;

import koinz.common.taskrunner.TaskRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuyerAsTakerCreatesAndSignsFinalizedTx extends BuyerCreatesAndSignsFinalizedTx {
    @SuppressWarnings({"unused"})
    public BuyerAsTakerCreatesAndSignsFinalizedTx(TaskRunner<BsqSwapTrade> taskHandler, BsqSwapTrade bsqSwapTrade) {
        super(taskHandler, bsqSwapTrade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            super.run();
        } catch (Throwable t) {
            failed(t);
        }
    }

    @Override
    protected long getSellersTradeFee() {
        return trade.getMakerFeeAsLong();
    }

    @Override
    protected long getBuyersTradeFee() {
        return trade.getTakerFeeAsLong();
    }
}
