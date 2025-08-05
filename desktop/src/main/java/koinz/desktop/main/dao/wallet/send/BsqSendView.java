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

package koinz.desktop.main.dao.wallet.send;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.components.InputTextField;
import koinz.desktop.components.TitledGroupBg;
import koinz.desktop.main.MainView;
import koinz.desktop.main.dao.wallet.BsqBalanceUtil;
import koinz.desktop.main.funds.FundsView;
import koinz.desktop.main.funds.deposit.DepositView;
import koinz.desktop.main.overlays.popups.Popup;
import koinz.desktop.main.overlays.windows.TxDetailsBsq;
import koinz.desktop.main.overlays.windows.TxInputSelectionWindow;
import koinz.desktop.main.overlays.windows.WalletPasswordWindow;
import koinz.desktop.util.FormBuilder;
import koinz.desktop.util.GUIUtil;
import koinz.desktop.util.Layout;
import koinz.desktop.util.validation.BsqAddressValidator;
import koinz.desktop.util.validation.BsqValidator;
import koinz.desktop.util.validation.BtcValidator;

import koinz.core.btc.exceptions.BsqChangeBelowDustException;
import koinz.core.btc.exceptions.TxBroadcastException;
import koinz.core.btc.listeners.BsqBalanceListener;
import koinz.core.btc.setup.WalletsSetup;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.Restrictions;
import koinz.core.btc.wallet.TxBroadcaster;
import koinz.core.btc.wallet.WalletsManager;
import koinz.core.dao.state.model.blockchain.TxType;
import koinz.core.locale.Res;
import koinz.core.monetary.Volume;
import koinz.core.user.DontShowAgainLookup;
import koinz.core.util.FormattingUtils;
import koinz.core.util.ParsingUtils;
import koinz.core.util.VolumeUtil;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.coin.CoinUtil;
import koinz.core.util.validation.BtcAddressValidator;

import koinz.network.p2p.P2PService;

import koinz.common.UserThread;
import koinz.common.crypto.Hash;
import koinz.common.handlers.ResultHandler;
import koinz.common.util.Hex;
import koinz.common.util.Tuple2;
import koinz.common.util.Tuple3;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.beans.value.ChangeListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import static koinz.desktop.util.FormBuilder.addInputTextField;
import static koinz.desktop.util.FormBuilder.addTitledGroupBg;
import static koinz.desktop.util.FormBuilder.addTopLabelTextField;

@FxmlView
public class BsqSendView extends ActivatableView<GridPane, Void> implements BsqBalanceListener {
    private final BsqWalletService bsqWalletService;
    private final BtcWalletService btcWalletService;
    private final WalletsManager walletsManager;
    private final WalletsSetup walletsSetup;
    private final P2PService p2PService;
    private final BsqFormatter bsqFormatter;
    private final CoinFormatter btcFormatter;
    private final Navigation navigation;
    private final BsqBalanceUtil bsqBalanceUtil;
    private final BsqValidator bsqValidator;
    private final BtcValidator btcValidator;
    private final BsqAddressValidator bsqAddressValidator;
    private final BtcAddressValidator btcAddressValidator;
    private final WalletPasswordWindow walletPasswordWindow;

    private int gridRow = 0;
    private InputTextField amountInputTextField, btcAmountInputTextField, preImageTextField;
    private TextField opReturnDataAsHexTextField;
    private VBox opReturnDataAsHexBox;
    private Label opReturnDataAsHexLabel;
    private Button sendBsqButton, sendBtcButton, bsqInputControlButton, btcInputControlButton, btcOpReturnButton;
    private InputTextField receiversAddressInputTextField, receiversBtcAddressInputTextField;
    private ChangeListener<Boolean> focusOutListener;
    private TitledGroupBg btcTitledGroupBg;
    private ChangeListener<String> inputTextFieldListener, preImageInputTextFieldListener;
    @Nullable
    private Set<TransactionOutput> bsqUtxoCandidates;
    @Nullable
    private Set<TransactionOutput> btcUtxoCandidates;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private BsqSendView(BsqWalletService bsqWalletService,
                        BtcWalletService btcWalletService,
                        WalletsManager walletsManager,
                        WalletsSetup walletsSetup,
                        P2PService p2PService,
                        BsqFormatter bsqFormatter,
                        @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                        Navigation navigation,
                        BsqBalanceUtil bsqBalanceUtil,
                        BsqValidator bsqValidator,
                        BtcValidator btcValidator,
                        BsqAddressValidator bsqAddressValidator,
                        BtcAddressValidator btcAddressValidator,
                        WalletPasswordWindow walletPasswordWindow) {
        this.bsqWalletService = bsqWalletService;
        this.btcWalletService = btcWalletService;
        this.walletsManager = walletsManager;
        this.walletsSetup = walletsSetup;
        this.p2PService = p2PService;
        this.bsqFormatter = bsqFormatter;
        this.btcFormatter = btcFormatter;
        this.navigation = navigation;
        this.bsqBalanceUtil = bsqBalanceUtil;
        this.bsqValidator = bsqValidator;
        this.btcValidator = btcValidator;
        this.bsqAddressValidator = bsqAddressValidator;
        this.btcAddressValidator = btcAddressValidator;
        this.walletPasswordWindow = walletPasswordWindow;
    }

