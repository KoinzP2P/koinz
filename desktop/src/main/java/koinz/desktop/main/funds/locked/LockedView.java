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

package koinz.desktop.main.funds.locked;

import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.components.AutoTooltipButton;
import koinz.desktop.components.AutoTooltipLabel;
import koinz.desktop.components.ExternalHyperlink;
import koinz.desktop.components.HyperlinkWithIcon;
import koinz.desktop.components.list.FilterBox;
import koinz.desktop.main.overlays.windows.OfferDetailsWindow;
import koinz.desktop.main.overlays.windows.TradeDetailsWindow;
import koinz.desktop.util.GUIUtil;

import koinz.core.btc.listeners.BalanceListener;
import koinz.core.btc.model.AddressEntry;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.locale.Res;
import koinz.core.offer.OpenOffer;
import koinz.core.offer.OpenOfferManager;
import koinz.core.trade.TradeManager;
import koinz.core.trade.model.Tradable;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;

import com.googlecode.jcsv.writer.CSVEntryConverter;

import javax.inject.Inject;
import javax.inject.Named;

import de.jensd.fx.fontawesome.AwesomeIcon;

import javafx.fxml.FXML;

import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.geometry.Insets;

import javafx.beans.property.ReadOnlyObjectWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.util.Callback;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@FxmlView
public class LockedView extends ActivatableView<VBox, Void> {
    @FXML
    FilterBox filterBox;
    @FXML
    TableView<LockedListItem> tableView;
    @FXML
    TableColumn<LockedListItem, LockedListItem> dateColumn, tradeIdColumn, detailsColumn, addressColumn, balanceColumn;
    @FXML
    Label numItems;
    @FXML
    Region spacer;
    @FXML
    AutoTooltipButton exportButton;

    private final BtcWalletService btcWalletService;
    private final TradeManager tradeManager;
    private final OpenOfferManager openOfferManager;
    private final CoinFormatter formatter;
    private final OfferDetailsWindow offerDetailsWindow;
    private final TradeDetailsWindow tradeDetailsWindow;
    private final ObservableList<LockedListItem> observableList = FXCollections.observableArrayList();
    private final FilteredList<LockedListItem> filteredList = new FilteredList<>(observableList);
    private final SortedList<LockedListItem> sortedList = new SortedList<>(filteredList);
    private BalanceListener balanceListener;
    private ListChangeListener<OpenOffer> openOfferListChangeListener;
    private ListChangeListener<Trade> tradeListChangeListener;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private LockedView(BtcWalletService btcWalletService,
                       TradeManager tradeManager,
                       OpenOfferManager openOfferManager,
                       @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter formatter,
                       OfferDetailsWindow offerDetailsWindow,
                       TradeDetailsWindow tradeDetailsWindow) {
        this.btcWalletService = btcWalletService;
        this.tradeManager = tradeManager;
        this.openOfferManager = openOfferManager;
        this.formatter = formatter;
        this.offerDetailsWindow = offerDetailsWindow;
        this.tradeDetailsWindow = tradeDetailsWindow;
    }

    @Override
    public void initialize() {
        dateColumn.setGraphic(new AutoTooltipLabel(Res.get("shared.dateTime")));
        tradeIdColumn.setGraphic(new AutoTooltipLabel(Res.get("shared.tradeId")));
        detailsColumn.setGraphic(new AutoTooltipLabel(Res.get("shared.details")));
        addressColumn.setGraphic(new AutoTooltipLabel(Res.get("shared.address")));
        balanceColumn.setGraphic(new AutoTooltipLabel(Res.get("shared.balanceWithCur", Res.getBaseCurrencyCode())));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPlaceholder(new AutoTooltipLabel(Res.get("funds.locked.noFunds")));

        setTradeIdColumnCellFactory();
        setDateColumnCellFactory();
        setDetailsColumnCellFactory();
        setAddressColumnCellFactory();
        setBalanceColumnCellFactory();

        addressColumn.setComparator(Comparator.comparing(LockedListItem::getAddressString));
        tradeIdColumn.setComparator(Comparator.comparing(o -> o.getTrade().getId()));
        detailsColumn.setComparator(Comparator.comparing(o -> o.getDetails()));
        balanceColumn.setComparator(Comparator.comparing(LockedListItem::getBalance));
        dateColumn.setComparator(Comparator.comparing(o -> getTradable(o).map(Tradable::getDate).orElse(new Date(0))));
        tableView.getSortOrder().add(dateColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);

        balanceListener = new BalanceListener() {
            @Override
            public void onBalanceChanged(Coin balance, Transaction tx) {
                updateList();
            }
        };
        openOfferListChangeListener = c -> updateList();
        tradeListChangeListener = c -> updateList();

        HBox.setHgrow(spacer, Priority.ALWAYS);
        numItems.setId("num-offers");
        numItems.setPadding(new Insets(-5, 0, 0, 10));
        filterBox.initializeWithCallback(filteredList, tableView, () ->
                numItems.setText(Res.get("shared.numItemsLabel", sortedList.size())));
        exportButton.updateText(Res.get("shared.exportCSV"));
    }

