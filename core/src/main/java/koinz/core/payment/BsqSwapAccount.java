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

package koinz.core.payment;

import koinz.core.locale.CryptoCurrency;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.payload.BsqSwapAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

// Placeholder account for Bsq swaps. We do not hold any data here, its just used to fit into the
// standard domain. We mimic the different trade protocol as a payment method with a dedicated account.
@EqualsAndHashCode(callSuper = true)
public final class BsqSwapAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(new CryptoCurrency("KNZ", "KNZ"));

    public static final String ID = "BsqSwapAccount";

    public BsqSwapAccount() {
        super(PaymentMethod.BSQ_SWAP);
    }

    @Override
    public void init() {
        id = ID;
        creationDate = new Date().getTime();
        paymentAccountPayload = createPayload();
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new BsqSwapAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

}
