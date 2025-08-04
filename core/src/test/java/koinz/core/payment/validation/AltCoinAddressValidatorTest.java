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

package koinz.core.payment.validation;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;

import koinz.asset.AssetRegistry;

import koinz.common.config.BaseCurrencyNetwork;
import koinz.common.config.Config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AltCoinAddressValidatorTest {

    @Test
    public void test() {
        AltCoinAddressValidator validator = new AltCoinAddressValidator(new AssetRegistry());

        BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);

        validator.setCurrencyCode("BTC");
        assertTrue(validator.validate("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem").isValid);

        validator.setCurrencyCode("LTC");
        assertTrue(validator.validate("Lg3PX8wRWmApFCoCMAsPF5P9dPHYQHEWKW").isValid);

        validator.setCurrencyCode("BOGUS");

        assertFalse(validator.validate("1BOGUSADDR").isValid);
    }
}