    @Override
    protected void activate() {
        filterBox.activate();
        openOfferManager.getObservableList().addListener(openOfferListChangeListener);
        tradeManager.getObservableList().addListener(tradeListChangeListener);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        updateList();

        btcWalletService.addBalanceListener(balanceListener);

        exportButton.setOnAction(event -> {
            ObservableList<TableColumn<LockedListItem, ?>> tableColumns = tableView.getColumns();
            int reportColumns = tableColumns.size();
            CSVEntryConverter<LockedListItem> headerConverter = item -> {
                String[] columns = new String[reportColumns];
                for (int i = 0; i < columns.length; i++)
                    columns[i] = ((AutoTooltipLabel) tableColumns.get(i).getGraphic()).getText();
                return columns;
            };
            CSVEntryConverter<LockedListItem> contentConverter = item -> {
                String[] columns = new String[reportColumns];
                columns[0] = item.getDateString();
                columns[1] = item.getTrade().getId();
                columns[2] = item.getDetails();
                columns[3] = item.getAddressString();
                columns[4] = item.getBalanceString();
                return columns;
            };

            GUIUtil.exportCSV("lockedInTradesFunds.csv",
                    headerConverter,
                    contentConverter,
                    new LockedListItem(),
                    sortedList,
                    (Stage) root.getScene().getWindow());
        });
    }

