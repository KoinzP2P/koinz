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

package koinz.core.trade.protocol.bisq_v1.tasks.seller;

import koinz.core.btc.wallet.TradeWalletService;
import koinz.core.trade.bisq_v1.TradeDataValidation;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.common.taskrunner.TaskRunner;
import koinz.common.util.Tuple2;

import org.bitcoinj.core.Transaction;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class SellerCreatesDelayedPayoutTx extends TradeTask {

    public SellerCreatesDelayedPayoutTx(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            TradeWalletService tradeWalletService = processModel.getTradeWalletService();
            Transaction depositTx = checkNotNull(processModel.getDepositTx());
            Transaction preparedDelayedPayoutTx;
            long inputAmount = depositTx.getOutput(0).getValue().value;
            long tradeTxFeeAsLong = trade.getTradeTxFeeAsLong();
            int selectionHeight = processModel.getBurningManSelectionHeight();
            List<Tuple2<Long, String>> delayedPayoutTxReceivers = processModel.getDelayedPayoutTxReceiverService().getReceivers(
                    selectionHeight,
                    inputAmount,
                    tradeTxFeeAsLong);
            log.info("Create delayedPayoutTx using selectionHeight {} and receivers {}", selectionHeight, delayedPayoutTxReceivers);
            long lockTime = trade.getLockTime();
            preparedDelayedPayoutTx = tradeWalletService.createDelayedUnsignedPayoutTx(
                    depositTx,
                    delayedPayoutTxReceivers,
                    lockTime);

            TradeDataValidation.validateDelayedPayoutTx(trade,
                    preparedDelayedPayoutTx,
                    processModel.getBtcWalletService());

            processModel.setPreparedDelayedPayoutTx(preparedDelayedPayoutTx);

            processModel.getTradeManager().requestPersistence();

            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
