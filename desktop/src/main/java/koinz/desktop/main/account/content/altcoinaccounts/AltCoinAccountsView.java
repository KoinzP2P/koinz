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

package koinz.desktop.main.account.content.altcoinaccounts;

import koinz.desktop.common.view.FxmlView;
import koinz.desktop.components.AutocompleteComboBox;
import koinz.desktop.components.TitledGroupBg;
import koinz.desktop.components.paymentmethods.AssetsForm;
import koinz.desktop.components.paymentmethods.PaymentMethodForm;
import koinz.desktop.components.paymentmethods.XmrForm;
import koinz.desktop.main.account.content.PaymentAccountsView;
import koinz.desktop.main.overlays.popups.Popup;
import koinz.desktop.util.FormBuilder;
import koinz.desktop.util.Layout;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.dao.governance.asset.AssetService;
import koinz.core.filter.FilterManager;
import koinz.core.locale.CryptoCurrency;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.locale.TradeCurrency;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.PaymentAccountFactory;
import koinz.core.payment.payload.PaymentMethod;
import koinz.core.payment.validation.AltCoinAddressValidator;
import koinz.core.user.Preferences;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.InputValidator;

import koinz.asset.AltCoinAccountDisclaimer;
import koinz.asset.Asset;
import koinz.asset.coins.Monero;

import koinz.common.util.Tuple2;
import koinz.common.util.Tuple3;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.collections.ObservableList;

import javafx.util.StringConverter;

import java.util.Optional;

import static koinz.desktop.util.FormBuilder.add2ButtonsAfterGroup;
import static koinz.desktop.util.FormBuilder.add3ButtonsAfterGroup;
import static koinz.desktop.util.FormBuilder.addTitledGroupBg;
import static koinz.desktop.util.FormBuilder.addTopLabelListView;
import static koinz.desktop.util.GUIUtil.getComboBoxButtonCell;

@FxmlView
public class AltCoinAccountsView extends PaymentAccountsView<GridPane, AltCoinAccountsViewModel> {

    private final InputValidator inputValidator;
    private final AltCoinAddressValidator altCoinAddressValidator;
    private final AssetService assetService;
    private final FilterManager filterManager;
    private final CoinFormatter formatter;
    private final User user;
    private final Preferences preferences;

    private PaymentMethodForm paymentMethodForm;
    private TitledGroupBg accountTitledGroupBg;
    private Button saveNewAccountButton;
    private int gridRow = 0;
    protected ComboBox<TradeCurrency> currencyComboBox;

    @Inject
    public AltCoinAccountsView(AltCoinAccountsViewModel model,
                               InputValidator inputValidator,
                               AltCoinAddressValidator altCoinAddressValidator,
                               AccountAgeWitnessService accountAgeWitnessService,
                               AssetService assetService,
                               FilterManager filterManager,
                               @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter formatter,
                               User user,
                               Preferences preferences) {
        super(model, accountAgeWitnessService);

        this.inputValidator = inputValidator;
        this.altCoinAddressValidator = altCoinAddressValidator;
        this.assetService = assetService;
        this.filterManager = filterManager;
        this.formatter = formatter;
        this.user = user;
        this.preferences = preferences;
    }

    @Override
    protected ObservableList<PaymentAccount> getPaymentAccounts() {
        return model.getPaymentAccounts();
    }

    @Override
    protected void importAccounts() {
        model.dataModel.importAccounts((Stage) root.getScene().getWindow());
    }

