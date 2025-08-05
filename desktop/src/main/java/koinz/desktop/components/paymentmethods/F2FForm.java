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
import koinz.desktop.util.validation.F2FValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.Country;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.FiatCurrency;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.offer.Offer;
import koinz.core.payment.CountryBasedPaymentAccount;
import koinz.core.payment.F2FAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.F2FAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import koinz.common.util.Tuple2;

import com.jfoenix.controls.JFXTextArea;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import static koinz.desktop.util.FormBuilder.*;

public class F2FForm extends PaymentMethodForm {
    private final F2FAccount f2fAccount;
    private final F2FValidator f2fValidator;
    private Country selectedCountry;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload, Offer offer, double top) {
        F2FAccountPayload f2fAccountPayload = (F2FAccountPayload) paymentAccountPayload;
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, 0, Res.get("shared.country"),
                CountryUtil.getNameAndCode(f2fAccountPayload.getCountryCode()), top);
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, gridRow, 1, Res.get("payment.f2f.city"),
                offer.getF2FCity(), top);
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.f2f.contact"),
                f2fAccountPayload.getContact());
        TextArea textArea = addTopLabelTextArea(gridPane, gridRow, 1, Res.get("payment.shared.extraInfo"), "").second;
        textArea.setMinHeight(70);
        textArea.setMaxHeight(70);
        textArea.setEditable(false);
        textArea.setId("text-area-disabled");
        textArea.setText(offer.getExtraInfo());
        return gridRow;
    }

    public F2FForm(PaymentAccount paymentAccount,
                   AccountAgeWitnessService accountAgeWitnessService, F2FValidator f2fValidator,
                   InputValidator inputValidator, GridPane gridPane, int gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);

        this.f2fAccount = (F2FAccount) paymentAccount;
        this.f2fValidator = f2fValidator;
    }


    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        Tuple2<ComboBox<TradeCurrency>, Integer> tuple = GUIUtil.addRegionCountryTradeCurrencyComboBoxes(gridPane, gridRow, this::onCountrySelected, this::onTradeCurrencySelected);
        currencyComboBox = tuple.first;
        gridRow = tuple.second;

        InputTextField contactInputTextField = addInputTextField(gridPane, ++gridRow,
                Res.get("payment.f2f.contact"));
        contactInputTextField.setPromptText(Res.get("payment.f2f.contact.prompt"));
        contactInputTextField.setValidator(f2fValidator);
        contactInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            f2fAccount.setContact(newValue);
            updateFromInputs();
        });

        InputTextField cityInputTextField = addInputTextField(gridPane, ++gridRow,
                Res.get("payment.f2f.city"));
        cityInputTextField.setPromptText(Res.get("payment.f2f.city.prompt"));
        cityInputTextField.setValidator(f2fValidator);
        cityInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            f2fAccount.setCity(newValue);
            updateFromInputs();
        });

        TextArea extraTextArea = addTopLabelTextArea(gridPane, ++gridRow,
                Res.get("payment.shared.optionalExtra"), Res.get("payment.shared.extraInfo.prompt")).second;
        extraTextArea.setMinHeight(70);
        ((JFXTextArea) extraTextArea).setLabelFloat(false);
        //extraTextArea.setValidator(f2fValidator);
        extraTextArea.textProperty().addListener((ov, oldValue, newValue) -> {
            f2fAccount.setExtraInfo(newValue);
            updateFromInputs();
        });

        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    private void onCountrySelected(Country country) {
        selectedCountry = country;
        if (selectedCountry != null) {
            getCountryBasedPaymentAccount().setCountry(selectedCountry);
            String countryCode = selectedCountry.code;
            TradeCurrency currency = CurrencyUtil.getCurrencyByCountryCode(countryCode);
            paymentAccount.setSingleTradeCurrency(currency);
            currencyComboBox.setDisable(false);
            currencyComboBox.getSelectionModel().select(currency);

            updateFromInputs();
        }
    }

    private void onTradeCurrencySelected(TradeCurrency tradeCurrency) {
        FiatCurrency defaultCurrency = CurrencyUtil.getCurrencyByCountryCode(selectedCountry.code);
        applyTradeCurrency(tradeCurrency, defaultCurrency);
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(f2fAccount.getCity());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;

        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(paymentAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.country"),
                getCountryBasedPaymentAccount().getCountry() != null ? getCountryBasedPaymentAccount().getCountry().name : "");
        TradeCurrency singleTradeCurrency = paymentAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.f2f.contact", f2fAccount.getContact()),
                f2fAccount.getContact());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.f2f.city", f2fAccount.getCity()),
                f2fAccount.getCity());
        TextArea textArea = addCompactTopLabelTextArea(gridPane, ++gridRow, Res.get("payment.shared.extraInfo"), "").second;
        textArea.setText(f2fAccount.getExtraInfo());
        textArea.setMinHeight(70);
        textArea.setEditable(false);

        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && f2fValidator.validate(f2fAccount.getContact()).isValid
                && f2fValidator.validate(f2fAccount.getCity()).isValid
                && f2fAccount.getTradeCurrencies().size() > 0);
    }

    private CountryBasedPaymentAccount getCountryBasedPaymentAccount() {
        return (CountryBasedPaymentAccount) this.paymentAccount;
    }
}
