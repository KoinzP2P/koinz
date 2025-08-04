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
import koinz.core.payment.payload.InteracETransferAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

import java.util.List;

import lombok.EqualsAndHashCode;

import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
public final class InteracETransferAccount extends PaymentAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = List.of(new FiatCurrency("CAD"));

    public InteracETransferAccount() {
        super(PaymentMethod.INTERAC_E_TRANSFER);
        setSingleTradeCurrency(SUPPORTED_CURRENCIES.get(0));
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new InteracETransferAccountPayload(paymentMethod.getId(), id);
    }

    @NotNull
    @Override
    public List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    public void setEmail(String email) {
        ((InteracETransferAccountPayload) paymentAccountPayload).setEmail(email);
    }

    public String getEmail() {
        return ((InteracETransferAccountPayload) paymentAccountPayload).getEmail();
    }

    public void setAnswer(String answer) {
        ((InteracETransferAccountPayload) paymentAccountPayload).setAnswer(answer);
    }

    public String getAnswer() {
        return ((InteracETransferAccountPayload) paymentAccountPayload).getAnswer();
    }

    public void setQuestion(String question) {
        ((InteracETransferAccountPayload) paymentAccountPayload).setQuestion(question);
    }

    public String getQuestion() {
        return ((InteracETransferAccountPayload) paymentAccountPayload).getQuestion();
    }

    public void setHolderName(String holderName) {
        ((InteracETransferAccountPayload) paymentAccountPayload).setHolderName(holderName);
    }

    public String getHolderName() {
        return ((InteracETransferAccountPayload) paymentAccountPayload).getHolderName();
    }
}