    @Override
    protected void exportAccounts() {
        model.dataModel.exportAccounts((Stage) root.getScene().getWindow());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // UI actions
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void onSaveNewAccount(PaymentAccount paymentAccount) {
        TradeCurrency selectedTradeCurrency = paymentAccount.getSelectedTradeCurrency();
        if (selectedTradeCurrency != null) {
            if (selectedTradeCurrency instanceof CryptoCurrency && ((CryptoCurrency) selectedTradeCurrency).isAsset()) {
                String name = selectedTradeCurrency.getName();
                new Popup().information(Res.get("account.altcoin.popup.wallet.msg",
                        selectedTradeCurrency.getCodeAndName(),
                        name,
                        name))
                        .closeButtonText(Res.get("account.altcoin.popup.wallet.confirm"))
                        .show();
            }

            final Optional<Asset> asset = CurrencyUtil.findAsset(selectedTradeCurrency.getCode());
            if (asset.isPresent()) {
                final AltCoinAccountDisclaimer disclaimerAnnotation = asset.get().getClass().getAnnotation(AltCoinAccountDisclaimer.class);
                if (disclaimerAnnotation != null) {
                    new Popup()
                            .width(asset.get() instanceof Monero ? 1000 : 669)
                            .maxMessageLength(2500)
                            .information(Res.get(disclaimerAnnotation.value()))
                            .useIUnderstandButton()
                            .show();
                }
            }

            if (model.getPaymentAccounts().stream().noneMatch(e -> e.getAccountName() != null &&
                    e.getAccountName().equals(paymentAccount.getAccountName()))) {
                model.onSaveNewAccount(paymentAccount);
                removeNewAccountForm();
            } else {
                new Popup().warning(Res.get("shared.accountNameAlreadyUsed")).show();
            }
        }
    }

    private void onCancelNewAccount() {
        removeNewAccountForm();
    }

    private void onUpdateAccount(PaymentAccount paymentAccount) {
        model.onUpdateAccount(paymentAccount);
        removeSelectAccountForm();
    }

    private void onCancelSelectedAccount(PaymentAccount paymentAccount) {
        paymentAccount.revertChanges();
        removeSelectAccountForm();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Base form
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected void buildForm() {
        addTitledGroupBg(root, gridRow, 2, Res.get("shared.manageAccounts"));

        Tuple3<Label, ListView<PaymentAccount>, VBox> tuple = addTopLabelListView(root, gridRow, Res.get("account.altcoin.yourAltcoinAccounts"), Layout.FIRST_ROW_DISTANCE);
        paymentAccountsListView = tuple.second;
        int prefNumRows = Math.min(4, Math.max(2, model.dataModel.getNumPaymentAccounts()));
        paymentAccountsListView.setMinHeight(prefNumRows * Layout.LIST_ROW_HEIGHT + 28);
        setPaymentAccountsCellFactory();

        Tuple3<Button, Button, Button> tuple3 = add3ButtonsAfterGroup(root, ++gridRow, Res.get("shared.addNewAccount"),
                Res.get("shared.ExportAccounts"), Res.get("shared.importAccounts"));
        addAccountButton = tuple3.first;
        exportButton = tuple3.second;
        importButton = tuple3.third;
    }

    // Add new account form
    protected void addNewAccount() {
        paymentAccountsListView.getSelectionModel().clearSelection();
        TradeCurrency selectedCurrency = currencyComboBox == null ? null : currencyComboBox.getValue();
        removeAccountRows();
        addAccountButton.setDisable(true);
        accountTitledGroupBg = addTitledGroupBg(root, ++gridRow, 1, Res.get("shared.createNewAccount"), Layout.GROUP_DISTANCE);

        if (paymentMethodForm != null) {
            FormBuilder.removeRowsFromGridPane(root, 3, paymentMethodForm.getGridRow() + 1);
            GridPane.setRowSpan(accountTitledGroupBg, paymentMethodForm.getRowSpan() + 1);
        }
        gridRow = 2;
        addTradeCurrencyComboBox(root, selectedCurrency);
        paymentMethodForm = getPaymentMethodForm(PaymentMethod.BLOCK_CHAINS, selectedCurrency);
        paymentMethodForm.addFormForAddAccount();
        gridRow = paymentMethodForm.getGridRow();
        Tuple2<Button, Button> tuple2 = add2ButtonsAfterGroup(root, ++gridRow, Res.get("shared.saveNewAccount"), Res.get("shared.cancel"));
        saveNewAccountButton = tuple2.first;
        saveNewAccountButton.setOnAction(event -> onSaveNewAccount(paymentMethodForm.getPaymentAccount()));
        saveNewAccountButton.disableProperty().bind(paymentMethodForm.allInputsValidProperty().not());
        Button cancelButton = tuple2.second;
        cancelButton.setOnAction(event -> onCancelNewAccount());
        GridPane.setRowSpan(accountTitledGroupBg, paymentMethodForm.getRowSpan() + 1);
    }

    // Select account form
    protected void onSelectAccount(PaymentAccount previous, PaymentAccount current) {
        if (previous != null) {
            previous.revertChanges();
        }
        removeAccountRows();
        addAccountButton.setDisable(false);
        accountTitledGroupBg = addTitledGroupBg(root, ++gridRow, 2, Res.get("shared.selectedAccount"), Layout.GROUP_DISTANCE);
        paymentMethodForm = getPaymentMethodForm(current);
        paymentMethodForm.addFormForEditAccount();
        gridRow = paymentMethodForm.getGridRow();
        Tuple3<Button, Button, Button> tuple = add3ButtonsAfterGroup(
                root,
                ++gridRow,
                Res.get("shared.save"),
                Res.get("shared.deleteAccount"),
                Res.get("shared.cancel")
        );

        Button saveAccountButton = tuple.first;
        saveAccountButton.setOnAction(event -> onUpdateAccount(current));
        Button deleteAccountButton = tuple.second;
        deleteAccountButton.setOnAction(event -> onDeleteAccount(current));
        Button cancelButton = tuple.third;
        cancelButton.setOnAction(event -> onCancelSelectedAccount(current));
        GridPane.setRowSpan(accountTitledGroupBg, paymentMethodForm.getRowSpan());
        model.onSelectAccount(current);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Utils
    ///////////////////////////////////////////////////////////////////////////////////////////

    private PaymentMethodForm getPaymentMethodForm(PaymentMethod paymentMethod) {
        PaymentAccount paymentAccount = PaymentAccountFactory.getPaymentAccount(paymentMethod);
        paymentAccount.init();
        return getPaymentMethodForm(paymentAccount);
    }

    private PaymentMethodForm getPaymentMethodForm(PaymentMethod paymentMethod, TradeCurrency currencyCode) {
        PaymentAccount paymentAccount = PaymentAccountFactory.getPaymentAccount(paymentMethod);
        paymentAccount.init();
        paymentAccount.setSingleTradeCurrency(currencyCode);
        paymentAccount.setSelectedTradeCurrency(currencyCode);
        return getPaymentMethodForm(paymentAccount);
    }

    private PaymentMethodForm getPaymentMethodForm(PaymentAccount paymentAccount) {
        if (paymentAccount.getSelectedTradeCurrency() != null &&
                paymentAccount.getSelectedTradeCurrency().getCode() != null &&
                paymentAccount.getSelectedTradeCurrency().getCode().equalsIgnoreCase("XMR")) {
            return new XmrForm(paymentAccount, accountAgeWitnessService, altCoinAddressValidator,
                    inputValidator, root, gridRow, formatter, assetService, user);
        }
        return new AssetsForm(paymentAccount, accountAgeWitnessService, altCoinAddressValidator,
                inputValidator, root, gridRow, formatter, assetService);
    }

    private void removeNewAccountForm() {
        saveNewAccountButton.disableProperty().unbind();
        removeAccountRows();
        addAccountButton.setDisable(false);
    }

    @Override
    protected void removeSelectAccountForm() {
        FormBuilder.removeRowsFromGridPane(root, 2, gridRow);
        gridRow = 1;
        addAccountButton.setDisable(false);
        paymentAccountsListView.getSelectionModel().clearSelection();
    }

    @Override
    protected boolean deleteAccountFromModel(PaymentAccount paymentAccount) {
        return model.onDeleteAccount(paymentAccount);
    }

    private void removeAccountRows() {
        FormBuilder.removeRowsFromGridPane(root, 2, gridRow);
        gridRow = 1;
    }

    protected void addTradeCurrencyComboBox(GridPane gridPane, TradeCurrency selectedCurrency) {
        currencyComboBox = FormBuilder.<TradeCurrency>addLabelAutocompleteComboBox(gridPane, ++gridRow, Res.get("payment.altcoin"),
                Layout.FIRST_ROW_AND_GROUP_DISTANCE).second;
        currencyComboBox.setPromptText(Res.get("payment.select.altcoin"));
        currencyComboBox.setButtonCell(getComboBoxButtonCell(Res.get("payment.select.altcoin"), currencyComboBox));

        currencyComboBox.getEditor().focusedProperty().addListener(observable ->
                currencyComboBox.setPromptText(""));

        ((AutocompleteComboBox<TradeCurrency>) currencyComboBox).setAutocompleteItems(
                CurrencyUtil.getActiveSortedCryptoCurrencies(assetService, filterManager));
        currencyComboBox.setVisibleRowCount(Math.min(currencyComboBox.getItems().size(), 10));

        currencyComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TradeCurrency tradeCurrency) {
                return tradeCurrency != null ? tradeCurrency.getNameAndCode() : "";
            }

            @Override
            public TradeCurrency fromString(String s) {
                return currencyComboBox.getItems().stream().
                        filter(item -> item.getNameAndCode().equals(s)).
                        findAny().orElse(null);
            }
        });

        if (selectedCurrency != null) {
            currencyComboBox.setValue(selectedCurrency);
        }
        ((AutocompleteComboBox<?>) currencyComboBox).setOnChangeConfirmed(e -> {
            if (currencyComboBox.getValue() != null) {
                addNewAccount();
            }
        });
    }


}
