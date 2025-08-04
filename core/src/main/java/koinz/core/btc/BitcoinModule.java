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

import koinz.core.btc.model.AddressEntryList;
import koinz.core.btc.nodes.BtcNodes;
import koinz.core.btc.setup.RegTestHost;
import koinz.core.btc.setup.WalletsSetup;
import koinz.core.btc.wallet.BsqCoinSelector;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.NonBsqCoinSelector;
import koinz.core.btc.wallet.TradeWalletService;
import koinz.core.provider.PriceFeedNodeAddressProvider;
import koinz.core.provider.fee.FeeService;
import koinz.core.provider.price.PriceFeedService;

import koinz.common.app.AppModule;
import koinz.common.config.Config;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import java.io.File;

import java.util.Arrays;
import java.util.List;

import static koinz.common.config.Config.PROVIDERS;
import static koinz.common.config.Config.WALLET_DIR;
import static com.google.inject.name.Names.named;

public class BitcoinModule extends AppModule {

    public BitcoinModule(Config config) {
        super(config);
    }

    @Override
    protected void configure() {
        // If we have selected KOINZ_DAO_REGTEST or KOINZ_DAO_TESTNET we use our master regtest node,
        // otherwise the specified host or default (localhost)
        String regTestHost = config.bitcoinRegtestHost;
        if (regTestHost.isEmpty()) {
            regTestHost = config.getBaseCurrencyNetwork().isDaoTestNet() ?
                    "104.248.31.39" :
                    config.getBaseCurrencyNetwork().isDaoRegTest() ?
                            "134.209.242.206" :
                            Config.DEFAULT_REGTEST_HOST;
        }

        RegTestHost.HOST = regTestHost;
        if (Arrays.asList("localhost", "127.0.0.1").contains(regTestHost)) {
            bind(RegTestHost.class).toInstance(RegTestHost.LOCALHOST);
        } else if ("none".equals(regTestHost)) {
            bind(RegTestHost.class).toInstance(RegTestHost.NONE);
        } else {
            bind(RegTestHost.class).toInstance(RegTestHost.REMOTE_HOST);
        }

        bind(File.class).annotatedWith(named(WALLET_DIR)).toInstance(config.walletDir);

        bindConstant().annotatedWith(named(Config.BTC_NODES)).to(config.btcNodes);
        bindConstant().annotatedWith(named(Config.USER_AGENT)).to(config.userAgent);
        bindConstant().annotatedWith(named(Config.NUM_CONNECTIONS_FOR_BTC)).to(config.numConnectionsForBtc);
        bindConstant().annotatedWith(named(Config.USE_ALL_PROVIDED_NODES)).to(config.useAllProvidedNodes);
        bindConstant().annotatedWith(named(Config.IGNORE_LOCAL_BTC_NODE)).to(config.ignoreLocalBtcNode);
        bindConstant().annotatedWith(named(Config.SOCKS5_DISCOVER_MODE)).to(config.socks5DiscoverMode);
        bind(new TypeLiteral<List<String>>(){}).annotatedWith(named(PROVIDERS)).toInstance(config.providers);

        bind(AddressEntryList.class).in(Singleton.class);
        bind(WalletsSetup.class).in(Singleton.class);
        bind(BtcWalletService.class).in(Singleton.class);
        bind(BsqWalletService.class).in(Singleton.class);
        bind(TradeWalletService.class).in(Singleton.class);
        bind(BsqCoinSelector.class).in(Singleton.class);
        bind(NonBsqCoinSelector.class).in(Singleton.class);
        bind(BtcNodes.class).in(Singleton.class);
        bind(Balances.class).in(Singleton.class);

        bind(PriceFeedNodeAddressProvider.class).in(Singleton.class);
        bind(PriceFeedService.class).in(Singleton.class);
        bind(FeeService.class).in(Singleton.class);
        bind(TxFeeEstimationService.class).in(Singleton.class);
    }
}

