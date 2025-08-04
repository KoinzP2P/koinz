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

package koinz.core.trade.protocol.bsq_swap;


import koinz.core.offer.Offer;
import koinz.core.trade.model.bsq_swap.BsqSwapBuyerAsTakerTrade;
import koinz.core.trade.protocol.TradeMessage;
import koinz.core.trade.protocol.TradeTaskRunner;
import koinz.core.trade.protocol.bisq_v1.tasks.EnforceFilterVersion;
import koinz.core.trade.protocol.bsq_swap.messages.BsqSwapFinalizeTxRequest;
import koinz.core.trade.protocol.bsq_swap.tasks.ApplyFilter;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.BuyerPublishesTx;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.PublishTradeStatistics;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.SendFinalizedTxMessage;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_taker.BuyerAsTakerCreatesAndSignsFinalizedTx;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_taker.BuyerAsTakerCreatesBsqInputsAndChange;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_taker.BuyerAsTakerProcessBsqSwapFinalizeTxRequest;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_taker.SendBuyersBsqSwapRequest;

import koinz.network.p2p.NodeAddress;

import lombok.extern.slf4j.Slf4j;

import static koinz.core.trade.model.bsq_swap.BsqSwapTrade.State.PREPARATION;
import static koinz.core.trade.protocol.bisq_v1.TakerProtocol.TakerEvent.TAKE_OFFER;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class BsqSwapBuyerAsTakerProtocol extends BsqSwapBuyerProtocol implements BsqSwapTakerProtocol {

    public BsqSwapBuyerAsTakerProtocol(BsqSwapBuyerAsTakerTrade trade) {
        super(trade);

        Offer offer = checkNotNull(trade.getOffer());
        protocolModel.getTradePeer().setPubKeyRing(offer.getPubKeyRing());
    }

    @Override
    public void onTakeOffer() {
        expect(preCondition(PREPARATION == trade.getTradeState())
                .with(TAKE_OFFER)
                .from(trade.getTradingPeerNodeAddress()))
                .setup(tasks(
                        EnforceFilterVersion.class,
                        ApplyFilter.class,
                        BuyerAsTakerCreatesBsqInputsAndChange.class,
                        SendBuyersBsqSwapRequest.class)
                        .withTimeout(40))
                .executeTasks();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Incoming message handling
    ///////////////////////////////////////////////////////////////////////////////////////////

    void handle(BsqSwapFinalizeTxRequest message, NodeAddress sender) {
        expect(preCondition(PREPARATION == trade.getTradeState())
                .with(message)
                .from(sender))
                .setup(tasks(
                        BuyerAsTakerProcessBsqSwapFinalizeTxRequest.class,
                        BuyerAsTakerCreatesAndSignsFinalizedTx.class,
                        BuyerPublishesTx.class,
                        PublishTradeStatistics.class,
                        SendFinalizedTxMessage.class)
                        .using(new TradeTaskRunner(trade,
                                () -> {
                                    stopTimeout();
                                    handleTaskRunnerSuccess(message);
                                },
                                errorMessage -> handleTaskRunnerFault(message, errorMessage))))
                .executeTasks();
    }

    @Override
    protected void onTradeMessage(TradeMessage message, NodeAddress peer) {
        log.info("Received {} from {} with tradeId {} and uid {}",
                message.getClass().getSimpleName(), peer, message.getTradeId(), message.getUid());

        if (message instanceof BsqSwapFinalizeTxRequest) {
            handle((BsqSwapFinalizeTxRequest) message, peer);
        }
    }
}
