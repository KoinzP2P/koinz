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

package koinz.core.trade.protocol.bisq_v1.tasks.buyer;

import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.messages.DelayedPayoutTxSignatureResponse;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.SendDirectMessageListener;

import koinz.common.taskrunner.TaskRunner;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class BuyerSendsDelayedPayoutTxSignatureResponse extends TradeTask {
    public BuyerSendsDelayedPayoutTxSignatureResponse(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            byte[] delayedPayoutTxSignature = checkNotNull(processModel.getDelayedPayoutTxSignature());
            byte[] depositTxBytes = processModel.getDepositTx() != null
                    ? processModel.getDepositTx().bitcoinSerialize() // set in BuyerAsTakerSignsDepositTx task
                    : processModel.getPreparedDepositTx();           // set in BuyerAsMakerCreatesAndSignsDepositTx task

            DelayedPayoutTxSignatureResponse message = new DelayedPayoutTxSignatureResponse(UUID.randomUUID().toString(),
                    processModel.getOfferId(),
                    processModel.getMyNodeAddress(),
                    delayedPayoutTxSignature,
                    depositTxBytes);

            NodeAddress peersNodeAddress = trade.getTradingPeerNodeAddress();
            log.info("Send {} to peer {}. tradeId={}, uid={}",
                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid());
            processModel.getP2PService().sendEncryptedDirectMessage(
                    peersNodeAddress,
                    processModel.getTradePeer().getPubKeyRing(),
                    message,
                    new SendDirectMessageListener() {
                        @Override
                        public void onArrived() {
                            log.info("{} arrived at peer {}. tradeId={}, uid={}",
                                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid());
                            complete();
                        }

                        @Override
                        public void onFault(String errorMessage) {
                            log.error("{} failed: Peer {}. tradeId={}, uid={}, errorMessage={}",
                                    message.getClass().getSimpleName(), peersNodeAddress, message.getTradeId(), message.getUid(), errorMessage);
                            appendToErrorMessage("Sending message failed: message=" + message + "\nerrorMessage=" + errorMessage);
                            failed(errorMessage);
                        }
                    }
            );
        } catch (Throwable t) {
            failed(t);
        }
    }
}
