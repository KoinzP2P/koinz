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

import koinz.desktop.common.model.ActivatableDataModel;
import koinz.desktop.components.paymentmethods.XmrForm;
import koinz.desktop.util.GUIUtil;

import koinz.core.locale.CryptoCurrency;
import koinz.core.locale.TradeCurrency;
import koinz.core.offer.OpenOfferManager;
import koinz.core.payment.AssetAccount;
import koinz.core.payment.CryptoCurrencyAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.trade.TradeManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;

import koinz.common.file.CorruptedStorageFileHandler;
import koinz.common.proto.persistable.PersistenceProtoResolver;

import com.google.inject.Inject;

import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

class AltCoinAccountsDataModel extends ActivatableDataModel {

    private final User user;
    private final Preferences preferences;
    private final OpenOfferManager openOfferManager;
    private final TradeManager tradeManager;
    final ObservableList<PaymentAccount> paymentAccounts = FXCollections.observableArrayList();
    private final SetChangeListener<PaymentAccount> setChangeListener;
    private final String accountsFileName = "AltcoinPaymentAccounts";
    private final PersistenceProtoResolver persistenceProtoResolver;
    private final CorruptedStorageFileHandler corruptedStorageFileHandler;

    @Inject
    public AltCoinAccountsDataModel(User user,
                                    Preferences preferences,
                                    OpenOfferManager openOfferManager,
                                    TradeManager tradeManager,
                                    PersistenceProtoResolver persistenceProtoResolver,
                                    CorruptedStorageFileHandler corruptedStorageFileHandler) {
        this.user = user;
        this.preferences = preferences;
        this.openOfferManager = openOfferManager;
        this.tradeManager = tradeManager;
        this.persistenceProtoResolver = persistenceProtoResolver;
        this.corruptedStorageFileHandler = corruptedStorageFileHandler;
        setChangeListener = change -> fillAndSortPaymentAccounts();
    }

    @Override
    protected void activate() {
        user.getPaymentAccountsAsObservable().addListener(setChangeListener);
        fillAndSortPaymentAccounts();

        paymentAccounts.stream()
                .filter(this::isXmrPaymentAccount)
                .forEach(e -> {
                    if (!xmrAccountUsesSubAddresses(e)) {
                        XmrForm.showXmrSubAddressPopup();
                    }
                });
    }

    private void fillAndSortPaymentAccounts() {
        if (user.getPaymentAccounts() != null) {
            paymentAccounts.setAll(user.getPaymentAccounts().stream()
                    .filter(paymentAccount -> paymentAccount.getPaymentMethod().isBlockchain())
                    .collect(Collectors.toList()));
            paymentAccounts.sort(Comparator.comparing(PaymentAccount::getAccountName));
        }
    }

    private boolean isXmrPaymentAccount(PaymentAccount paymentAccount) {
        TradeCurrency tradeCurrency = paymentAccount.getSingleTradeCurrency();
        return tradeCurrency != null && tradeCurrency.getCode().equals("XMR");
    }

    private boolean xmrAccountUsesSubAddresses(PaymentAccount paymentAccount) {
        if (paymentAccount instanceof CryptoCurrencyAccount) {
            CryptoCurrencyAccount account = (CryptoCurrencyAccount) paymentAccount;
            Map<String, String> extraData = account.getExtraData();
            if (extraData == null) {
                return false;
            }

            String useXMmrSubAddresses = extraData.get("UseXMmrSubAddresses");
            return useXMmrSubAddresses != null && useXMmrSubAddresses.equals("1");
        }

        return false;
    }

    @Override
    protected void deactivate() {
        user.getPaymentAccountsAsObservable().removeListener(setChangeListener);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // UI actions
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void onSaveNewAccount(PaymentAccount paymentAccount) {
        TradeCurrency singleTradeCurrency = paymentAccount.getSingleTradeCurrency();
        preferences.addCryptoCurrency((CryptoCurrency) singleTradeCurrency);
        user.addPaymentAccount(paymentAccount);
        paymentAccount.onPersistChanges();
    }

    public void onUpdateAccount(PaymentAccount paymentAccount) {
        paymentAccount.onPersistChanges();
        user.requestPersistence();
    }

    public boolean onDeleteAccount(PaymentAccount paymentAccount) {
        boolean usedInOpenOffers = openOfferManager.getObservableList().stream()
                .anyMatch(openOffer -> openOffer.getOffer().getMakerPaymentAccountId().equals(paymentAccount.getId()));

        boolean usedInTrades = tradeManager.getObservableList().stream()
                .anyMatch(trade -> trade.getOffer().getMakerPaymentAccountId().equals(paymentAccount.getId()) ||
                        paymentAccount.getId().equals(trade.getTakerPaymentAccountId()));
        boolean isPaymentAccountUsed = usedInOpenOffers || usedInTrades;

        if (!isPaymentAccountUsed) {
            user.removePaymentAccount(paymentAccount);
        }
        return isPaymentAccountUsed;
    }

    public void onSelectAccount(PaymentAccount paymentAccount) {
        user.setCurrentPaymentAccount(paymentAccount);
    }

    public void exportAccounts(Stage stage) {
        if (user.getPaymentAccounts() != null) {
            ArrayList<PaymentAccount> accounts = user.getPaymentAccounts().stream()
                    .filter(paymentAccount -> paymentAccount instanceof AssetAccount)
                    .collect(Collectors.toCollection(ArrayList::new));
            GUIUtil.exportAccounts(accounts, accountsFileName, preferences, stage, persistenceProtoResolver, corruptedStorageFileHandler);
        }
    }

    public void importAccounts(Stage stage) {
        GUIUtil.importAccounts(user, accountsFileName, preferences, stage, persistenceProtoResolver, corruptedStorageFileHandler);
    }

    public int getNumPaymentAccounts() {
        return user.getPaymentAccounts() != null ? user.getPaymentAccounts().size() : 0;
    }
}
