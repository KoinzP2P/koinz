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

package koinz.desktop.util.validation;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.util.validation.RegexValidator;

import koinz.common.config.BaseCurrencyNetwork;
import koinz.common.config.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InteracETransferQuestionValidatorTest {

    @BeforeEach
    public void setup() {
        final BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        final String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);
    }

    @Test
    public void validate() throws Exception {
        InteracETransferQuestionValidator validator = new InteracETransferQuestionValidator(new LengthValidator(), new RegexValidator());

        assertTrue(validator.validate("abcdefghijklmnopqrstuvwxyz").isValid);
        assertTrue(validator.validate("ABCDEFGHIJKLMNOPQRSTUVWXYZ").isValid);
        assertTrue(validator.validate("1234567890").isValid);
        assertTrue(validator.validate("' _ , . ? -").isValid);
        assertTrue(validator.validate("what is 2-1?").isValid);

        assertFalse(validator.validate(null).isValid); // null
        assertFalse(validator.validate("").isValid); // empty
        assertFalse(validator.validate("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ").isValid); // too long
        assertFalse(validator.validate("abc !@#").isValid); // invalid characters
    }

}
