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

package koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_taker;

import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.messages.DepositTxMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.SendDirectMessageListener;

import koinz.common.taskrunner.TaskRunner;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuyerAsTakerSendsDepositTxMessage extends TradeTask {
    public BuyerAsTakerSendsDepositTxMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            if (processModel.getDepositTx() != null) {
                // Remove witnesses from the sent depositTx, so that the seller can still compute the final
                // tx id, but cannot publish it before providing the buyer with a signed delayed payout tx.
                DepositTxMessage message = new DepositTxMessage(UUID.randomUUID().toString(),
                        processModel.getOfferId(),
                        processModel.getMyNodeAddress(),
                        processModel.getDepositTx().bitcoinSerialize(false));

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
                                failed();
                            }
                        }
                );
            } else {
                log.error("processModel.getDepositTx() = {}", processModel.getDepositTx());
                failed("DepositTx is null");
            }
        } catch (Throwable t) {
            failed(t);
        }
    }
}
