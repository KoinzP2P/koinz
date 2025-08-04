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

package koinz.core.trade.protocol.bisq_v1.tasks.mediation;

import koinz.core.support.dispute.mediation.MediationResultState;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.messages.MediatedPayoutTxPublishedMessage;
import koinz.core.trade.protocol.bisq_v1.messages.TradeMailboxMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.SendMailboxMessageTask;

import koinz.common.taskrunner.TaskRunner;

import org.bitcoinj.core.Transaction;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;


@Slf4j
public class SendMediatedPayoutTxPublishedMessage extends SendMailboxMessageTask {
    public SendMediatedPayoutTxPublishedMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected TradeMailboxMessage getTradeMailboxMessage(String id) {
        Transaction payoutTx = checkNotNull(trade.getPayoutTx(), "trade.getPayoutTx() must not be null");
        return new MediatedPayoutTxPublishedMessage(
                id,
                payoutTx.bitcoinSerialize(),
                processModel.getMyNodeAddress(),
                UUID.randomUUID().toString()
        );
    }

    @Override
    protected void setStateSent() {
        trade.setMediationResultState(MediationResultState.PAYOUT_TX_PUBLISHED_MSG_SENT);
        processModel.getTradeManager().requestPersistence();
    }

    @Override
    protected void setStateArrived() {
        trade.setMediationResultState(MediationResultState.PAYOUT_TX_PUBLISHED_MSG_ARRIVED);
        processModel.getTradeManager().requestPersistence();
    }

    @Override
    protected void setStateStoredInMailbox() {
        trade.setMediationResultState(MediationResultState.PAYOUT_TX_PUBLISHED_MSG_IN_MAILBOX);
        processModel.getTradeManager().requestPersistence();
    }

    @Override
    protected void setStateFault() {
        trade.setMediationResultState(MediationResultState.PAYOUT_TX_PUBLISHED_MSG_SEND_FAILED);
        processModel.getTradeManager().requestPersistence();
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            if (trade.getPayoutTx() == null) {
                log.error("PayoutTx is null");
                failed("PayoutTx is null");
                return;
            }

            super.run();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
