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
import koinz.core.util.validation.InputValidator;

public class ChangeParamInputValidator extends InputValidator {
    private final Param param;
    private final ChangeParamValidator changeParamValidator;

    public ChangeParamInputValidator(Param param, ChangeParamValidator changeParamValidator) {
        this.changeParamValidator = changeParamValidator;
        this.param = param;
    }

    @Override
    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid)
            return validationResult;

        return validateParam(input);
    }

    private ValidationResult validateParam(String input) {
        try {
            changeParamValidator.validateParamValue(param, input);
            return new ValidationResult(true);
        } catch (Throwable e) {
            return new ValidationResult(false, e.getMessage());
        }
    }
}
