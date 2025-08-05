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

package koinz.desktop.main.dao.bonding.reputation;

import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.components.AutoTooltipButton;
import koinz.desktop.components.AutoTooltipTableColumn;
import koinz.desktop.components.ExternalHyperlink;
import koinz.desktop.components.HyperlinkWithIcon;
import koinz.desktop.components.InputTextField;
import koinz.desktop.main.dao.bonding.BondingViewUtils;
import koinz.desktop.util.FormBuilder;
import koinz.desktop.util.GUIUtil;
import koinz.desktop.util.Layout;
import koinz.desktop.util.validation.BsqValidator;

import koinz.core.btc.listeners.BsqBalanceListener;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.dao.DaoFacade;
import koinz.core.dao.governance.bond.BondConsensus;
import koinz.core.dao.governance.bond.BondState;
import koinz.core.dao.governance.bond.reputation.MyBondedReputation;
import koinz.core.locale.Res;
import koinz.core.util.ParsingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.validation.HexStringValidator;
import koinz.core.util.validation.IntegerValidator;

import koinz.common.crypto.Hash;
import koinz.common.util.Utilities;

import org.bitcoinj.core.Coin;

import javax.inject.Inject;

import com.google.common.base.Charsets;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import javafx.util.Callback;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

import static koinz.desktop.util.FormBuilder.addButtonAfterGroup;
import static koinz.desktop.util.FormBuilder.addInputTextField;
import static koinz.desktop.util.FormBuilder.addTitledGroupBg;

@FxmlView
public class MyReputationView extends ActivatableView<GridPane, Void> implements BsqBalanceListener {
    private InputTextField amountInputTextField, timeInputTextField, saltInputTextField;
    private Button lockupButton;
    private TableView<MyReputationListItem> tableView;

    private final BsqFormatter bsqFormatter;
    private final BsqWalletService bsqWalletService;
    private final BondingViewUtils bondingViewUtils;
    private final HexStringValidator hexStringValidator;
    private final BsqValidator bsqValidator;
    private final DaoFacade daoFacade;

    private final IntegerValidator timeInputTextFieldValidator;

    private final ObservableList<MyReputationListItem> observableList = FXCollections.observableArrayList();
    private final SortedList<MyReputationListItem> sortedList = new SortedList<>(observableList);

    private int gridRow = 0;

    private ChangeListener<Boolean> amountFocusOutListener, timeFocusOutListener, saltFocusOutListener;
    private ChangeListener<String> amountInputTextFieldListener, timeInputTextFieldListener, saltInputTextFieldListener;
    private ListChangeListener<MyBondedReputation> myBondedReputationsChangeListener;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private MyReputationView(BsqFormatter bsqFormatter,
                             BsqWalletService bsqWalletService,
                             BondingViewUtils bondingViewUtils,
                             HexStringValidator hexStringValidator,
                             BsqValidator bsqValidator,
                             DaoFacade daoFacade) {
        this.bsqFormatter = bsqFormatter;
        this.bsqWalletService = bsqWalletService;
        this.bondingViewUtils = bondingViewUtils;
        this.hexStringValidator = hexStringValidator;
        this.bsqValidator = bsqValidator;
        this.daoFacade = daoFacade;

        timeInputTextFieldValidator = new IntegerValidator();
        timeInputTextFieldValidator.setMinValue(BondConsensus.getMinLockTime());
        timeInputTextFieldValidator.setMaxValue(BondConsensus.getMaxLockTime());
    }

    @Override
    public void initialize() {
        addTitledGroupBg(root, gridRow, 3, Res.get("dao.bond.reputation.header"));

        amountInputTextField = addInputTextField(root, gridRow, Res.get("dao.bond.reputation.amount"),
                Layout.FIRST_ROW_DISTANCE);
        amountInputTextField.setValidator(bsqValidator);

        timeInputTextField = FormBuilder.addInputTextField(root, ++gridRow, Res.get("dao.bond.reputation.time"));
        timeInputTextField.setValidator(timeInputTextFieldValidator);

        saltInputTextField = FormBuilder.addInputTextField(root, ++gridRow, Res.get("dao.bond.reputation.salt"));
        saltInputTextField.setValidator(hexStringValidator);

        lockupButton = addButtonAfterGroup(root, ++gridRow, Res.get("dao.bond.reputation.lockupButton"));

        tableView = FormBuilder.<MyReputationListItem>addTableViewWithHeader(root, ++gridRow,
                Res.get("dao.bond.reputation.table.header"), 20, "last").first;
        createColumns();
        tableView.setItems(sortedList);
        GridPane.setVgrow(tableView, Priority.ALWAYS);

        createListeners();
    }

