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
import koinz.core.payment.payload.MoneseAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public final class MoneseAccount extends PaymentAccount {

    // https://github.com/bisq-network/growth/issues/227
    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(
            new FiatCurrency("EUR"),
            new FiatCurrency("GBP"),
            new FiatCurrency("RON")
    );

    public MoneseAccount() {
        super(PaymentMethod.MONESE);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new MoneseAccountPayload(paymentMethod.getId(), id);
    }

    public void setHolderName(String accountId) {
        ((MoneseAccountPayload) paymentAccountPayload).setHolderName(accountId);
    }

    public String getHolderName() {
        return ((MoneseAccountPayload) paymentAccountPayload).getHolderName();
    }

    public void setMobileNr(String accountId) {
        ((MoneseAccountPayload) paymentAccountPayload).setMobileNr(accountId);
    }

    public String getMobileNr() {
        return ((MoneseAccountPayload) paymentAccountPayload).getMobileNr();
    }

    public String getMessageForBuyer() {
        return "payment.monese.info.buyer";
    }

    public String getMessageForSeller() {
        return "payment.monese.info.seller";
    }

    public String getMessageForAccountCreation() {
        return "payment.monese.info.account";
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
