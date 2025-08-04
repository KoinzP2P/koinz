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

package koinz.core.trade.protocol.bisq_v1;


import koinz.core.offer.Offer;
import koinz.core.trade.model.bisq_v1.BuyerAsTakerTrade;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.TradeMessage;
import koinz.core.trade.protocol.bisq_v1.messages.DelayedPayoutTxSignatureRequest;
import koinz.core.trade.protocol.bisq_v1.messages.DepositTxAndDelayedPayoutTxMessage;
import koinz.core.trade.protocol.bisq_v1.messages.InputsForDepositTxResponse;
import koinz.core.trade.protocol.bisq_v1.messages.PayoutTxPublishedMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.ApplyFilter;
import koinz.core.trade.protocol.bisq_v1.tasks.CheckIfDaoStateIsInSync;
import koinz.core.trade.protocol.bisq_v1.tasks.CheckRestrictions;
import koinz.core.trade.protocol.bisq_v1.tasks.EnforceFilterVersion;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerFinalizesDelayedPayoutTx;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerProcessDelayedPayoutTxSignatureRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerSendsDelayedPayoutTxSignatureResponse;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerSetupDepositTxListener;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerSignsDelayedPayoutTx;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer.BuyerVerifiesPreparedDelayedPayoutTx;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_taker.BuyerAsTakerCreatesDepositTxInputs;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_taker.BuyerAsTakerSendsDepositTxMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_taker.BuyerAsTakerSignsDepositTx;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.CreateTakerFeeTx;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.TakerProcessesInputsForDepositTxResponse;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.TakerPublishFeeTx;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.TakerSendInputsForDepositTxRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.TakerVerifyAndSignContract;
import koinz.core.trade.protocol.bisq_v1.tasks.taker.TakerVerifyMakerFeePayment;

import koinz.network.p2p.NodeAddress;

import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.handlers.ResultHandler;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class BuyerAsTakerProtocol extends BuyerProtocol implements TakerProtocol {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BuyerAsTakerProtocol(BuyerAsTakerTrade trade) {
        super(trade);

        Offer offer = checkNotNull(trade.getOffer());
        processModel.getTradePeer().setPubKeyRing(offer.getPubKeyRing());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Take offer
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTakeOffer() {
        expect(phase(Trade.Phase.INIT)
                .with(TakerEvent.TAKE_OFFER))
                .setup(tasks(
                        EnforceFilterVersion.class,
                        CheckIfDaoStateIsInSync.class,
                        ApplyFilter.class,
                        CheckRestrictions.class,
                        getVerifyPeersFeePaymentClass(),
                        CreateTakerFeeTx.class,
                        BuyerAsTakerCreatesDepositTxInputs.class,
                        TakerSendInputsForDepositTxRequest.class)
                        .withTimeout(120))
                .run(() -> {
                    processModel.setTempTradingPeerNodeAddress(trade.getTradingPeerNodeAddress());
                    processModel.getTradeManager().requestPersistence();
                })
                .executeTasks();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Incoming messages Take offer process
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void handle(InputsForDepositTxResponse message, NodeAddress peer) {
        expect(phase(Trade.Phase.INIT)
                .with(message)
                .from(peer))
                .setup(tasks(TakerProcessesInputsForDepositTxResponse.class,
                        ApplyFilter.class,
                        TakerVerifyAndSignContract.class,
                        TakerPublishFeeTx.class,
                        BuyerAsTakerSignsDepositTx.class,
                        BuyerSetupDepositTxListener.class,
                        BuyerAsTakerSendsDepositTxMessage.class)
                        .withTimeout(120))
                .executeTasks();
    }

    protected void handle(DelayedPayoutTxSignatureRequest message, NodeAddress peer) {
        expect(phase(Trade.Phase.TAKER_FEE_PUBLISHED)
                .with(message)
                .from(peer))
                .setup(tasks(
                        BuyerProcessDelayedPayoutTxSignatureRequest.class,
                        BuyerVerifiesPreparedDelayedPayoutTx.class,
                        BuyerSignsDelayedPayoutTx.class,
                        BuyerFinalizesDelayedPayoutTx.class,
                        BuyerSendsDelayedPayoutTxSignatureResponse.class)
                        .withTimeout(120))
                .executeTasks();
    }

    // We keep the handler here in as well to make it more transparent which messages we expect
    @Override
    protected void handle(DepositTxAndDelayedPayoutTxMessage message, NodeAddress peer) {
        super.handle(message, peer);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // User interaction
    ///////////////////////////////////////////////////////////////////////////////////////////

    // We keep the handler here in as well to make it more transparent which events we expect
    @Override
    public void onPaymentStarted(ResultHandler resultHandler, ErrorMessageHandler errorMessageHandler) {
        super.onPaymentStarted(resultHandler, errorMessageHandler);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Incoming message Payout tx
    ///////////////////////////////////////////////////////////////////////////////////////////

    // We keep the handler here in as well to make it more transparent which messages we expect
    @Override
    protected void handle(PayoutTxPublishedMessage message, NodeAddress peer) {
        super.handle(message, peer);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Message dispatcher
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onTradeMessage(TradeMessage message, NodeAddress peer) {
        super.onTradeMessage(message, peer);

        if (message instanceof InputsForDepositTxResponse) {
            handle((InputsForDepositTxResponse) message, peer);
        }
    }

    @Override
    protected Class<? extends TradeTask> getVerifyPeersFeePaymentClass() {
        return TakerVerifyMakerFeePayment.class;
    }
}
