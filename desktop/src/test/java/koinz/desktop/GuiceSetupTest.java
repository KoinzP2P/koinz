package koinz.desktop;

import koinz.desktop.app.BisqAppModule;
import koinz.desktop.common.view.CachingViewLoader;
import koinz.desktop.common.view.ViewLoader;
import koinz.desktop.common.view.guice.InjectorViewFactory;
import koinz.desktop.main.dao.bonding.BondingViewUtils;
import koinz.desktop.main.funds.transactions.TradableRepository;
import koinz.desktop.main.offer.offerbook.OfferBook;
import koinz.desktop.main.overlays.notifications.NotificationCenter;
import koinz.desktop.main.overlays.windows.TorNetworkSettingsWindow;
import koinz.desktop.main.presentation.DaoPresentation;
import koinz.desktop.main.presentation.MarketPricePresentation;
import koinz.desktop.util.Transitions;

import koinz.core.app.AvoidStandbyModeService;
import koinz.core.app.P2PNetworkSetup;
import koinz.core.app.TorSetup;
import koinz.core.app.WalletAppSetup;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.network.p2p.seed.DefaultSeedNodeRepository;
import koinz.core.notifications.MobileMessageEncryption;
import koinz.core.notifications.MobileModel;
import koinz.core.notifications.MobileNotificationService;
import koinz.core.notifications.MobileNotificationValidator;
import koinz.core.notifications.alerts.MyOfferTakenEvents;
import koinz.core.notifications.alerts.TradeEvents;
import koinz.core.notifications.alerts.market.MarketAlerts;
import koinz.core.notifications.alerts.price.PriceAlert;
import koinz.core.payment.ChargeBackRisk;
import koinz.core.payment.TradeLimits;
import koinz.core.proto.network.CoreNetworkProtoResolver;
import koinz.core.proto.persistable.CorePersistenceProtoResolver;
import koinz.core.support.dispute.arbitration.ArbitrationDisputeListService;
import koinz.core.support.dispute.arbitration.ArbitrationManager;
import koinz.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import koinz.core.support.dispute.arbitration.arbitrator.ArbitratorService;
import koinz.core.support.dispute.mediation.MediationDisputeListService;
import koinz.core.support.dispute.mediation.MediationManager;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.mediation.mediator.MediatorService;
import koinz.core.support.traderchat.TraderChatManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;
import koinz.core.util.coin.BsqFormatter;

import koinz.network.p2p.network.BridgeAddressProvider;
import koinz.network.p2p.seed.SeedNodeRepository;

import koinz.common.ClockWatcher;
import koinz.common.config.Config;
import koinz.common.crypto.KeyRing;
import koinz.common.crypto.KeyStorage;
import koinz.common.crypto.PubKeyRing;
import koinz.common.file.CorruptedStorageFileHandler;
import koinz.common.persistence.PersistenceManager;
import koinz.common.proto.network.NetworkProtoResolver;
import koinz.common.proto.persistable.PersistenceProtoResolver;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiceSetupTest {

    private Injector injector;

    @BeforeEach
    public void setUp() {
        Res.setup();
        CurrencyUtil.setup();

        injector = Guice.createInjector(new BisqAppModule(new Config()));
    }

    @Test
    public void testGuiceSetup() {
        injector.getInstance(AvoidStandbyModeService.class);
        // desktop module
        assertSingleton(OfferBook.class);
        assertSingleton(CachingViewLoader.class);
        assertSingleton(Navigation.class);
        assertSingleton(InjectorViewFactory.class);
        assertSingleton(NotificationCenter.class);
        assertSingleton(BsqFormatter.class);
        assertSingleton(TorNetworkSettingsWindow.class);
        assertSingleton(MarketPricePresentation.class);
        assertSingleton(ViewLoader.class);
        assertSingleton(DaoPresentation.class);
        assertSingleton(Transitions.class);
        assertSingleton(TradableRepository.class);
        assertSingleton(BondingViewUtils.class);

        // core module
//        assertSingleton(BisqSetup.class); // this is a can of worms
//        assertSingleton(DisputeMsgEvents.class);
        assertSingleton(TorSetup.class);
        assertSingleton(P2PNetworkSetup.class);
        assertSingleton(WalletAppSetup.class);
        assertSingleton(TradeLimits.class);
        assertSingleton(KeyStorage.class);
        assertSingleton(KeyRing.class);
        assertSingleton(PubKeyRing.class);
        assertSingleton(User.class);
        assertSingleton(ClockWatcher.class);
        assertSingleton(Preferences.class);
        assertSingleton(BridgeAddressProvider.class);
        assertSingleton(CorruptedStorageFileHandler.class);
        assertSingleton(AvoidStandbyModeService.class);
        assertSingleton(DefaultSeedNodeRepository.class);
        assertSingleton(SeedNodeRepository.class);
        assertTrue(injector.getInstance(SeedNodeRepository.class) instanceof DefaultSeedNodeRepository);
        assertSingleton(CoreNetworkProtoResolver.class);
        assertSingleton(NetworkProtoResolver.class);
        assertTrue(injector.getInstance(NetworkProtoResolver.class) instanceof CoreNetworkProtoResolver);
        assertSingleton(PersistenceProtoResolver.class);
        assertSingleton(CorePersistenceProtoResolver.class);
        assertTrue(injector.getInstance(PersistenceProtoResolver.class) instanceof CorePersistenceProtoResolver);
        assertSingleton(MobileMessageEncryption.class);
        assertSingleton(MobileNotificationService.class);
        assertSingleton(MobileNotificationValidator.class);
        assertSingleton(MobileModel.class);
        assertSingleton(MyOfferTakenEvents.class);
        assertSingleton(TradeEvents.class);
        assertSingleton(PriceAlert.class);
        assertSingleton(MarketAlerts.class);
        assertSingleton(ChargeBackRisk.class);
        assertSingleton(ArbitratorService.class);
        assertSingleton(ArbitratorManager.class);
        assertSingleton(ArbitrationManager.class);
        assertSingleton(ArbitrationDisputeListService.class);
        assertSingleton(MediatorService.class);
        assertSingleton(MediatorManager.class);
        assertSingleton(MediationManager.class);
        assertSingleton(MediationDisputeListService.class);
        assertSingleton(TraderChatManager.class);

        assertNotSingleton(PersistenceManager.class);
    }

    private void assertSingleton(Class<?> type) {
        assertSame(injector.getInstance(type), injector.getInstance(type));
    }

    private void assertNotSingleton(Class<?> type) {
        assertNotSame(injector.getInstance(type), injector.getInstance(type));
    }
}
