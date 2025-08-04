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

package koinz.core.trade.protocol.bisq_v1.tasks.seller_as_taker;

import koinz.core.btc.model.InputsAndChangeOutput;
import koinz.core.offer.Offer;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.trade.protocol.bisq_v1.tasks.TradeTask;

import koinz.common.taskrunner.TaskRunner;

import org.bitcoinj.core.Coin;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class SellerAsTakerCreatesDepositTxInputs extends TradeTask {
    public SellerAsTakerCreatesDepositTxInputs(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            Coin tradeAmount = checkNotNull(trade.getAmount());
            Offer offer = checkNotNull(trade.getOffer());
            Coin txFee = trade.getTradeTxFee();
            Coin takerInputAmount = offer.getSellerSecurityDeposit()
                    .add(txFee)
                    .add(txFee) // We add 2 times the fee as one is for the payout tx
                    .add(tradeAmount);
            InputsAndChangeOutput result = processModel.getTradeWalletService().takerCreatesDepositTxInputs(
                    processModel.getTakeOfferFeeTx(),
                    takerInputAmount,
                    txFee);

            processModel.setRawTransactionInputs(result.rawTransactionInputs);
            processModel.setChangeOutputValue(result.changeOutputValue);
            processModel.setChangeOutputAddress(result.changeOutputAddress);

            processModel.getTradeManager().requestPersistence();

            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
