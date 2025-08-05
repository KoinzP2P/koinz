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

package koinz.desktop.main.funds.transactions;

import koinz.core.offer.Offer;
import koinz.core.offer.OpenOffer;
import koinz.core.trade.model.Tradable;

import org.bitcoinj.core.Transaction;

import java.util.stream.IntStream;

class TransactionAwareOpenOffer implements TransactionAwareTradable {
    private final OpenOffer delegate;

    TransactionAwareOpenOffer(OpenOffer delegate) {
        this.delegate = delegate;
    }

    public boolean isRelatedToTransaction(Transaction transaction) {
        Offer offer = delegate.getOffer();
        String paymentTxId = offer.getOfferFeePaymentTxId();

        return paymentTxId != null && paymentTxId.equals(transaction.getTxId().toString());
    }

    public Tradable asTradable() {
        return delegate;
    }

    @Override
    public IntStream getRelatedTransactionFilter() {
        Offer offer = delegate.getOffer();
        String paymentTxId = offer.getOfferFeePaymentTxId();
        return IntStream.of(TransactionAwareTradable.bucketIndex(paymentTxId))
                .filter(i -> i >= 0);
    }
}
