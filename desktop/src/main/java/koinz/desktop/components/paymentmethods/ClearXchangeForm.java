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

import koinz.desktop.components.InputTextField;
import koinz.desktop.util.FormBuilder;
import koinz.desktop.util.validation.ClearXchangeValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.ClearXchangeAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.ClearXchangeAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static koinz.desktop.util.FormBuilder.addTopLabelTextField;

public class ClearXchangeForm extends PaymentMethodForm {
    private final ClearXchangeAccount clearXchangeAccount;
    private final ClearXchangeValidator clearXchangeValidator;

    public static int addFormForBuyer(GridPane gridPane, int gridRow, PaymentAccountPayload paymentAccountPayload) {
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                ((ClearXchangeAccountPayload) paymentAccountPayload).getHolderName());
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.email.mobile"),
                ((ClearXchangeAccountPayload) paymentAccountPayload).getEmailOrMobileNr());
        return gridRow;
    }

    public ClearXchangeForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, ClearXchangeValidator clearXchangeValidator, InputValidator inputValidator, GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.clearXchangeAccount = (ClearXchangeAccount) paymentAccount;
        this.clearXchangeValidator = clearXchangeValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner.fullname"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            clearXchangeAccount.setHolderName(newValue.trim());
            updateFromInputs();
        });

        InputTextField mobileNrInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.email.mobile"));
        mobileNrInputTextField.setValidator(clearXchangeValidator);
        mobileNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            clearXchangeAccount.setEmailOrMobileNr(newValue.trim());
            updateFromInputs();
        });
        final TradeCurrency singleTradeCurrency = clearXchangeAccount.getSingleTradeCurrency();
        final String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "";
        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"),
                nameAndCode);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(clearXchangeAccount.getEmailOrMobileNr());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(clearXchangeAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                clearXchangeAccount.getHolderName());
        TextField field = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.email.mobile"),
                clearXchangeAccount.getEmailOrMobileNr()).second;
        field.setMouseTransparent(false);
        final TradeCurrency singleTradeCurrency = clearXchangeAccount.getSingleTradeCurrency();
        final String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"),
                nameAndCode);
        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && clearXchangeValidator.validate(clearXchangeAccount.getEmailOrMobileNr()).isValid
                && inputValidator.validate(clearXchangeAccount.getHolderName()).isValid
                && clearXchangeAccount.getTradeCurrencies().size() > 0);
    }
}
