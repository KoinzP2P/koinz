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

import koinz.core.trade.model.bisq_v1.BuyerAsMakerTrade;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.TradeTaskRunner;
import koinz.core.trade.protocol.bisq_v1.messages.DelayedPayoutTxSignatureRequest;
import koinz.core.trade.protocol.bisq_v1.messages.DepositTxAndDelayedPayoutTxMessage;
import koinz.core.trade.protocol.bisq_v1.messages.InputsForDepositTxRequest;
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
import koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_maker.BuyerAsMakerCreatesAndSignsDepositTx;
import koinz.core.trade.protocol.bisq_v1.tasks.buyer_as_maker.BuyerAsMakerSendsInputsForDepositTxResponse;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerCreateAndSignContract;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerProcessesInputsForDepositTxRequest;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerRemovesOpenOffer;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerSetsLockTime;
import koinz.core.trade.protocol.bisq_v1.tasks.maker.MakerVerifyTakerFeePayment;

import koinz.network.p2p.NodeAddress;

import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.handlers.ResultHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuyerAsMakerProtocol extends BuyerProtocol implements MakerProtocol {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BuyerAsMakerProtocol(BuyerAsMakerTrade trade) {
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
                        MakerProcessesInputsForDepositTxRequest.class,
                        ApplyFilter.class,
                        CheckRestrictions.class,
                        getVerifyPeersFeePaymentClass(),
                        MakerSetsLockTime.class,
                        MakerCreateAndSignContract.class,
                        BuyerAsMakerCreatesAndSignsDepositTx.class,
                        BuyerSetupDepositTxListener.class,
                        BuyerAsMakerSendsInputsForDepositTxResponse.class).
                        using(new TradeTaskRunner(trade,
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

    protected void handle(DelayedPayoutTxSignatureRequest message, NodeAddress peer) {
        expect(phase(Trade.Phase.TAKER_FEE_PUBLISHED)
                .with(message)
                .from(peer))
                .setup(tasks(
                        MakerRemovesOpenOffer.class,
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


    @Override
    protected Class<? extends TradeTask> getVerifyPeersFeePaymentClass() {
        return MakerVerifyTakerFeePayment.class;
    }
}
