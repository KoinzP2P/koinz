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
import koinz.desktop.util.normalization.IBANNormalizer;
import koinz.desktop.util.validation.BICValidator;
import koinz.desktop.util.validation.SepaIBANValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.Country;
import koinz.core.locale.CountryUtil;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.SepaInstantAccount;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.SepaInstantAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;

import javafx.collections.FXCollections;

import java.util.List;
import java.util.Optional;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextField;

public class SepaInstantForm extends GeneralSepaForm {

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload, String amount) {
        SepaInstantAccountPayload sepaInstantAccountPayload = (SepaInstantAccountPayload) paymentAccountPayload;
        return GeneralSepaForm.addFormForBuyer(gridPane, gridRow, amount,
                sepaInstantAccountPayload.getCountryCode(),
                sepaInstantAccountPayload.getHolderName(),
                sepaInstantAccountPayload.getBic(),
                sepaInstantAccountPayload.getIban());
    }

    private final SepaInstantAccount sepaInstantAccount;
    private final SepaIBANValidator sepaIBANValidator;
    private final BICValidator bicValidator;

    public SepaInstantForm(PaymentAccount paymentAccount,
                           AccountAgeWitnessService accountAgeWitnessService,
                           BICValidator bicValidator,
                           InputValidator inputValidator,
                           GridPane gridPane,
                           int gridRow,
                           CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.sepaInstantAccount = (SepaInstantAccount) paymentAccount;
        this.sepaIBANValidator = new SepaIBANValidator();
        this.bicValidator = bicValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner.fullname"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            sepaInstantAccount.setHolderName(newValue);
            updateFromInputs();
        });

        InputTextField ibanInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, IBAN);
        ibanInputTextField.setTextFormatter(new TextFormatter<>(new IBANNormalizer()));
        ibanInputTextField.setValidator(sepaIBANValidator);

        InputTextField bicInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, BIC);
        bicInputTextField.setValidator(bicValidator);
        bicInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            sepaInstantAccount.setBic(newValue);
            updateFromInputs();

        });

        ComboBox<Country> countryComboBox = addCountrySelection();

        setCountryComboBoxAction(countryComboBox, sepaInstantAccount);

        addCountriesGrid(Res.get("payment.accept.euro"), CountryUtil.getAllSepaEuroCountries());
        addCountriesGrid(Res.get("payment.accept.nonEuro"), CountryUtil.getAllSepaNonEuroCountries());
        addLimitations(false);
        addAccountNameTextFieldWithAutoFillToggleButton();

        countryComboBox.setItems(FXCollections.observableArrayList(CountryUtil.getAllSepaInstantCountries()));
        Country country = CountryUtil.getDefaultCountry();
        if (CountryUtil.getAllSepaInstantCountries().contains(country)) {
            countryComboBox.getSelectionModel().select(country);
            sepaInstantAccount.setCountry(country);
        }

        ibanInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            sepaInstantAccount.setIban(newValue);
            updateFromInputs();

            if (ibanInputTextField.validate()) {
                List<Country> countries = CountryUtil.getAllSepaCountries();
                String ibanCountryCode = newValue.substring(0, 2).toUpperCase();
                Optional<Country> ibanCountry = countries
                        .stream()
                        .filter(c -> c.code.equals(ibanCountryCode))
                        .findFirst();

                if (ibanCountry.isPresent()) {
                    countryComboBox.setValue(ibanCountry.get());
                }
            }
        });

        countryComboBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            sepaIBANValidator.setRestrictToCountry(newValue.code);
            ibanInputTextField.refreshValidation();
        });

        updateFromInputs();
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && bicValidator.validate(sepaInstantAccount.getBic()).isValid
                && sepaIBANValidator.validate(sepaInstantAccount.getIban()).isValid
                && inputValidator.validate(sepaInstantAccount.getHolderName()).isValid
                && sepaInstantAccount.getAcceptedCountryCodes().size() > 0
                && sepaInstantAccount.getSingleTradeCurrency() != null
                && sepaInstantAccount.getCountry() != null);
    }

    @Override
    public void addFormForEditAccount() {
        gridRowFrom = gridRow;
        addAccountNameTextFieldWithAutoFillToggleButton();
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(sepaInstantAccount.getPaymentMethod().getId()));
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner.fullname"), sepaInstantAccount.getHolderName());
        addCompactTopLabelTextField(gridPane, ++gridRow, IBAN, sepaInstantAccount.getIban()).second.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, BIC, sepaInstantAccount.getBic()).second.setMouseTransparent(false);
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("payment.bank.country"),
                sepaInstantAccount.getCountry() != null ? sepaInstantAccount.getCountry().name : "");
        TradeCurrency singleTradeCurrency = sepaInstantAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"), nameAndCode);

        addCountriesGrid(Res.get("payment.accept.euro"), CountryUtil.getAllSepaEuroCountries());
        addCountriesGrid(Res.get("payment.accept.nonEuro"), CountryUtil.getAllSepaNonEuroCountries());
        addLimitations(true);
    }

    @Override
    void removeAcceptedCountry(String countryCode) {
        sepaInstantAccount.removeAcceptedCountry(countryCode);
    }

    @Override
    void addAcceptedCountry(String countryCode) {
        sepaInstantAccount.addAcceptedCountry(countryCode);
    }

    @Override
    boolean isCountryAccepted(String countryCode) {
        return sepaInstantAccount.getAcceptedCountryCodes().contains(countryCode);
    }

    @Override
    protected String getIban() {
        return sepaInstantAccount.getIban();
    }
}
