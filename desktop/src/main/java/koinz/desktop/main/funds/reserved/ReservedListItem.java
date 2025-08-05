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

package koinz.desktop.main.funds.reserved;

import koinz.desktop.util.filtering.FilterableListItem;
import koinz.desktop.components.AutoTooltipLabel;
import koinz.desktop.util.DisplayUtils;
import koinz.desktop.util.filtering.FilteringUtils;

import koinz.core.btc.listeners.BalanceListener;
import koinz.core.btc.model.AddressEntry;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.locale.Res;
import koinz.core.offer.OpenOffer;
import koinz.core.util.coin.CoinFormatter;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.Label;

import java.util.Optional;

import lombok.Getter;

class ReservedListItem implements FilterableListItem {
    private final BalanceListener balanceListener;
    private final BtcWalletService btcWalletService;
    private final CoinFormatter formatter;

    @Getter
    private final Label balanceLabel;
    @Getter
    private final OpenOffer openOffer;
    @Getter
    private final AddressEntry addressEntry;
    @Getter
    private final String addressString;
    @Getter
    private final Address address;
    @Getter
    private Coin balance;
    @Getter
    private String balanceString;

    public ReservedListItem(OpenOffer openOffer,
                            AddressEntry addressEntry,
                            BtcWalletService btcWalletService,
                            CoinFormatter formatter) {
        this.openOffer = openOffer;
        this.addressEntry = addressEntry;
        this.btcWalletService = btcWalletService;
        this.formatter = formatter;
        addressString = addressEntry.getAddressString();
        address = addressEntry.getAddress();
        balanceLabel = new AutoTooltipLabel();
        balanceListener = new BalanceListener(address) {
            @Override
            public void onBalanceChanged(Coin balance, Transaction tx) {
                updateBalance();
            }
        };
        btcWalletService.addBalanceListener(balanceListener);
        updateBalance();
    }

    ReservedListItem() {
        this.openOffer = null;
        this.addressEntry = null;
        this.btcWalletService = null;
        this.formatter = null;
        addressString = null;
        address = null;
        balanceLabel = null;
        balanceListener = null;
    }

    public void cleanup() {
        btcWalletService.removeBalanceListener(balanceListener);
    }

    private void updateBalance() {
        Optional<AddressEntry> addressEntryOptional = btcWalletService.getAddressEntry(openOffer.getId(),
                AddressEntry.Context.RESERVED_FOR_TRADE);
        addressEntryOptional.ifPresent(addressEntry -> {
            balance = btcWalletService.getBalanceForAddress(addressEntry.getAddress());
            if (balance != null) {
                balanceString = formatter.formatCoin(balance);
                balanceLabel.setText(balanceString);
            }
        });
    }

    public String getDateAsString() {
        return DisplayUtils.formatDateTime(openOffer.getDate());
    }

    public String getDetails() {
        return openOffer != null ?
                Res.get("funds.reserved.reserved") :
                Res.get("shared.noDetailsAvailable");
    }

    @Override
    public boolean match(String filterString) {
        if (filterString.isEmpty()) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getDetails(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getAddressString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getDateAsString(), filterString)) {
            return true;
        }
        if (StringUtils.containsIgnoreCase(getBalanceString(), filterString)) {
            return true;
        }
        return FilteringUtils.match(getOpenOffer().getOffer(), filterString);
    }
}