    @Override
    public void initialize() {
        gridRow = bsqBalanceUtil.addGroup(root, gridRow);

        addSendBsqGroup();
        addSendBtcGroup();

        focusOutListener = (observable, oldValue, newValue) -> {
            if (!newValue)
                onUpdateBalances();
        };
        inputTextFieldListener = (observable, oldValue, newValue) -> onUpdateBalances();

        preImageInputTextFieldListener = (observable, oldValue, newValue) -> opReturnDataAsHexTextField.setText(getOpReturnDataAsHexFromPreImage(newValue));

        setSendBtcGroupVisibleState(false);
    }

    @Override
    protected void activate() {
        setSendBtcGroupVisibleState(false);
        bsqBalanceUtil.activate();

        receiversAddressInputTextField.resetValidation();
        amountInputTextField.resetValidation();
        receiversBtcAddressInputTextField.resetValidation();
        btcAmountInputTextField.resetValidation();

        sendBsqButton.setOnAction((event) -> onSendBsq());
        bsqInputControlButton.setOnAction((event) -> onBsqInputControl());
        sendBtcButton.setOnAction((event) -> onSendBtc());
        btcInputControlButton.setOnAction((event) -> onBtcInputControl());
        btcOpReturnButton.setOnAction((event) -> onShowPreImageField());

        receiversAddressInputTextField.focusedProperty().addListener(focusOutListener);
        amountInputTextField.focusedProperty().addListener(focusOutListener);
        receiversBtcAddressInputTextField.focusedProperty().addListener(focusOutListener);
        btcAmountInputTextField.focusedProperty().addListener(focusOutListener);

        receiversAddressInputTextField.textProperty().addListener(inputTextFieldListener);
        amountInputTextField.textProperty().addListener(inputTextFieldListener);
        receiversBtcAddressInputTextField.textProperty().addListener(inputTextFieldListener);
        btcAmountInputTextField.textProperty().addListener(inputTextFieldListener);
        preImageTextField.textProperty().addListener(preImageInputTextFieldListener);

        bsqWalletService.addBsqBalanceListener(this);

        // We reset the input selection at activate to have all inputs selected, otherwise the user
        // might get confused if he had deselected inputs earlier and cannot spend the full balance.
        bsqUtxoCandidates = null;
        btcUtxoCandidates = null;

        onUpdateBalances();
    }

    private void onUpdateBalances() {
        onUpdateBalances(getSpendableBsqBalance(),
                bsqWalletService.getAvailableNonBsqBalance(),
                bsqWalletService.getUnverifiedBalance(),
                bsqWalletService.getUnconfirmedChangeBalance(),
                bsqWalletService.getLockedForVotingBalance(),
                bsqWalletService.getLockupBondsBalance(),
                bsqWalletService.getUnlockingBondsBalance());
    }


