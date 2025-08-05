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

package koinz.desktop.maker;

import koinz.core.locale.CryptoCurrency;
import koinz.core.locale.FiatCurrency;
import koinz.core.locale.TradeCurrency;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;

public class TradeCurrencyMakers {

    public static final Property<TradeCurrency, String> currencyCode = new Property<>();
    public static final Property<TradeCurrency, String> currencyName = new Property<>();

    public static final Instantiator<koinz.core.locale.CryptoCurrency> CryptoCurrency = lookup ->
            new CryptoCurrency(lookup.valueOf(currencyCode, "BTC"), lookup.valueOf(currencyName, "Bitcoin"));

    public static final Instantiator<koinz.core.locale.FiatCurrency> FiatCurrency = lookup ->
            new FiatCurrency(lookup.valueOf(currencyCode, "EUR"));

    public static final CryptoCurrency bitcoin = make(a(CryptoCurrency));
    public static final FiatCurrency euro = make(a(FiatCurrency));
    public static final FiatCurrency usd = make(a(FiatCurrency).but(with(currencyCode, "USD")));
}

