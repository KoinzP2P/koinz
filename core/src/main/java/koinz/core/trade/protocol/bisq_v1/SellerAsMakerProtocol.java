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


import koinz.core.trade.model.bisq_v1.SellerAsMakerTrade;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.TradeMessage;
import koinz.core.trade.protocol.TradeTaskRunner;
import koinz.core.trade.protocol.bisq_v1.messages.CounterCurrencyTransferStartedMessage;
import koinz.core.trade.protocol.bisq_v1.messages.DelayedPayoutTxSignatureResponse;
import koinz.core.trade.protocol.bisq_v1.messages.DepositTxMessage;
import koinz.core.trade.protocol.bisq_v1.messages.InputsForDepositTxRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.ApplyFilter;
import koinz.core.trade.protocol.bisq_v1.tasks.CheckIfDaoStateIsInSync;
import koinz.core.trade.protocol.bisq_v1.tasks.CheckRestrictions;
import koinz.core.trade.protocol.bisq_v1.tasks.EnforceFilterVersion;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerCreateAndSignContract;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerProcessesInputsForDepositTxRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerRemovesOpenOffer;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerSetsLockTime;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerVerifyTakerFeePayment;
import koinz.core.trade.protocol.bisq_v1.tasks.seller.MaybeCreateSubAccount;
import koinz.core.trade.protocol.bisq_v1.tasks.seller.SellerCreatesDelayedPayoutTx;
import koinz.core.trade.protocol.bisq_v1.tasks.seller.SellerSendDelayedPayoutTxSignatureRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.seller.SellerSignsDelayedPayoutTx;
import koinz.core.trade.protocol.bisq_v1.tasks.seller_as_maker.SellerAsMakerCreatesUnsignedDepositTx;
import koinz.core.trade.protocol.bisq_v1.tasks.seller_as_maker.SellerAsMakerFinalizesDepositTx;
import koinz.core.trade.protocol.bisq_v1.tasks.seller_as_maker.SellerAsMakerProcessDepositTxMessage;
import koinz.core.trade.protocol.bisq_v1.tasks.seller_as_maker.SellerAsMakerSendsInputsForDepositTxResponse;

import koinz.network.p2p.NodeAddress;

import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.handlers.ResultHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SellerAsMakerProtocol extends SellerProtocol implements MakerProtocol {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public SellerAsMakerProtocol(SellerAsMakerTrade trade) {
        super(trade);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Handle take offer request
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleTakeOfferRequest(InputsForDepositTxRequest message,
                                       NodeAddress peer,
                                       ErrorMessageHandler errorMessageHandler) {
        expect(phase(Trade.Phase.INIT)
                .with(message)
                .from(peer))
                .setup(tasks(
                        EnforceFilterVersion.class,
                        CheckIfDaoStateIsInSync.class,
                        MaybeCreateSubAccount.class,
                        MakerProcessesInputsForDepositTxRequest.class,
                        ApplyFilter.class,
                        CheckRestrictions.class,
                        getVerifyPeersFeePaymentClass(),
                        MakerSetsLockTime.class,
                        MakerCreateAndSignContract.class,
                        SellerAsMakerCreatesUnsignedDepositTx.class,
                        SellerAsMakerSendsInputsForDepositTxResponse.class)
                        .using(new TradeTaskRunner(trade,
                                () -> handleTaskRunnerSuccess(message),
                                errorMessage -> {
                                    errorMessageHandler.handleErrorMessage(errorMessage);
                                    handleTaskRunnerFault(message, errorMessage);
                                }))
                        .withTimeout(120))
                .executeTasks();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Incoming messages Take offer process
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected void handle(DepositTxMessage message, NodeAddress peer) {
        expect(phase(Trade.Phase.TAKER_FEE_PUBLISHED)
                .with(message)
                .from(peer))
                .setup(tasks(
                        MakerRemovesOpenOffer.class,
                        SellerAsMakerProcessDepositTxMessage.class,
                        SellerAsMakerFinalizesDepositTx.class,
                        SellerCreatesDelayedPayoutTx.class,
                        SellerSignsDelayedPayoutTx.class,
                        SellerSendDelayedPayoutTxSignatureRequest.class)
                        .withTimeout(120))
                .executeTasks();
    }

    // We keep the handler here in as well to make it more transparent which messages we expect
    @Override
    protected void handle(DelayedPayoutTxSignatureResponse message, NodeAddress peer) {
        super.handle(message, peer);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Incoming message when buyer has clicked payment started button
    ///////////////////////////////////////////////////////////////////////////////////////////

    // We keep the handler here in as well to make it more transparent which messages we expect
    @Override
    protected void handle(CounterCurrencyTransferStartedMessage message, NodeAddress peer) {
        super.handle(message, peer);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // User interaction
    ///////////////////////////////////////////////////////////////////////////////////////////

    // We keep the handler here in as well to make it more transparent which events we expect
    @Override
    public void onPaymentReceived(ResultHandler resultHandler, ErrorMessageHandler errorMessageHandler) {
        super.onPaymentReceived(resultHandler, errorMessageHandler);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Massage dispatcher
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onTradeMessage(TradeMessage message, NodeAddress peer) {
        super.onTradeMessage(message, peer);

        log.info("Received {} from {} with tradeId {} and uid {}",
                message.getClass().getSimpleName(), peer, message.getTradeId(), message.getUid());

        if (message instanceof DepositTxMessage) {
            handle((DepositTxMessage) message, peer);
        }
    }

    @Override
    protected Class<? extends TradeTask> getVerifyPeersFeePaymentClass() {
        return MakerVerifyTakerFeePayment.class;
    }
}
