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

package koinz.desktop.main.portfolio.bsqswaps;

import koinz.desktop.common.model.ActivatableDataModel;

import koinz.core.btc.listeners.BsqBalanceListener;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.trade.ClosedTradableManager;
import koinz.core.trade.bsq_swap.BsqSwapTradeManager;
import koinz.core.trade.model.bsq_swap.BsqSwapTrade;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import com.google.inject.Inject;

import javax.inject.Named;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

class UnconfirmedBsqSwapsDataModel extends ActivatableDataModel {

    private final BsqSwapTradeManager bsqSwapTradeManager;
    private final BsqWalletService bsqWalletService;
    private final ObservableList<UnconfirmedBsqSwapsListItem> list = FXCollections.observableArrayList();
    private final ListChangeListener<BsqSwapTrade> tradesListChangeListener;
    private final BsqBalanceListener bsqBalanceListener;
    private final BsqFormatter bsqFormatter;
    private final CoinFormatter btcFormatter;
    private final ClosedTradableManager closedTradableManager;

    @Inject
    public UnconfirmedBsqSwapsDataModel(BsqSwapTradeManager bsqSwapTradeManager,
                                        BsqWalletService bsqWalletService,
                                        BsqFormatter bsqFormatter,
                                        @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                                        ClosedTradableManager closedTradableManager) {
        this.bsqSwapTradeManager = bsqSwapTradeManager;
        this.bsqWalletService = bsqWalletService;
        this.bsqFormatter = bsqFormatter;
        this.btcFormatter = btcFormatter;
        this.closedTradableManager = closedTradableManager;

        tradesListChangeListener = change -> applyList();
        bsqBalanceListener = (availableBalance, availableNonBsqBalance, unverifiedBalance,
                              unconfirmedChangeBalance, lockedForVotingBalance, lockedInBondsBalance,
                              unlockingBondsBalance) -> applyList();
    }

    @Override
    protected void activate() {
        applyList();
        bsqSwapTradeManager.getObservableList().addListener(tradesListChangeListener);
        bsqWalletService.addBsqBalanceListener(bsqBalanceListener);
    }

    @Override
    protected void deactivate() {
        bsqSwapTradeManager.getObservableList().removeListener(tradesListChangeListener);
        bsqWalletService.removeBsqBalanceListener(bsqBalanceListener);
    }

    public ObservableList<UnconfirmedBsqSwapsListItem> getList() {
        return list;
    }

    private void applyList() {
        list.clear();

        list.addAll(bsqSwapTradeManager.getUnconfirmedBsqSwapTrades()
                .map(bsqSwapTrade -> new UnconfirmedBsqSwapsListItem(
                        bsqSwapTrade,
                        bsqWalletService,
                        btcFormatter,
                        bsqFormatter,
                        bsqSwapTradeManager,
                        closedTradableManager
                ))
                .collect(Collectors.toList()));

        // we sort by date, the earliest first
        list.sort((o1, o2) -> o2.getBsqSwapTrade().getDate().compareTo(o1.getBsqSwapTrade().getDate()));
    }
}
