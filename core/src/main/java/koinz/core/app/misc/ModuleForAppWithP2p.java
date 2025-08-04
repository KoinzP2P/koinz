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

package koinz.core.app.misc;

import koinz.core.alert.AlertModule;
import koinz.core.app.TorSetup;
import koinz.core.btc.BitcoinModule;
import koinz.core.dao.DaoModule;
import koinz.core.filter.FilterModule;
import koinz.core.network.CoreBanFilter;
import koinz.core.network.p2p.seed.DefaultSeedNodeRepository;
import koinz.core.offer.OfferModule;
import koinz.core.proto.network.CoreNetworkProtoResolver;
import koinz.core.proto.persistable.CorePersistenceProtoResolver;
import koinz.core.trade.TradeModule;
import koinz.core.user.Preferences;
import koinz.core.user.User;

import koinz.network.crypto.EncryptionServiceModule;
import koinz.network.p2p.P2PModule;
import koinz.network.p2p.network.BridgeAddressProvider;
import koinz.network.p2p.network.BanFilter;
import koinz.network.p2p.seed.SeedNodeRepository;

import koinz.common.ClockWatcher;
import koinz.common.app.AppModule;
import koinz.common.config.Config;
import koinz.common.crypto.KeyRing;
import koinz.common.crypto.KeyStorage;
import koinz.common.crypto.PubKeyRing;
import koinz.common.crypto.PubKeyRingProvider;
import koinz.common.proto.network.NetworkProtoResolver;
import koinz.common.proto.persistable.PersistenceProtoResolver;

import com.google.inject.Singleton;

import java.io.File;

import static koinz.common.config.Config.*;
import static com.google.inject.name.Names.named;

public class ModuleForAppWithP2p extends AppModule {

    public ModuleForAppWithP2p(Config config) {
        super(config);
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);

        bind(KeyStorage.class).in(Singleton.class);
        bind(KeyRing.class).in(Singleton.class);
        bind(User.class).in(Singleton.class);
        bind(ClockWatcher.class).in(Singleton.class);
        bind(NetworkProtoResolver.class).to(CoreNetworkProtoResolver.class).in(Singleton.class);
        bind(PersistenceProtoResolver.class).to(CorePersistenceProtoResolver.class).in(Singleton.class);
        bind(Preferences.class).in(Singleton.class);
        bind(BridgeAddressProvider.class).to(Preferences.class).in(Singleton.class);
        bind(TorSetup.class).in(Singleton.class);

        bind(SeedNodeRepository.class).to(DefaultSeedNodeRepository.class).in(Singleton.class);
        bind(BanFilter.class).to(CoreBanFilter.class).in(Singleton.class);

        bind(File.class).annotatedWith(named(STORAGE_DIR)).toInstance(config.storageDir);
        bind(File.class).annotatedWith(named(KEY_STORAGE_DIR)).toInstance(config.keyStorageDir);

        bindConstant().annotatedWith(named(USE_DEV_PRIVILEGE_KEYS)).to(config.useDevPrivilegeKeys);
        bindConstant().annotatedWith(named(USE_DEV_MODE)).to(config.useDevMode);
        bindConstant().annotatedWith(named(USE_DEV_MODE_HEADER)).to(config.useDevModeHeader);
        bindConstant().annotatedWith(named(REFERRAL_ID)).to(config.referralId);
        bindConstant().annotatedWith(named(PREVENT_PERIODIC_SHUTDOWN_AT_SEED_NODE)).to(config.preventPeriodicShutdownAtSeedNode);
        bindConstant().annotatedWith(named(SEED_NODE_REPORTING_SERVER_URL)).to(config.seedNodeReportingServerUrl);

        // ordering is used for shut down sequence
        install(new TradeModule(config));
        install(new EncryptionServiceModule(config));
        install(new OfferModule(config));
        install(new P2PModule(config));
        install(new BitcoinModule(config));
        install(new DaoModule(config));
        install(new AlertModule(config));
        install(new FilterModule(config));
        bind(PubKeyRing.class).toProvider(PubKeyRingProvider.class);
    }
}