    @Override
    protected void activate() {
        amountInputTextField.textProperty().addListener(amountInputTextFieldListener);
        amountInputTextField.focusedProperty().addListener(amountFocusOutListener);

        timeInputTextField.textProperty().addListener(timeInputTextFieldListener);
        timeInputTextField.focusedProperty().addListener(timeFocusOutListener);

        saltInputTextField.textProperty().addListener(saltInputTextFieldListener);
        saltInputTextField.focusedProperty().addListener(saltFocusOutListener);

        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        daoFacade.getMyBondedReputations().addListener(myBondedReputationsChangeListener);
        bsqWalletService.addBsqBalanceListener(this);

        lockupButton.setOnAction((event) -> {
            Coin lockupAmount = ParsingUtils.parseToCoin(amountInputTextField.getText(), bsqFormatter);
            int lockupTime = Integer.parseInt(timeInputTextField.getText());
            String saltAsString = saltInputTextField.getText();
            log.info("Lockup BSQ: salt as hex string={}", saltAsString);
            byte[] salt = Utilities.decodeFromHex(saltAsString);
            bondingViewUtils.lockupBondForReputation(lockupAmount,
                    lockupTime,
                    salt,
                    txId -> {
                    });
            amountInputTextField.setText("");
            timeInputTextField.setText("");
            setNewRandomSalt();
        });


        amountInputTextField.resetValidation();
        timeInputTextField.resetValidation();

        setNewRandomSalt();

        updateList();
        GUIUtil.setFitToRowsForTableView(tableView, 41, 28, 2, 30);
    }