    @Override
    protected void deactivate() {
        bsqBalanceUtil.deactivate();

        receiversAddressInputTextField.focusedProperty().removeListener(focusOutListener);
        amountInputTextField.focusedProperty().removeListener(focusOutListener);
        receiversBtcAddressInputTextField.focusedProperty().removeListener(focusOutListener);
        btcAmountInputTextField.focusedProperty().removeListener(focusOutListener);

        receiversAddressInputTextField.textProperty().removeListener(inputTextFieldListener);
        amountInputTextField.textProperty().removeListener(inputTextFieldListener);
        receiversBtcAddressInputTextField.textProperty().removeListener(inputTextFieldListener);
        btcAmountInputTextField.textProperty().removeListener(inputTextFieldListener);
        preImageTextField.textProperty().removeListener(preImageInputTextFieldListener);

        bsqWalletService.removeBsqBalanceListener(this);

        sendBsqButton.setOnAction(null);
        btcInputControlButton.setOnAction(null);
        btcOpReturnButton.setOnAction(null);
        sendBtcButton.setOnAction(null);
        bsqInputControlButton.setOnAction(null);
    }

    @Override
    public void onUpdateBalances(Coin availableBalance,
                                 Coin availableNonBsqBalance,
                                 Coin unverifiedBalance,
                                 Coin unconfirmedChangeBalance,
                                 Coin lockedForVotingBalance,
                                 Coin lockupBondsBalance,
                                 Coin unlockingBondsBalance) {
        updateBsqValidator(availableBalance);
        updateBtcValidator(availableNonBsqBalance);

        setSendBtcGroupVisibleState(availableNonBsqBalance.isPositive());
    }

    public void fillFromTradeData(Tuple2<Volume, String> tuple) {
        amountInputTextField.setText(VolumeUtil.formatVolume(tuple.first));
        receiversAddressInputTextField.setText(tuple.second);
    }

    private void updateBsqValidator(Coin availableBalance) {
        bsqValidator.setAvailableBalance(availableBalance);
        boolean isValid = bsqAddressValidator.validate(receiversAddressInputTextField.getText()).isValid &&
                bsqValidator.validate(amountInputTextField.getText()).isValid;
        sendBsqButton.setDisable(!isValid);
    }

    private void updateBtcValidator(Coin availableBalance) {
        btcValidator.setMaxValue(availableBalance);
        boolean isValid = btcAddressValidator.validate(receiversBtcAddressInputTextField.getText()).isValid &&
                btcValidator.validate(btcAmountInputTextField.getText()).isValid;
        sendBtcButton.setDisable(!isValid);
    }

    private void addSendBsqGroup() {
        TitledGroupBg titledGroupBg = addTitledGroupBg(root, ++gridRow, 2, Res.get("dao.wallet.send.sendFunds"), Layout.GROUP_DISTANCE);
        GridPane.setColumnSpan(titledGroupBg, 3);

        receiversAddressInputTextField = addInputTextField(root, gridRow,
                Res.get("dao.wallet.send.receiverAddress"), Layout.FIRST_ROW_AND_GROUP_DISTANCE);
        receiversAddressInputTextField.setValidator(bsqAddressValidator);
        GridPane.setColumnSpan(receiversAddressInputTextField, 3);

        amountInputTextField = addInputTextField(root, ++gridRow, Res.get("dao.wallet.send.setAmount", bsqFormatter.formatCoinWithCode(Restrictions.getMinNonDustOutput())));
        amountInputTextField.setValidator(bsqValidator);
        GridPane.setColumnSpan(amountInputTextField, 3);

        focusOutListener = (observable, oldValue, newValue) -> {
            if (!newValue)
                onUpdateBalances();
        };

        Tuple2<Button, Button> tuple = FormBuilder.add2ButtonsAfterGroup(root, ++gridRow,
                Res.get("dao.wallet.send.send"), Res.get("dao.wallet.send.inputControl"));
        sendBsqButton = tuple.first;
        bsqInputControlButton = tuple.second;
    }

    private void onSendBsq() {
        if (!GUIUtil.isReadyForTxBroadcastOrShowPopup(p2PService, walletsSetup)) {
            return;
        }

        String receiversAddressString = bsqFormatter.getAddressFromBsqAddress(receiversAddressInputTextField.getText()).toString();
        Coin receiverAmount = ParsingUtils.parseToCoin(amountInputTextField.getText(), bsqFormatter);
        try {
            Transaction preparedSendTx = bsqWalletService.getPreparedSendBsqTx(receiversAddressString,
                    receiverAmount, bsqUtxoCandidates);
            Transaction txWithBtcFee = btcWalletService.completePreparedSendBsqTx(preparedSendTx);
            Transaction signedTx = bsqWalletService.signTxAndVerifyNoDustOutputs(txWithBtcFee);
            Coin miningFee = signedTx.getFee();
            int txVsize = signedTx.getVsize();
            showPublishTxPopup(receiverAmount,
                    txWithBtcFee,
                    TxType.TRANSFER_BSQ,
                    miningFee,
                    txVsize,
                    receiversAddressInputTextField.getText(),
                    bsqFormatter,
                    btcFormatter,
                    () -> {
                        receiversAddressInputTextField.setText("");
                        amountInputTextField.setText("");

                        receiversAddressInputTextField.resetValidation();
                        amountInputTextField.resetValidation();
                    });
        } catch (BsqChangeBelowDustException e) {
            String msg = Res.get("popup.warning.bsqChangeBelowDustException", bsqFormatter.formatCoinWithCode(e.getOutputValue()));
            new Popup().warning(msg).show();
        } catch (Throwable t) {
            handleError(t);
        }
    }

