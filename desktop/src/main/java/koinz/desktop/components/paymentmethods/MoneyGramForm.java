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
import koinz.desktop.util.GUIUtil;
import koinz.desktop.util.Layout;
import koinz.desktop.util.validation.EmailValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.BankUtil;
import koinz.core.locale.Country;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.Res;
import koinz.core.payment.MoneyGramAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.MoneyGramAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import koinz.common.util.Tuple2;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import lombok.extern.slf4j.Slf4j;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static koinz.desktop.util.FormBuilder.addInputTextField;
import static koinz.desktop.util.FormBuilder.addTopLabelFlowPane;

@Slf4j
public class MoneyGramForm extends PaymentMethodForm {

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        final MoneyGramAccountPayload payload = (MoneyGramAccountPayload) paymentAccountPayload;
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.account.fullName"),
                payload.getHolderName());
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.email"),
                payload.getEmail());
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow,
                Res.get("payment.bank.country"),
                CountryUtil.getNameAndCode(((MoneyGramAccountPayload) paymentAccountPayload).getCountryCode()));
        if (BankUtil.isStateRequired(payload.getCountryCode()))
            addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1,
                    Res.get("payment.account.state"),
                    payload.getState());

        return gridRow;
    }

    private final MoneyGramAccountPayload moneyGramAccountPayload;
    private InputTextField stateInputTextField;
    private final EmailValidator emailValidator;

    public MoneyGramForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, InputValidator inputValidator,
                         GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.moneyGramAccountPayload = (MoneyGramAccountPayload) paymentAccount.paymentAccountPayload;

        emailValidator = new EmailValidator();
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        final Country country = getMoneyGramPaymentAccount().getCountry();
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(paymentAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.country"), country != null ? country.name : "");
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.fullName"),
                moneyGramAccountPayload.getHolderName());
        if (BankUtil.isStateRequired(moneyGramAccountPayload.getCountryCode()))
            addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.state"),
                    moneyGramAccountPayload.getState()).second.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.email"),
                moneyGramAccountPayload.getEmail());
        addLimitations(true);
        addCurrenciesGrid(false);
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        gridRow = GUIUtil.addRegionCountry(gridPane, gridRow, this::onCountrySelected);

        InputTextField holderNameInputTextField = addInputTextField(gridPane,
                ++gridRow, Res.get("payment.account.fullName"));
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            moneyGramAccountPayload.setHolderName(newValue);
            updateFromInputs();
        });
        holderNameInputTextField.setValidator(inputValidator);

        stateInputTextField = addInputTextField(gridPane, ++gridRow, Res.get("payment.account.state"));
        stateInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            moneyGramAccountPayload.setState(newValue);
            updateFromInputs();

        });
        applyIsStateRequired();

        InputTextField emailInputTextField = addInputTextField(gridPane, ++gridRow, Res.get("payment.email"));
        emailInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            moneyGramAccountPayload.setEmail(newValue);
            updateFromInputs();
        });
        emailInputTextField.setValidator(emailValidator);

        addCurrenciesGrid(true);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();

        updateFromInputs();
    }

    private void onCountrySelected(Country country) {
        if (country != null) {
            getMoneyGramPaymentAccount().setCountry(country);
            updateFromInputs();
            applyIsStateRequired();
            stateInputTextField.setText("");
        }
    }

    private void addCurrenciesGrid(boolean isEditable) {
        final Tuple2<Label, FlowPane> labelFlowPaneTuple2 = addTopLabelFlowPane(gridPane, ++gridRow, Res.get("payment.supportedCurrencies"),
                Layout.FLOATING_LABEL_DISTANCE * 3, Layout.FLOATING_LABEL_DISTANCE * 3);

        FlowPane flowPane = labelFlowPaneTuple2.second;

        if (isEditable)
            flowPane.setId("flow-pane-checkboxes-bg");
        else
            flowPane.setId("flow-pane-checkboxes-non-editable-bg");

        paymentAccount.getSupportedCurrencies().forEach(e ->
                fillUpFlowPaneWithCurrencies(isEditable, flowPane, e, paymentAccount));
    }

    private void applyIsStateRequired() {
        final boolean stateRequired = BankUtil.isStateRequired(moneyGramAccountPayload.getCountryCode());
        stateInputTextField.setManaged(stateRequired);
        stateInputTextField.setVisible(stateRequired);
    }

    private MoneyGramAccount getMoneyGramPaymentAccount() {
        return (MoneyGramAccount) this.paymentAccount;
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(moneyGramAccountPayload.getHolderName());
    }

    @Override
    public void updateAllInputsValid() {
        boolean result = isAccountNameValid()
                && getMoneyGramPaymentAccount().getCountry() != null
                && inputValidator.validate(moneyGramAccountPayload.getHolderName()).isValid
                && emailValidator.validate(moneyGramAccountPayload.getEmail()).isValid
                && paymentAccount.getTradeCurrencies().size() > 0;
        allInputsValid.set(result);
    }
}
