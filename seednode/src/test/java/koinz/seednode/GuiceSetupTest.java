package koinz.seednode;

import koinz.core.app.misc.AppSetupWithP2PAndDAO;
import koinz.core.app.misc.ModuleForAppWithP2p;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;

import koinz.common.config.Config;

import com.google.inject.Guice;

import org.junit.jupiter.api.Test;

public class GuiceSetupTest {
    @Test
    public void testGuiceSetup() {
        Res.setup();
        CurrencyUtil.setup();

        ModuleForAppWithP2p module = new ModuleForAppWithP2p(new Config());
        Guice.createInjector(module).getInstance(AppSetupWithP2PAndDAO.class);
    }
}