    private void onBsqInputControl() {
        List<TransactionOutput> unspentTransactionOutputs = bsqWalletService.getSpendableBsqTransactionOutputs();
        if (bsqUtxoCandidates == null) {
            bsqUtxoCandidates = new HashSet<>(unspentTransactionOutputs);
        } else {
            // If we had some selection stored we need to update to already spent entries
            bsqUtxoCandidates = bsqUtxoCandidates.stream().
                    filter(e -> unspentTransactionOutputs.contains(e)).
                    collect(Collectors.toSet());
        }
        TxInputSelectionWindow txInputSelectionWindow = new TxInputSelectionWindow(unspentTransactionOutputs,
                bsqUtxoCandidates,
                bsqFormatter);
        txInputSelectionWindow.onAction(() -> setBsqUtxoCandidates(txInputSelectionWindow.getCandidates()))
                .show();
    }

    private void setBsqUtxoCandidates(Set<TransactionOutput> candidates) {
        this.bsqUtxoCandidates = candidates;
        updateBsqValidator(getSpendableBsqBalance());
        amountInputTextField.refreshValidation();
    }

    // We have used input selection it is the sum of our selected inputs, otherwise the availableBalance
    private Coin getSpendableBsqBalance() {
        return bsqUtxoCandidates != null ?
                Coin.valueOf(bsqUtxoCandidates.stream().mapToLong(e -> e.getValue().value).sum()) :
                bsqWalletService.getAvailableBalance();
    }

    private void setSendBtcGroupVisibleState(boolean visible) {
        btcTitledGroupBg.setVisible(visible);
        receiversBtcAddressInputTextField.setVisible(visible);
        btcAmountInputTextField.setVisible(visible);
        sendBtcButton.setVisible(visible);
        btcInputControlButton.setVisible(visible);
        btcOpReturnButton.setVisible(visible);

        btcTitledGroupBg.setManaged(visible);
        receiversBtcAddressInputTextField.setManaged(visible);
        btcAmountInputTextField.setManaged(visible);
        sendBtcButton.setManaged(visible);
        btcInputControlButton.setManaged(visible);
        btcOpReturnButton.setManaged(visible);
    }

    private void addSendBtcGroup() {
        btcTitledGroupBg = addTitledGroupBg(root, ++gridRow, 2, Res.get("dao.wallet.send.sendBtcFunds"), Layout.GROUP_DISTANCE);
        GridPane.setColumnSpan(btcTitledGroupBg, 3);
        receiversBtcAddressInputTextField = addInputTextField(root, gridRow,
                Res.get("dao.wallet.send.receiverBtcAddress"), Layout.FIRST_ROW_AND_GROUP_DISTANCE);
        receiversBtcAddressInputTextField.setValidator(btcAddressValidator);
        GridPane.setColumnSpan(receiversBtcAddressInputTextField, 3);

        btcAmountInputTextField = addInputTextField(root, ++gridRow, Res.get("dao.wallet.send.btcAmount"));
        btcAmountInputTextField.setValidator(btcValidator);
        GridPane.setColumnSpan(btcAmountInputTextField, 3);

        preImageTextField = addInputTextField(root, ++gridRow, Res.get("dao.wallet.send.preImage"));
        GridPane.setColumnSpan(preImageTextField, 3);
        preImageTextField.setVisible(false);
        preImageTextField.setManaged(false);

        Tuple3<Label, TextField, VBox> opReturnDataAsHexTuple = addTopLabelTextField(root, ++gridRow, Res.get("dao.wallet.send.opReturnAsHex"), -10);
        opReturnDataAsHexLabel = opReturnDataAsHexTuple.first;
        opReturnDataAsHexTextField = opReturnDataAsHexTuple.second;
        opReturnDataAsHexBox = opReturnDataAsHexTuple.third;
        GridPane.setColumnSpan(opReturnDataAsHexBox, 3);
        opReturnDataAsHexBox.setVisible(false);
        opReturnDataAsHexBox.setManaged(false);

        Tuple3<Button, Button, Button> tuple = FormBuilder.add3ButtonsAfterGroup(root, ++gridRow,
                Res.get("dao.wallet.send.sendBtc"), Res.get("dao.wallet.send.inputControl"), Res.get("dao.wallet.send.addOpReturn"));
        sendBtcButton = tuple.first;
        btcInputControlButton = tuple.second;
        btcOpReturnButton = tuple.third;
    }

