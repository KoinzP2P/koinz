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

package koinz.core.btc;

import koinz.core.btc.listeners.BalanceListener;
import koinz.core.btc.model.AddressEntry;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.offer.OpenOffer;
import koinz.core.offer.OpenOfferManager;
import koinz.core.support.dispute.Dispute;
import koinz.core.support.dispute.refund.RefundManager;
import koinz.core.trade.ClosedTradableManager;
import koinz.core.trade.TradeManager;
import koinz.core.trade.bisq_v1.FailedTradesManager;
import koinz.core.trade.model.bisq_v1.Trade;

import koinz.common.UserThread;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;

import javax.inject.Inject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.collections.ListChangeListener;

import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Balances {
    private final TradeManager tradeManager;
    private final BtcWalletService btcWalletService;
    private final OpenOfferManager openOfferManager;
    private final ClosedTradableManager closedTradableManager;
    private final FailedTradesManager failedTradesManager;
    private final RefundManager refundManager;

    @Getter
    private final ObjectProperty<Coin> availableBalance = new SimpleObjectProperty<>();
    @Getter
    private final ObjectProperty<Coin> reservedBalance = new SimpleObjectProperty<>();
    @Getter
    private final ObjectProperty<Coin> lockedBalance = new SimpleObjectProperty<>();

    @Inject
    public Balances(TradeManager tradeManager,
                    BtcWalletService btcWalletService,
                    OpenOfferManager openOfferManager,
                    ClosedTradableManager closedTradableManager,
                    FailedTradesManager failedTradesManager,
                    RefundManager refundManager) {
        this.tradeManager = tradeManager;
        this.btcWalletService = btcWalletService;
        this.openOfferManager = openOfferManager;
        this.closedTradableManager = closedTradableManager;
        this.failedTradesManager = failedTradesManager;
        this.refundManager = refundManager;
    }

    public void onAllServicesInitialized() {
        openOfferManager.getObservableList().addListener((ListChangeListener<OpenOffer>) c -> updateBalance());
        tradeManager.getObservableList().addListener((ListChangeListener<Trade>) change -> updateBalance());
        refundManager.getDisputesAsObservableList().addListener((ListChangeListener<Dispute>) c -> updateBalance());
        btcWalletService.addBalanceListener(new BalanceListener() {
            @Override
            public void onBalanceChanged(Coin balance, Transaction tx) {
                updateBalance();
            }
        });
        btcWalletService.addNewBestBlockListener(storedBlock -> updateBalance());

        updateBalance();
    }

    private void updateBalance() {
        // Need to delay a bit to get the balances correct
        UserThread.execute(() -> {
            updateAvailableBalance();
            updateReservedBalance();
            updateLockedBalance();
        });
    }

    private void updateAvailableBalance() {
        long sum = btcWalletService.getAddressEntriesForAvailableBalanceStream()
                .mapToLong(addressEntry -> btcWalletService.getBalanceForAddress(addressEntry.getAddress()).value)
                .sum();
        availableBalance.set(Coin.valueOf(sum));
    }

    private void updateReservedBalance() {
        long sum = openOfferManager.getObservableList().stream()
                .map(openOffer -> btcWalletService.getAddressEntry(openOffer.getId(), AddressEntry.Context.RESERVED_FOR_TRADE)
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(AddressEntry::getAddress)
                .distinct()
                .mapToLong(address -> btcWalletService.getBalanceForAddress(address).value)
                .sum();
        reservedBalance.set(Coin.valueOf(sum));
    }

    private void updateLockedBalance() {
        Stream<Trade> lockedTrades = Stream.concat(closedTradableManager.getTradesStreamWithFundsLockedIn(), failedTradesManager.getTradesStreamWithFundsLockedIn());
        lockedTrades = Stream.concat(lockedTrades, tradeManager.getTradesStreamWithFundsLockedIn());
        long sum = lockedTrades.map(trade -> btcWalletService.getAddressEntry(trade.getId(),
                                AddressEntry.Context.MULTI_SIG)
                        .orElse(null))
                .filter(Objects::nonNull)
                .mapToLong(AddressEntry::getCoinLockedInMultiSig)
                .sum();
        lockedBalance.set(Coin.valueOf(sum));
    }
}
