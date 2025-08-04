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

package koinz.core.filter;

import koinz.core.locale.Res;
import koinz.core.provider.PriceFeedNodeAddressProvider;
import koinz.core.user.Preferences;
import koinz.core.user.User;

import koinz.network.p2p.P2PService;
import koinz.network.p2p.network.BanFilter;
import koinz.network.p2p.storage.P2PDataStorage;

import koinz.common.config.Config;
import koinz.common.crypto.KeyRing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mockito.MockedStatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class FilterManagerInitializationTests {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void onAllServicesInitializedNoFilterMainnet() {
        P2PService p2PService = mock(P2PService.class);
        var filterManager = new FilterManager(
                p2PService,
                mock(KeyRing.class),
                mock(User.class),
                mock(Preferences.class),
                mock(Config.class),
                mock(PriceFeedNodeAddressProvider.class),
                mock(BanFilter.class),
                false,
                true
        );

        var p2pStorageMap = new HashMap<>();
        P2PDataStorage p2pDataStorage = mock(P2PDataStorage.class);

        doReturn(p2pStorageMap).when(p2pDataStorage).getMap();
        doReturn(p2pDataStorage).when(p2PService).getP2PDataStorage();

        final var warningHandlerTriggered = new AtomicBoolean();
        try (MockedStatic<Res> mocked = mockStatic(Res.class)) {
            mocked.when(() -> Res.get("popup.warning.noFilter"))
                    .thenReturn("No filter.");

            filterManager.setFilterWarningHandler(errorMessage -> {
                if (errorMessage.equals(Res.get("popup.warning.noFilter"))) {
                    warningHandlerTriggered.set(true);
                }
            });

            filterManager.onAllServicesInitialized();
        }

        assertTrue(warningHandlerTriggered.get());
    }

    @Test
    void onAllServicesInitializedNoFilterMainnetIgnoreDevMsg() {
        P2PService p2PService = mock(P2PService.class);
        var filterManager = new FilterManager(
                p2PService,
                mock(KeyRing.class),
                mock(User.class),
                mock(Preferences.class),
                mock(Config.class),
                mock(PriceFeedNodeAddressProvider.class),
                mock(BanFilter.class),
                true,
                true
        );

        var p2pStorageMap = new HashMap<>();
        P2PDataStorage p2pDataStorage = mock(P2PDataStorage.class);

        doReturn(p2pStorageMap).when(p2pDataStorage).getMap();
        doReturn(p2pDataStorage).when(p2PService).getP2PDataStorage();

        final var warningHandlerTriggered = new AtomicBoolean();
        try (MockedStatic<Res> mocked = mockStatic(Res.class)) {
            mocked.when(() -> Res.get("popup.warning.noFilter"))
                    .thenReturn("No filter.");

            filterManager.setFilterWarningHandler(errorMessage -> {
                if (errorMessage.equals(Res.get("popup.warning.noFilter"))) {
                    warningHandlerTriggered.set(true);
                }
            });

            filterManager.onAllServicesInitialized();
        }

        assertFalse(warningHandlerTriggered.get());
    }
}