    private void onBtcInputControl() {
        List<TransactionOutput> unspentTransactionOutputs = bsqWalletService.getSpendableNonBsqTransactionOutputs();
        if (btcUtxoCandidates == null) {
            btcUtxoCandidates = new HashSet<>(unspentTransactionOutputs);
        } else {
            // If we had some selection stored we need to update to already spent entries
            btcUtxoCandidates = btcUtxoCandidates.stream().
                    filter(e -> unspentTransactionOutputs.contains(e)).
                    collect(Collectors.toSet());
        }
        TxInputSelectionWindow txInputSelectionWindow = new TxInputSelectionWindow(unspentTransactionOutputs,
                btcUtxoCandidates,
                btcFormatter);
        txInputSelectionWindow.onAction(() -> setBtcUtxoCandidates(txInputSelectionWindow.getCandidates())).
                show();
    }

    private void onShowPreImageField() {
        btcOpReturnButton.setDisable(true);
        preImageTextField.setManaged(true);
        preImageTextField.setVisible(true);
        opReturnDataAsHexBox.setManaged(true);
        opReturnDataAsHexBox.setVisible(true);
        GridPane.setRowSpan(btcTitledGroupBg, 4);
    }

    private void setBtcUtxoCandidates(Set<TransactionOutput> candidates) {
        this.btcUtxoCandidates = candidates;
        updateBtcValidator(getSpendableBtcBalance());
        btcAmountInputTextField.refreshValidation();
    }

    private Coin getSpendableBtcBalance() {
        return btcUtxoCandidates != null ?
                Coin.valueOf(btcUtxoCandidates.stream().mapToLong(e -> e.getValue().value).sum()) :
                bsqWalletService.getAvailableNonBsqBalance();
    }

    private void onSendBtc() {
        if (!GUIUtil.isReadyForTxBroadcastOrShowPopup(p2PService, walletsSetup)) {
            return;
        }

        String receiversAddressString = receiversBtcAddressInputTextField.getText();
        Coin receiverAmount = bsqFormatter.parseToBTC(btcAmountInputTextField.getText());
        try {
            byte[] opReturnData = null;
            if (preImageTextField.isVisible() && !preImageTextField.getText().trim().isEmpty()) {
                opReturnData = getOpReturnData(preImageTextField.getText());
            }
            Transaction preparedSendTx = bsqWalletService.getPreparedSendBtcTx(receiversAddressString, receiverAmount, btcUtxoCandidates);
            Transaction txWithBtcFee = btcWalletService.completePreparedBsqTx(preparedSendTx, opReturnData);
            Transaction signedTx = bsqWalletService.signTxAndVerifyNoDustOutputs(txWithBtcFee);
            Coin miningFee = signedTx.getFee();

            if (miningFee.getValue() >= receiverAmount.getValue())
                GUIUtil.showWantToBurnBTCPopup(miningFee, receiverAmount, btcFormatter);
            else {
                int txVsize = signedTx.getVsize();
                showPublishTxPopup(receiverAmount,
                        txWithBtcFee,
                        TxType.INVALID,
                        miningFee,
                        txVsize, receiversBtcAddressInputTextField.getText(),
                        btcFormatter,
                        btcFormatter,
                        () -> {
                            receiversBtcAddressInputTextField.setText("");
                            btcAmountInputTextField.setText("");
                            preImageTextField.clear();

                            receiversBtcAddressInputTextField.resetValidation();
                            btcAmountInputTextField.resetValidation();

                        });
            }
        } catch (BsqChangeBelowDustException e) {
            String msg = Res.get("popup.warning.btcChangeBelowDustException", btcFormatter.formatCoinWithCode(e.getOutputValue()));
            new Popup().warning(msg).show();
        } catch (Throwable t) {
            handleError(t);
        }
    }