    @Override
    protected void deactivate() {
        filterBox.deactivate();
        openOfferManager.getObservableList().removeListener(openOfferListChangeListener);
        tradeManager.getObservableList().removeListener(tradeListChangeListener);
        sortedList.comparatorProperty().unbind();
        observableList.forEach(LockedListItem::cleanup);
        btcWalletService.removeBalanceListener(balanceListener);
        exportButton.setOnAction(null);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void updateList() {
        observableList.forEach(LockedListItem::cleanup);
        observableList.setAll(tradeManager.getTradesStreamWithFundsLockedIn()
                .map(trade -> {
                    Optional<AddressEntry> addressEntryOptional = btcWalletService.getAddressEntry(trade.getId(),
                            AddressEntry.Context.MULTI_SIG);
                    return addressEntryOptional.map(addressEntry -> new LockedListItem(trade,
                            addressEntry,
                            btcWalletService,
                            formatter)).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    private Optional<Tradable> getTradable(LockedListItem item) {
        String offerId = item.getAddressEntry().getOfferId();
        Optional<Trade> tradeOptional = tradeManager.getTradeById(offerId);
        if (tradeOptional.isPresent()) {
            return Optional.of(tradeOptional.get());
        } else if (openOfferManager.getOpenOfferById(offerId).isPresent()) {
            return Optional.of(openOfferManager.getOpenOfferById(offerId).get());
        } else {
            return Optional.empty();
        }
    }

    private void openDetailPopup(LockedListItem item) {
        Optional<Tradable> tradableOptional = getTradable(item);
        if (tradableOptional.isPresent()) {
            Tradable tradable = tradableOptional.get();
            if (tradable instanceof Trade) {
                tradeDetailsWindow.show((Trade) tradable);
            } else if (tradable instanceof OpenOffer) {
                offerDetailsWindow.show(tradable.getOffer());
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // ColumnCellFactories
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void setDateColumnCellFactory() {
        dateColumn.getStyleClass().add("first-column");
        dateColumn.setCellValueFactory((addressListItem) -> new ReadOnlyObjectWrapper<>(addressListItem.getValue()));
        dateColumn.setCellFactory(new Callback<>() {

            @Override
            public TableCell<LockedListItem, LockedListItem> call(TableColumn<LockedListItem,
                    LockedListItem> column) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(final LockedListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            if (getTradable(item).isPresent())
                                setGraphic(new AutoTooltipLabel(item.getDateString()));
                            else
                                setGraphic(new AutoTooltipLabel(item.getDateString()));
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    private void setTradeIdColumnCellFactory() {
        tradeIdColumn.setCellValueFactory((addressListItem) -> new ReadOnlyObjectWrapper<>(addressListItem.getValue()));
        tradeIdColumn.setCellFactory(new Callback<>() {

            @Override
            public TableCell<LockedListItem, LockedListItem> call(TableColumn<LockedListItem,
                    LockedListItem> column) {
                return new TableCell<>() {

                    private HyperlinkWithIcon field;

                    @Override
                    public void updateItem(final LockedListItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            Optional<Tradable> tradableOptional = getTradable(item);
                            if (tradableOptional.isPresent()) {
                                field = new HyperlinkWithIcon(item.getTrade().getId(), AwesomeIcon.INFO_SIGN);
                                field.setOnAction(event -> openDetailPopup(item));
                                field.setTooltip(new Tooltip(Res.get("tooltip.openPopupForDetails")));
                                setGraphic(field);
                            } else {
                                setGraphic(new AutoTooltipLabel(item.getTrade().getId()));
                            }

                        } else {
                            setGraphic(null);
                            if (field != null)
                                field.setOnAction(null);
                        }
                    }
                };
            }
        });
    }

    private void setDetailsColumnCellFactory() {
        detailsColumn.setCellValueFactory((addressListItem) -> new ReadOnlyObjectWrapper<>(addressListItem.getValue()));
        detailsColumn.setCellFactory(new Callback<>() {

            @Override
            public TableCell<LockedListItem, LockedListItem> call(TableColumn<LockedListItem,
                    LockedListItem> column) {
                return new TableCell<>() {

                    private HyperlinkWithIcon field;

                    @Override
                    public void updateItem(final LockedListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setGraphic(new AutoTooltipLabel(item.getDetails()));
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    private void setAddressColumnCellFactory() {
        addressColumn.setCellValueFactory((addressListItem) -> new ReadOnlyObjectWrapper<>(addressListItem.getValue()));

        addressColumn.setCellFactory(
                new Callback<>() {

                    @Override
                    public TableCell<LockedListItem, LockedListItem> call(TableColumn<LockedListItem,
                            LockedListItem> column) {
                        return new TableCell<>() {
                            private HyperlinkWithIcon hyperlinkWithIcon;

                            @Override
                            public void updateItem(final LockedListItem item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item != null && !empty) {
                                    String address = item.getAddressString();
                                    hyperlinkWithIcon = new ExternalHyperlink(address, true);
                                    hyperlinkWithIcon.setOnAction(event -> GUIUtil.openAddressInBlockExplorer(address));
                                    hyperlinkWithIcon.setTooltip(new Tooltip(Res.get("tooltip.openBlockchainForAddress", address)));
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
    }

    private void setBalanceColumnCellFactory() {
        balanceColumn.getStyleClass().add("last-column");
        balanceColumn.setCellValueFactory((addressListItem) -> new ReadOnlyObjectWrapper<>(addressListItem.getValue()));
        balanceColumn.setCellFactory(
                new Callback<>() {

                    @Override
                    public TableCell<LockedListItem, LockedListItem> call(TableColumn<LockedListItem,
                            LockedListItem> column) {
                        return new TableCell<>() {
                            @Override
                            public void updateItem(final LockedListItem item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic((item != null && !empty) ? item.getBalanceLabel() : null);
                            }
                        };
                    }
                });
    }

}


