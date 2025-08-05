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
import koinz.desktop.util.Layout;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.Res;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.IfscBasedAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;
import koinz.core.util.validation.RegexValidator;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static koinz.desktop.util.FormBuilder.addTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addTopLabelTextFieldWithCopyIcon;

public class IfscBankForm extends PaymentMethodForm {
    private final IfscBasedAccountPayload ifscBasedAccountPayload;
    private final RegexValidator ifscValidator;  // https://en.wikipedia.org/wiki/Indian_Financial_System_Code

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        IfscBasedAccountPayload ifscAccountPayload = (IfscBasedAccountPayload) paymentAccountPayload;
        addTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.account.owner.fullname"), ifscAccountPayload.getHolderName(), Layout.COMPACT_FIRST_ROW_AND_GROUP_DISTANCE);
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.accountNr"), ifscAccountPayload.getAccountNr());
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.ifsc"), ifscAccountPayload.getIfsc());
        return gridRow;
    }

    public IfscBankForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService,
                    InputValidator inputValidator, GridPane gridPane,
                    int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.ifscBasedAccountPayload = (IfscBasedAccountPayload) paymentAccount.paymentAccountPayload;
        ifscValidator = new RegexValidator();
        ifscValidator.setPattern("[A-Z]{4}0[0-9]{6}");
        ifscValidator.setErrorMessage(Res.get("payment.ifsc.validation"));
    }

    @Override
    public void addFormForAddAccount() {
        // this payment method is only for India/INR
        paymentAccount.setSingleTradeCurrency(paymentAccount.getSupportedCurrencies().get(0));
        CountryUtil.findCountryByCode("IN").ifPresent(c -> ifscBasedAccountPayload.setCountryCode(c.code));

        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner.fullname"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            ifscBasedAccountPayload.setHolderName(newValue.trim());
            updateFromInputs();
        });

        InputTextField accountNrInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.accountNr"));
        accountNrInputTextField.setValidator(inputValidator);
        accountNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            ifscBasedAccountPayload.setAccountNr(newValue.trim());
            updateFromInputs();
        });

        InputTextField ifscInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.ifsc"));
        ifscInputTextField.setText("XXXX0999999");
        ifscInputTextField.setValidator(ifscValidator);
        ifscInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            ifscBasedAccountPayload.setIfsc(newValue.trim());
            updateFromInputs();
        });

        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), paymentAccount.getSingleTradeCurrency().getNameAndCode());
        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.country"), CountryUtil.getNameByCode(ifscBasedAccountPayload.getCountryCode()));
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(ifscBasedAccountPayload.getHolderName());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"), Res.get(paymentAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                ifscBasedAccountPayload.getHolderName());
        TextField field = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.accountNr"), ifscBasedAccountPayload.getAccountNr()).second;
        field.setMouseTransparent(false);
        TextField fieldIfsc = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.ifsc"), ifscBasedAccountPayload.getIfsc()).second;
        fieldIfsc.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), paymentAccount.getSingleTradeCurrency().getNameAndCode());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.country"), CountryUtil.getNameByCode(ifscBasedAccountPayload.getCountryCode()));
        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(ifscBasedAccountPayload.getHolderName()).isValid
                && inputValidator.validate(ifscBasedAccountPayload.getAccountNr()).isValid
                && ifscValidator.validate(ifscBasedAccountPayload.getIfsc()).isValid);
    }
}