    private byte[] getOpReturnData(String preImageAsString) {
        byte[] opReturnData;
        try {
            // If preImage is hex encoded we use it directly
            opReturnData = Hex.decode(preImageAsString);
        } catch (Throwable ignore) {
            opReturnData = preImageAsString.getBytes(Charsets.UTF_8);
        }

        // If too long for OpReturn we hash it
        if (opReturnData.length > 80) {
            opReturnData = Hash.getSha256Ripemd160hash(opReturnData);
            opReturnDataAsHexLabel.setText(Res.get("dao.wallet.send.opReturnAsHash"));
        } else {
            opReturnDataAsHexLabel.setText(Res.get("dao.wallet.send.opReturnAsHex"));
        }

        return opReturnData;
    }

    private String getOpReturnDataAsHexFromPreImage(String preImage) {
        return Hex.encode(getOpReturnData(preImage));
    }

    private void handleError(Throwable t) {
        if (t instanceof InsufficientMoneyException) {
            final Coin missingCoin = ((InsufficientMoneyException) t).missing;
            final String missing = missingCoin != null ? missingCoin.toFriendlyString() : "null";
            new Popup().warning(Res.get("popup.warning.insufficientBtcFundsForBsqTx", missing))
                    .actionButtonTextWithGoTo("navigation.funds.depositFunds")
                    .onAction(() -> navigation.navigateTo(MainView.class, FundsView.class, DepositView.class))
                    .show();
        } else {
            log.error(t.toString());
            t.printStackTrace();
            new Popup().warning(t.getMessage()).show();
        }
    }

    private void showPublishTxPopup(Coin receiverAmount,
                                    Transaction txWithBtcFee,
                                    TxType txType,
                                    Coin miningFee,
                                    int txVsize, String address,
                                    CoinFormatter amountFormatter, // can be BSQ or BTC formatter
                                    CoinFormatter feeFormatter,
                                    ResultHandler resultHandler) {
        new Popup().headLine(Res.get("dao.wallet.send.sendFunds.headline"))
                .confirmation(Res.get("dao.wallet.send.sendFunds.details",
                        amountFormatter.formatCoinWithCode(receiverAmount),
                        address,
                        feeFormatter.formatCoinWithCode(miningFee),
                        CoinUtil.getFeePerVbyte(miningFee, txVsize),
                        txVsize / 1000d,
                        amountFormatter.formatCoinWithCode(receiverAmount)))
                .actionButtonText(Res.get("shared.yes"))
                .onAction(() -> {
                    doWithdraw(txWithBtcFee, txType, new TxBroadcaster.Callback() {
                        @Override
                        public void onSuccess(Transaction transaction) {
                            log.debug("Successfully sent tx with id {}", txWithBtcFee.getTxId().toString());
                            String key = "showTransactionSentBsq";
                            if (DontShowAgainLookup.showAgain(key)) {
                                new TxDetailsBsq(txWithBtcFee.getTxId().toString(), address, amountFormatter.formatCoinWithCode(receiverAmount))
                                        .dontShowAgainId(key)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(TxBroadcastException exception) {
                            new Popup().warning(exception.toString());
                        }
                    });
                    resultHandler.handleResult();
                })
                .closeButtonText(Res.get("shared.cancel"))
                .show();
    }

    private void doWithdraw(Transaction txWithBtcFee, TxType txType, TxBroadcaster.Callback callback) {
        if (btcWalletService.isEncrypted()) {
            UserThread.runAfter(() -> walletPasswordWindow.onAesKey(aesKey ->
                            sendFunds(txWithBtcFee, txType, callback))
                    .show(), 300, TimeUnit.MILLISECONDS);
        } else {
            sendFunds(txWithBtcFee, txType, callback);
        }
    }

    private void sendFunds(Transaction txWithBtcFee, TxType txType, TxBroadcaster.Callback callback) {
        walletsManager.publishAndCommitBsqTx(txWithBtcFee, txType, callback);
    }
}
