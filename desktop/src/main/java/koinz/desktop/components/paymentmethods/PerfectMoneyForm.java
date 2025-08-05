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

import koinz.desktop.util.validation.PerfectMoneyValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.locale.FiatCurrency;
import koinz.core.locale.Res;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.PerfectMoneyAccount;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.payment.payload.PerfectMoneyAccountPayload;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import javafx.scene.layout.GridPane;

import javafx.collections.FXCollections;

import static koinz.desktop.util.FormBuilder.addCompactTopLabelTextFieldWithCopyIcon;

public class PerfectMoneyForm extends GeneralAccountNumberForm {

    private final PerfectMoneyAccount perfectMoneyAccount;

    public static int addFormForBuyer(GridPane gridPane, int gridRow, PaymentAccountPayload paymentAccountPayload) {
        addCompactTopLabelTextFieldWithCopyIcon(gridPane, ++gridRow, Res.get("payment.account.no"), ((PerfectMoneyAccountPayload) paymentAccountPayload).getAccountNr());
        return gridRow;
    }

    public PerfectMoneyForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, PerfectMoneyValidator perfectMoneyValidator, InputValidator inputValidator, GridPane gridPane, int
            gridRow, CoinFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.perfectMoneyAccount = (PerfectMoneyAccount) paymentAccount;
    }

    @Override
    public void addTradeCurrency() {
        addTradeCurrencyComboBox();
        currencyComboBox.setItems(FXCollections.observableArrayList(new FiatCurrency("USD"), new FiatCurrency("EUR")));
    }

    @Override
    void setAccountNumber(String newValue) {
        perfectMoneyAccount.setAccountNr(newValue);
    }

    @Override
    String getAccountNr() {
        return perfectMoneyAccount.getAccountNr();
    }
}
