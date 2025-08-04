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

package koinz.core.trade.protocol.bisq_v1.tasks.taker;

import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.common.taskrunner.TaskRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TakerVerifyMakerFeePayment extends TradeTask {
    public TakerVerifyMakerFeePayment(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            //TODO impl. missing
            // int numOfPeersSeenTx = processModel.getWalletService().getNumOfPeersSeenTx(processModel.getTakeOfferFeeTxId().getHashAsString());
       /* if (numOfPeersSeenTx > 2) {
            resultHandler.handleResult();
        }*/
            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