    @Override
    protected void deactivate() {
        amountInputTextField.textProperty().removeListener(amountInputTextFieldListener);
        amountInputTextField.focusedProperty().removeListener(amountFocusOutListener);

        timeInputTextField.textProperty().removeListener(timeInputTextFieldListener);
        timeInputTextField.focusedProperty().removeListener(timeFocusOutListener);

        saltInputTextField.textProperty().removeListener(saltInputTextFieldListener);
        saltInputTextField.focusedProperty().removeListener(saltFocusOutListener);

        daoFacade.getMyBondedReputations().removeListener(myBondedReputationsChangeListener);
        bsqWalletService.removeBsqBalanceListener(this);

        sortedList.comparatorProperty().unbind();

        lockupButton.setOnAction(null);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // BsqBalanceListener
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onUpdateBalances(Coin availableBalance,
                                 Coin availableNonBsqBalance,
                                 Coin unverifiedBalance,
                                 Coin unconfirmedChangeBalance,
                                 Coin lockedForVotingBalance,
                                 Coin lockupBondsBalance,
                                 Coin unlockingBondsBalance) {
        bsqValidator.setAvailableBalance(availableBalance);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void createListeners() {
        amountFocusOutListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                updateButtonState();
            }
        };
        timeFocusOutListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                updateButtonState();
            }
        };
        saltFocusOutListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                updateButtonState();
            }
        };

        amountInputTextFieldListener = (observable, oldValue, newValue) -> updateButtonState();
        timeInputTextFieldListener = (observable, oldValue, newValue) -> updateButtonState();
        saltInputTextFieldListener = (observable, oldValue, newValue) -> updateButtonState();

        myBondedReputationsChangeListener = c -> updateList();
    }

    private void updateList() {
        observableList.setAll(daoFacade.getMyBondedReputations().stream()
                .map(myBondedReputation -> new MyReputationListItem(myBondedReputation, bsqFormatter))
                .sorted(Comparator.comparing(MyReputationListItem::getLockupDateString).reversed())
                .collect(Collectors.toList()));
        GUIUtil.setFitToRowsForTableView(tableView, 41, 28, 2, 30);
    }

    private void setNewRandomSalt() {
        byte[] randomBytes = UUID.randomUUID().toString().getBytes(Charsets.UTF_8);
        // We want to limit it to 20 bytes
        byte[] hashOfRandomBytes = Hash.getSha256Ripemd160hash(randomBytes);
        // bytesAsHexString results in 40 chars
        String bytesAsHexString = Utilities.bytesAsHexString(hashOfRandomBytes);
        saltInputTextField.setText(bytesAsHexString);
        saltInputTextField.resetValidation();
    }

    private void updateButtonState() {
        boolean isValid = bsqValidator.validate(amountInputTextField.getText()).isValid &&
                timeInputTextFieldValidator.validate(timeInputTextField.getText()).isValid &&
                hexStringValidator.validate(saltInputTextField.getText()).isValid;
        lockupButton.setDisable(!isValid);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Table columns
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void createColumns() {
        TableColumn<MyReputationListItem, MyReputationListItem> column;

        column = new AutoTooltipTableColumn<>(Res.get("shared.amountWithCur", "KNZ"));
        column.setMinWidth(120);
        column.setMaxWidth(column.getMinWidth());
        column.getStyleClass().add("first-column");
        column.setCellValueFactory((item) -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                    MyReputationListItem> column) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(final MyReputationListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getAmount());
                        } else
                            setText("");
                    }
                };
            }
        });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.table.column.lockTime"));
        column.setMinWidth(60);
        column.setMaxWidth(column.getMinWidth());
        column.setCellValueFactory((item) -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                    MyReputationListItem> column) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(final MyReputationListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getLockTime());
                        } else
                            setText("");
                    }
                };
            }
        });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.table.column.bondState"));
        column.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setMinWidth(120);
        column.setCellFactory(
                new Callback<>() {
                    @Override
                    public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                            MyReputationListItem> column) {
                        return new TableCell<>() {
                            @Override
                            public void updateItem(MyReputationListItem item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null && !empty) {
                                    setText(item.getBondStateString());
                                } else
                                    setText("");
                            }
                        };
                    }
                });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.table.column.lockupDate"));
        column.setMinWidth(140);
        column.setMaxWidth(column.getMinWidth());
        column.setCellValueFactory((item) -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                    MyReputationListItem> column) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(final MyReputationListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getLockupDateString());
                        } else
                            setText("");
                    }
                };
            }
        });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.table.column.lockupTxId"));
        column.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setMinWidth(80);
        column.setCellFactory(
                new Callback<>() {
                    @Override
                    public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                            MyReputationListItem> column) {
                        return new TableCell<>() {
                            private HyperlinkWithIcon hyperlinkWithIcon;

                            @Override
                            public void updateItem(final MyReputationListItem item, boolean empty) {
                                super.updateItem(item, empty);
                                //noinspection Duplicates
                                if (item != null && !empty) {
                                    String transactionId = item.getTxId();
                                    hyperlinkWithIcon = new ExternalHyperlink(transactionId, true);
                                    hyperlinkWithIcon.setOnAction(event -> GUIUtil.openTxInBsqBlockExplorer(transactionId));
                                    hyperlinkWithIcon.setTooltip(new Tooltip(Res.get("tooltip.openBlockchainForTx", transactionId)));
                                    setGraphic(hyperlinkWithIcon);
                                } else {
                                    setGraphic(null);
                                    if (hyperlinkWithIcon != null)
                                        hyperlinkWithIcon.setOnAction(null);
                                }
                            }
                        };
                    }
                });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.reputation.salt"));
        column.setMinWidth(80);
        column.setCellValueFactory((item) -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                    MyReputationListItem> column) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(final MyReputationListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getSalt());
                        } else
                            setText("");
                    }
                };
            }
        });
        tableView.getColumns().add(column);

        column = new AutoTooltipTableColumn<>(Res.get("dao.bond.reputation.hash"));
        column.setMinWidth(80);
        column.setCellValueFactory((item) -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                    MyReputationListItem> column) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(final MyReputationListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getHash());
                        } else
                            setText("");
                    }
                };
            }
        });
        tableView.getColumns().add(column);

        column = new TableColumn<>();
        column.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue()));
        column.setMinWidth(60);
        column.getStyleClass().add("last-column");
        column.setCellFactory(
                new Callback<>() {
                    @Override
                    public TableCell<MyReputationListItem, MyReputationListItem> call(TableColumn<MyReputationListItem,
                            MyReputationListItem> column) {
                        return new TableCell<>() {
                            AutoTooltipButton button;

                            @Override
                            public void updateItem(final MyReputationListItem item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null && !empty && item.isShowButton()) {
                                    button = new AutoTooltipButton(item.getButtonText());
                                    button.setOnAction(e -> {
                                        if (item.getBondState() == BondState.LOCKUP_TX_CONFIRMED) {
                                            bondingViewUtils.unLock(item.getLockupTxId(),
                                                    txId -> {
                                                    });
                                        }
                                    });
                                    setGraphic(button);
                                } else {
                                    setGraphic(null);
                                    if (button != null) {
                                        button.setOnAction(null);
                                        button = null;
                                    }
                                }
                            }
                        };
                    }
                });
        tableView.getColumns().add(column);
    }
}
