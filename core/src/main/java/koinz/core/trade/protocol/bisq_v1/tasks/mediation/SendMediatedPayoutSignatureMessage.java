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
import koinz.core.trade.model.bisq_v1.Contract;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.messages.MediatedPayoutTxSignatureMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.P2PService;
import koinz.network.p2p.SendMailboxMessageListener;

import koinz.common.crypto.PubKeyRing;
import koinz.common.taskrunner.TaskRunner;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class SendMediatedPayoutSignatureMessage extends TradeTask {
    public SendMediatedPayoutSignatureMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            PubKeyRing pubKeyRing = processModel.getPubKeyRing();
            Contract contract = checkNotNull(trade.getContract(), "contract must not be null");
            PubKeyRing peersPubKeyRing = contract.getPeersPubKeyRing(pubKeyRing);
            NodeAddress peersNodeAddress = contract.getPeersNodeAddress(pubKeyRing);
            P2PService p2PService = processModel.getP2PService();
            MediatedPayoutTxSignatureMessage message = new MediatedPayoutTxSignatureMessage(processModel.getMediatedPayoutTxSignature(),
                    trade.getId(),
                    p2PService.getAddress(),
                    UUID.randomUUID().toString());
            log.info("Send {} to peer {}. tradeId={}, uid={}",
                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid());

            trade.setMediationResultState(MediationResultState.SIG_MSG_SENT);
            processModel.getTradeManager().requestPersistence();
            p2PService.getMailboxMessageService().sendEncryptedMailboxMessage(peersNodeAddress,
                    peersPubKeyRing,
                    message,
                    new SendMailboxMessageListener() {
                        @Override
                        public void onArrived() {
                            log.info("{} arrived at peer {}. tradeId={}, uid={}",
                                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid());

                            trade.setMediationResultState(MediationResultState.SIG_MSG_ARRIVED);
                            processModel.getTradeManager().requestPersistence();
                            complete();
                        }

                        @Override
                        public void onStoredInMailbox() {
                            log.info("{} stored in mailbox for peer {}. tradeId={}, uid={}",
                                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid());

                            trade.setMediationResultState(MediationResultState.SIG_MSG_IN_MAILBOX);
                            processModel.getTradeManager().requestPersistence();
                            complete();
                        }

                        @Override
                        public void onFault(String errorMessage) {
                            log.error("{} failed: Peer {}. tradeId={}, uid={}, errorMessage={}",
                                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid(), errorMessage);
                            trade.setMediationResultState(MediationResultState.SIG_MSG_SEND_FAILED);
                            appendToErrorMessage("Sending message failed: message=" + message + "\nerrorMessage=" + errorMessage);
                            processModel.getTradeManager().requestPersistence();
                            failed(errorMessage);
                        }
                    }
            );
        } catch (Throwable t) {
            failed(t);
        }
    }
}
