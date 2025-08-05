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
import koinz.core.payment.PixAccount;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PixAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;
import static koinz.desktop.util.FormBuilder.addTopLabelTextField;
import static koinz.desktop.util.FormBuilder.addTopLabelTextFieldWithCopyIcon;

public class PixForm extends PaymentMethodForm {
    private final PixAccount account;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        addTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.pix.key"),
                ((PixAccountPayload) paymentAccountPayload).getPixKey(), Layout.COMPACT_FIRST_ROW_AND_GROUP_DISTANCE);
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                paymentAccountPayload.getHolderNameOrPromptIfEmpty());
        return gridRow;
    }

    public PixForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService,
                     InputValidator inputValidator, GridPane gridPane,
                     int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.account = (PixAccount) paymentAccount;
    }

    @Override
    public void addFormForAddAccount() {
        // this payment method is only for Brazil/BRL
        account.setSingleTradeCurrency(account.getSupportedCurrencies().get(0));
        CountryUtil.findCountryByCode("BR").ifPresent(c -> account.setCountry(c));

        gridRowFrom = gridRow + 1;

        InputTextField pixKeyInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.pix.key"));
        pixKeyInputTextField.setValidator(inputValidator);
        pixKeyInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setPixKey(newValue.trim());
            updateFromInputs();
        });

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner.fullname"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            account.setHolderName(newValue);
            updateFromInputs();
        });

        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), account.getSingleTradeCurrency().getNameAndCode());
        addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.country"), account.getCountry().name);
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(account.getPixKey());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(account.getPaymentMethod().getId()));
        TextField field = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.pix.key"),
                account.getPixKey()).second;
        field.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                account.getHolderName());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), account.getSingleTradeCurrency().getNameAndCode());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.country"), account.getCountry().name);
        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(account.getHolderName()).isValid
                && inputValidator.validate(account.getPixKey()).isValid);
    }
}
