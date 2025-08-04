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

import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.trade.model.bisq_v1.Contract;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.messages.ShareBuyerPaymentAccountMessage;
import koinz.core.trade.protocol.bisq_v1.model.ProcessModel;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;
import koinz.core.util.JsonUtil;

import koinz.common.crypto.Hash;
import koinz.common.crypto.Sig;
import koinz.common.taskrunner.TaskRunner;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import static koinz.core.util.Validator.checkTradeId;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class SellerProcessShareBuyerPaymentAccountMessage extends TradeTask {
    public SellerProcessShareBuyerPaymentAccountMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            ShareBuyerPaymentAccountMessage message = (ShareBuyerPaymentAccountMessage) processModel.getTradeMessage();
            checkNotNull(message);
            checkTradeId(processModel.getOfferId(), message);

            PaymentAccountPayload buyerPaymentAccountPayload = message.getBuyerPaymentAccountPayload();

            byte[] buyerPaymentAccountPayloadHash = ProcessModel.hashOfPaymentAccountPayload(buyerPaymentAccountPayload);
            Contract contract = trade.getContract();
            byte[] peersPaymentAccountPayloadHash = checkNotNull(contract).getHashOfPeersPaymentAccountPayload(processModel.getPubKeyRing());
            checkArgument(Arrays.equals(buyerPaymentAccountPayloadHash, peersPaymentAccountPayloadHash),
                    "Hash of payment account is invalid");

            processModel.getTradePeer().setPaymentAccountPayload(buyerPaymentAccountPayload);
            checkNotNull(contract).setPaymentAccountPayloads(buyerPaymentAccountPayload,
                    processModel.getPaymentAccountPayload(trade),
                    processModel.getPubKeyRing());

            // As we have added the payment accounts we need to update the json. We also update the signature
            // thought that has less relevance with the changes of 1.7.0
            String contractAsJson = JsonUtil.objectToJson(contract);
            String signature = Sig.sign(processModel.getKeyRing().getSignatureKeyPair().getPrivate(), contractAsJson);
            trade.setContractAsJson(contractAsJson);
            if (contract.isBuyerMakerAndSellerTaker()) {
                trade.setTakerContractSignature(signature);
            } else {
                trade.setMakerContractSignature(signature);
            }

            byte[] contractHash = Hash.getSha256Hash(checkNotNull(contractAsJson));
            trade.setContractHash(contractHash);

            trade.setTradingPeerNodeAddress(processModel.getTempTradingPeerNodeAddress());

            processModel.getTradeManager().requestPersistence();

            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
