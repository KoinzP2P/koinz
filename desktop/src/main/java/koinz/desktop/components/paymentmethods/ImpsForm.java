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

package koinz.desktop.components.paymentmethods;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import javafx.scene.layout.GridPane;

public class ImpsForm extends IfscBankForm {

    public static int addFormForBuyer(GridPane gridPane, int gridRow, PaymentAccountPayload paymentAccountPayload) {
        return IfscBankForm.addFormForBuyer(gridPane, gridRow, paymentAccountPayload);
    }

    public ImpsForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, InputValidator inputValidator,
                    GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
    }
}
