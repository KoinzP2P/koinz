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


import koinz.core.trade.model.bsq_swap.BsqSwapBuyerAsMakerTrade;
import koinz.core.trade.protocol.TradeMessage;
import koinz.core.trade.protocol.TradeTaskRunner;
import koinz.core.trade.protocol.bisq_v1.tasks.EnforceFilterVersion;
import koinz.core.trade.protocol.bsq_swap.messages.BsqSwapFinalizeTxRequest;
import koinz.core.trade.protocol.bsq_swap.messages.BsqSwapRequest;
import koinz.core.trade.protocol.bsq_swap.messages.SellersBsqSwapRequest;
import koinz.core.trade.protocol.bsq_swap.tasks.ApplyFilter;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.BuyerPublishesTx;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.PublishTradeStatistics;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer.SendFinalizedTxMessage;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.BuyerAsMakerCreatesAndSignsFinalizedTx;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.BuyerAsMakerCreatesBsqInputsAndChange;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.BuyerAsMakerProcessBsqSwapFinalizeTxRequest;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.BuyerAsMakerRemoveOpenOffer;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.ProcessSellersBsqSwapRequest;
import koinz.core.trade.protocol.bsq_swap.tasks.buyer_as_maker.SendBsqSwapTxInputsMessage;

import koinz.network.p2p.NodeAddress;

import koinz.common.handlers.ErrorMessageHandler;

import lombok.extern.slf4j.Slf4j;

import static koinz.core.trade.model.bsq_swap.BsqSwapTrade.State.PREPARATION;

@Slf4j
public class BsqSwapBuyerAsMakerProtocol extends BsqSwapBuyerProtocol implements BsqSwapMakerProtocol {

    public BsqSwapBuyerAsMakerProtocol(BsqSwapBuyerAsMakerTrade trade) {
        super(trade);
    }

    @Override
    public void handleTakeOfferRequest(BsqSwapRequest bsqSwapRequest,
                                       NodeAddress sender,
                                       ErrorMessageHandler errorMessageHandler) {
        SellersBsqSwapRequest request = (SellersBsqSwapRequest) bsqSwapRequest;
        expect(preCondition(PREPARATION == trade.getTradeState())
                .with(request)
                .from(sender))
                .setup(tasks(
                        EnforceFilterVersion.class,
                        ApplyFilter.class,
                        ProcessSellersBsqSwapRequest.class,
                        BuyerAsMakerCreatesBsqInputsAndChange.class,
                        SendBsqSwapTxInputsMessage.class)
                        .using(new TradeTaskRunner(trade,
                                () -> handleTaskRunnerSuccess(request),
                                errorMessage -> {
                                    errorMessageHandler.handleErrorMessage(errorMessage);
                                    handleTaskRunnerFault(request, errorMessage);
                                }))
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
                        BuyerAsMakerProcessBsqSwapFinalizeTxRequest.class,
                        BuyerAsMakerCreatesAndSignsFinalizedTx.class,
                        BuyerPublishesTx.class,
                        BuyerAsMakerRemoveOpenOffer.class,
                        PublishTradeStatistics.class,
                        SendFinalizedTxMessage.class)
                        .using(new TradeTaskRunner(trade,
                                () -> {
                                    stopTimeout();
                                    handleTaskRunnerSuccess(message);
                                },
                                errorMessage -> handleTaskRunnerFault(message, errorMessage)))
                )
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
