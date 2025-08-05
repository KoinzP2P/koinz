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

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.Country;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.MercadoPagoAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.MercadoPagoAccountPayload;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import koinz.common.UserThread;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javafx.collections.FXCollections;

import javafx.util.StringConverter;

import static koinz.desktop.util.FormBuilder.*;

public class MercadoPagoForm extends PaymentMethodForm {
    private final MercadoPagoAccount mercadoPagoAccount;
    ComboBox<Country> countryCombo;

    public static int addFormForBuyer(GridPane gridPane, int gridRow, PaymentAccountPayload paymentAccountPayload) {
        MercadoPagoAccountPayload mercadoPagoAccountPayload = (MercadoPagoAccountPayload) paymentAccountPayload;

        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                mercadoPagoAccountPayload.getAccountHolderName());
        addCompactTopLabelTextField(gridPane, gridRow, 1, Res.get("shared.country"),
                CountryUtil.getNameAndCode(mercadoPagoAccountPayload.getCountryCode()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.mercadoPago.holderId"),
                mercadoPagoAccountPayload.getAccountHolderId());
        addCompactTopLabelTextField(gridPane, gridRow, 1, Res.get("payment.mercadoPago.site"),
                MercadoPagoAccount.countryToMercadoPagoSite(mercadoPagoAccountPayload.getCountryCode()));
        return gridRow;
    }

    public MercadoPagoForm(PaymentAccount paymentAccount,
                              AccountAgeWitnessService accountAgeWitnessService,
                              InputValidator inputValidator,
                              GridPane gridPane,
                              int gridRow,
                              CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.mercadoPagoAccount = (MercadoPagoAccount) paymentAccount;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner.fullname"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            mercadoPagoAccount.setAccountHolderName(newValue);
            updateFromInputs();
        });

        InputTextField mobileNrInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.mercadoPago.holderId"));
        mobileNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            mercadoPagoAccount.setAccountHolderId(newValue);
            updateFromInputs();
        });

        countryCombo = addComboBox(gridPane, ++gridRow, Res.get("shared.country"));
        countryCombo.setPromptText(Res.get("payment.select.country"));
        countryCombo.setItems(FXCollections.observableArrayList(MercadoPagoAccount.getAllMercadoPagoCountries()));
        TextField ccyField = addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), "").second;
        countryCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country country) {
                return country.name + " (" + country.code + ")";
            }
            @Override
            public Country fromString(String s) {
                return null;
            }
        });
        countryCombo.setOnAction(e -> {
            Country countryCode = countryCombo.getValue();
            mercadoPagoAccount.setCountry(countryCode);
            TradeCurrency currency = CurrencyUtil.getCurrencyByCountryCode(countryCode.code);
            paymentAccount.setSingleTradeCurrency(currency);
            ccyField.setText(currency.getNameAndCode());
            updateFromInputs();
        });
        if (countryCombo.getItems().size() == 1) {  // auto select when only one choice
            UserThread.runAfter(() -> countryCombo.setValue(countryCombo.getItems().get(0)), 1);
        }

        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(mercadoPagoAccount.getAccountHolderId());
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(mercadoPagoAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"),
                mercadoPagoAccount.getAccountHolderName());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.mercadoPago.holderId"),
                mercadoPagoAccount.getAccountHolderId());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.country"),
                mercadoPagoAccount.getCountry() != null ? mercadoPagoAccount.getCountry().name : "");
        TradeCurrency singleTradeCurrency = mercadoPagoAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);
        addLimitations(true);
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && inputValidator.validate(mercadoPagoAccount.getAccountHolderId()).isValid
                && inputValidator.validate(mercadoPagoAccount.getAccountHolderName()).isValid
                && mercadoPagoAccount.getTradeCurrencies().size() > 0);
    }
}
