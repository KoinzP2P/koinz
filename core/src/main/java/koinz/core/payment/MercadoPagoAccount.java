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

import koinz.core.locale.Country;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.payload.MercadoPagoAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;

public final class MercadoPagoAccount extends CountryBasedPaymentAccount {
    private static final String[] SUPPORTED_COUNTRIES = {"AR"};
        // other countries can be added later: "BR", "CL", "CO", "MX", "PE", "UY"

    private static final String[] MERCADO_PAGO_SITES = {
        "https://www.mercadopago.com.ar/"
        // shown when user is prompted to make payment.
        // other country specific sites can be added, see https://github.com/bisq-network/growth/issues/278
    };

    public static String countryToMercadoPagoSite(String countryCode) {
        int index = Arrays.stream(SUPPORTED_COUNTRIES).collect(Collectors.toList()).indexOf(countryCode);
        return index >= 0 ? MERCADO_PAGO_SITES[index] : Res.get("payment.ask");
    }

    public static List<Country> getAllMercadoPagoCountries() {
        return Arrays.stream(SUPPORTED_COUNTRIES)
                .map(CountryUtil::findCountryByCode)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public static List<TradeCurrency> SUPPORTED_CURRENCIES() {
        return Arrays.stream(SUPPORTED_COUNTRIES)
                .map(CurrencyUtil::getCurrencyByCountryCode)
                .collect(Collectors.toList());
    }

    public MercadoPagoAccount() {
        super(PaymentMethod.MERCADO_PAGO);
    }

    @Override
    protected PaymentAccountPayload createPayload() {
        return new MercadoPagoAccountPayload(paymentMethod.getId(), id);
    }

    @Override
    public @NonNull List<TradeCurrency> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES();
    }

    public String getMessageForBuyer() {
        return "payment.generic.info.buyer";
    }

    public String getMessageForSeller() {
        return "payment.generic.info.seller";
    }

    public String getMessageForAccountCreation() {
        return "payment.mercadoPago.info.account";
    }

    public String getAccountHolderId() {
        return ((MercadoPagoAccountPayload) paymentAccountPayload).getAccountHolderId();
    }

    public void setAccountHolderId(String id) {
        if (id == null) id = "";
        ((MercadoPagoAccountPayload) paymentAccountPayload).setAccountHolderId(id);
    }

    public String getAccountHolderName() {
        return ((MercadoPagoAccountPayload) paymentAccountPayload).getAccountHolderName();
    }

    public void setAccountHolderName(String accountHolderName) {
        if (accountHolderName == null) accountHolderName = "";
        ((MercadoPagoAccountPayload) paymentAccountPayload).setAccountHolderName(accountHolderName);
    }
}
