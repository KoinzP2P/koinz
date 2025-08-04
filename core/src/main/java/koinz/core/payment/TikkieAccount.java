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

import koinz.core.locale.FiatCurrency;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;
import koinz.core.payment.payload.TikkieAccountPayload;

import java.util.List;

import lombok.EqualsAndHashCode;

import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
public final class TikkieAccount extends CountryBasedPaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(new FiatCurrency("EUR"));

    public TikkieAccount() {
        super(PaymentMethod.TIKKIE);
        // this payment method is only for Netherlands/EUR
        setSingleTradeCurrency(SUPPORTED_CURRENCIES.get(0));
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new TikkieAccountPayload(paymentMethod.getId(), id);
    }

    public void setIban(String iban) {
        ((TikkieAccountPayload) paymentAccountPayload).setIban(iban);
    }

    public String getIban() {
        return ((TikkieAccountPayload) paymentAccountPayload).getIban();
    }

    public String getMessageForBuyer() {
        return "payment.tikkie.info.buyer";
    }

    public String getMessageForSeller() {
        return "payment.tikkie.info.seller";
    }

    public String getMessageForAccountCreation() {
        return "payment.tikkie.info.account";
    }

    @NotNull
    @Override
    public List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
