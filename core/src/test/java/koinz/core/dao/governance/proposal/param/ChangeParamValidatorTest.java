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

package koinz.core.dao.governance.proposal.param;

import koinz.core.dao.governance.param.Param;
import koinz.core.locale.Res;
import koinz.core.util.coin.BsqFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ChangeParamValidatorTest {
    @BeforeEach
    public void setup() {
        Res.setup();
    }

    @Test
    public void testGetChangeValidationResult() throws ParamValidationException {
        ChangeParamValidator changeParamValidator = new ChangeParamValidator(null, null, new BsqFormatter());
        try {
            changeParamValidator.validationChange(0, 0, 2, 2, Param.UNDEFINED);
            fail();
        } catch (ParamValidationException e) {
            assertEquals(e.getError(), ParamValidationException.ERROR.SAME);
        }

        try {
            changeParamValidator.validationChange(0, 1, 2, 2, Param.UNDEFINED);
            fail();
        } catch (ParamValidationException e) {
            assertEquals(e.getError(), ParamValidationException.ERROR.NO_CHANGE_POSSIBLE);
        }

        try {
            changeParamValidator.validationChange(0, -1, 2, 2, Param.UNDEFINED);
            fail();
        } catch (ParamValidationException e) {
            assertEquals(e.getError(), ParamValidationException.ERROR.NO_CHANGE_POSSIBLE);
        }

        try {
            changeParamValidator.validationChange(2, 4, 2, 1.1, Param.UNDEFINED);
            fail();
        } catch (ParamValidationException e) {
            assertEquals(e.getError(), ParamValidationException.ERROR.TOO_HIGH);
        }

        try {
            changeParamValidator.validationChange(4, 2, 1.5, 2, Param.UNDEFINED);
            fail();
        } catch (ParamValidationException e) {
            assertEquals(e.getError(), ParamValidationException.ERROR.TOO_LOW);
        }

        changeParamValidator.validationChange(4, 2, 2, 2, Param.UNDEFINED);
        changeParamValidator.validationChange(2, 4, 2, 2, Param.UNDEFINED);
        changeParamValidator.validationChange(0, 1, 0, 0, Param.UNDEFINED);
        changeParamValidator.validationChange(0, -1, 0, 0, Param.UNDEFINED);
        changeParamValidator.validationChange(-1, 0, 0, 0, Param.UNDEFINED);
        changeParamValidator.validationChange(1, 0, 0, 0, Param.UNDEFINED);
    }
}
