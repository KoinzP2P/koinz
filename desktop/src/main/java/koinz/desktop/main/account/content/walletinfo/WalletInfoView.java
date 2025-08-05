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

package koinz.desktop.main.account.content.walletinfo;

import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.overlays.popups.Popup;
import koinz.desktop.main.overlays.windows.ShowWalletDataWindow;
import koinz.desktop.main.overlays.windows.WalletPasswordWindow;
import koinz.desktop.util.Layout;

import koinz.core.btc.listeners.BalanceListener;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.WalletService;
import koinz.core.btc.wallet.WalletsManager;
import koinz.core.locale.Res;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import koinz.common.config.Config;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Joiner;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

import static koinz.desktop.util.FormBuilder.addButtonAfterGroup;
import static koinz.desktop.util.FormBuilder.addMultilineLabel;
import static koinz.desktop.util.FormBuilder.addTitledGroupBg;
import static koinz.desktop.util.FormBuilder.addTopLabelTextField;
import static org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED_SPENDABLE;

@FxmlView
public class WalletInfoView extends ActivatableView<GridPane, Void> {

    private final WalletsManager walletsManager;
    private final BtcWalletService btcWalletService;
    private final BsqWalletService bsqWalletService;
    private final WalletPasswordWindow walletPasswordWindow;
    private final CoinFormatter btcFormatter;
    private final BsqFormatter bsqFormatter;
    private int gridRow = 0;
    private Button openDetailsButton;
    private TextField btcTextField, bsqTextField;
    private BalanceListener btcWalletBalanceListener;
    private BalanceListener bsqWalletBalanceListener;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private WalletInfoView(WalletsManager walletsManager,
                           BtcWalletService btcWalletService,
                           BsqWalletService bsqWalletService,
                           WalletPasswordWindow walletPasswordWindow,
                           @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                           BsqFormatter bsqFormatter) {
        this.walletsManager = walletsManager;
        this.btcWalletService = btcWalletService;
        this.bsqWalletService = bsqWalletService;
        this.walletPasswordWindow = walletPasswordWindow;
        this.btcFormatter = btcFormatter;
        this.bsqFormatter = bsqFormatter;
    }

    @Override
    public void initialize() {
        addTitledGroupBg(root, gridRow, 3, Res.get("account.menu.walletInfo.balance.headLine"));
        addMultilineLabel(root, gridRow, Res.get("account.menu.walletInfo.balance.info"), Layout.FIRST_ROW_DISTANCE, Double.MAX_VALUE);
        btcTextField = addTopLabelTextField(root, ++gridRow, "BTC", -Layout.FLOATING_LABEL_DISTANCE).second;
        bsqTextField = addTopLabelTextField(root, ++gridRow, "KNZ", -Layout.FLOATING_LABEL_DISTANCE).second;

        addTitledGroupBg(root, ++gridRow, 4, Res.get("account.menu.walletInfo.xpub.headLine"), Layout.GROUP_DISTANCE);
        addXpubKeys(btcWalletService, "BTC", Layout.FIRST_ROW_AND_GROUP_DISTANCE);
        addXpubKeys(bsqWalletService, "KNZ", -Layout.FLOATING_LABEL_DISTANCE);

        addTitledGroupBg(root, gridRow, 4, Res.get("account.menu.walletInfo.path.headLine"), Layout.GROUP_DISTANCE);
        addMultilineLabel(root, gridRow++, Res.get("account.menu.walletInfo.path.info"), Layout.FIRST_ROW_AND_GROUP_DISTANCE, Double.MAX_VALUE);
        addAccountPaths(btcWalletService, "BTC");
        addAccountPaths(bsqWalletService, "KNZ");

        openDetailsButton = addButtonAfterGroup(root, gridRow, Res.get("account.menu.walletInfo.openDetails"));

        btcWalletBalanceListener = new BalanceListener() {
            @Override
            public void onBalanceChanged(Coin balanceAsCoin, Transaction tx) {
                updateBalances(btcWalletService);
            }
        };
        bsqWalletBalanceListener = new BalanceListener() {
            @Override
            public void onBalanceChanged(Coin balanceAsCoin, Transaction tx) {
                updateBalances(bsqWalletService);
            }
        };
    }


    @Override
    protected void activate() {
        btcWalletService.addBalanceListener(btcWalletBalanceListener);
        bsqWalletService.addBalanceListener(bsqWalletBalanceListener);
        updateBalances(btcWalletService);
        updateBalances(bsqWalletService);

        openDetailsButton.setOnAction(e -> {
            if (walletsManager.areWalletsAvailable()) {
                new ShowWalletDataWindow(walletsManager, btcWalletService, walletPasswordWindow).width(root.getWidth()).show();
            } else {
                new Popup().warning(Res.get("popup.warning.walletNotInitialized")).show();
            }
        });
    }

    @Override
    protected void deactivate() {
        btcWalletService.removeBalanceListener(btcWalletBalanceListener);
        bsqWalletService.removeBalanceListener(bsqWalletBalanceListener);
        openDetailsButton.setOnAction(null);
    }

    private void addXpubKeys(WalletService walletService, String currency, double top) {
        double topDist = top;
        for (DeterministicKeyChain chain : walletService.getWallet().getActiveKeyChains()) {
            Script.ScriptType outputScriptType = chain.getOutputScriptType();
            String type = outputScriptType == Script.ScriptType.P2WPKH ? "segwit" : "legacy";
            String key = chain.getWatchingKey().serializePubB58(Config.baseCurrencyNetworkParameters(), outputScriptType);
            addTopLabelTextField(root, gridRow++, Res.get("account.menu.walletInfo.walletSelector", currency, type),
                    key, topDist);
            topDist = -Layout.FLOATING_LABEL_DISTANCE;
        }
    }

    private void addAccountPaths(WalletService walletService, String currency) {
        for (DeterministicKeyChain chain : walletService.getWallet().getActiveKeyChains()) {
            Script.ScriptType outputScriptType = chain.getOutputScriptType();
            String type = outputScriptType == Script.ScriptType.P2WPKH ? "segwit" : "legacy";
            String path = formatAccountPath(chain.getAccountPath());
            addTopLabelTextField(root, gridRow++, Res.get("account.menu.walletInfo.walletSelector", currency, type),
                    path, -Layout.FLOATING_LABEL_DISTANCE);
        }
    }

    private String formatAccountPath(List<ChildNumber> path) {
        return Joiner.on('/').join(path).replace('H', '\'');
    }

    private void updateBalances(WalletService walletService) {
        if (walletService instanceof BtcWalletService) {
            btcTextField.setText(btcFormatter.formatCoinWithCode(walletService.getBalance(ESTIMATED_SPENDABLE)));
        } else {
            bsqTextField.setText(bsqFormatter.formatCoinWithCode(walletService.getBalance(ESTIMATED_SPENDABLE)));
        }
    }
}
