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

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.payload.CryptoCurrencyAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public final class CryptoCurrencyAccount extends AssetAccount {

    public static final List<TradeCurrency> SUPPORTED_CURRENCIES = new ArrayList<>(CurrencyUtil.getAllSortedCryptoCurrencies());

    public CryptoCurrencyAccount() {
        super(PaymentMethod.BLOCK_CHAINS);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new CryptoCurrencyAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }

    public PaymentAccountPayload getPaymentAccountPayload() {
        return paymentAccountPayload;
    }
}
