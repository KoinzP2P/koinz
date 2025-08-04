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

package koinz.core.btc.nodes;

import koinz.core.btc.nodes.BtcNodes.BtcNode;
import koinz.core.user.Preferences;

import koinz.common.config.Config;

import java.util.List;

import org.junit.jupiter.api.Test;

import static koinz.core.btc.nodes.BtcNodes.BitcoinNodesOption.CUSTOM;
import static koinz.core.btc.nodes.BtcNodes.BitcoinNodesOption.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BtcNodesSetupPreferencesTest {
    @Test
    public void testSelectPreferredNodesWhenPublicOption() {
        Preferences delegate = mock(Preferences.class);
        when(delegate.getBitcoinNodesOptionOrdinal()).thenReturn(PUBLIC.ordinal());

        BtcNodesSetupPreferences preferences = new BtcNodesSetupPreferences(delegate, Config.DEFAULT_NUM_CONNECTIONS_FOR_BTC_PUBLIC, null);
        List<BtcNode> nodes = preferences.selectPreferredNodes(mock(BtcNodes.class));

        assertTrue(nodes.isEmpty());
    }

    @Test
    public void testSelectPreferredNodesWhenCustomOption() {
        Preferences delegate = mock(Preferences.class);
        when(delegate.getBitcoinNodesOptionOrdinal()).thenReturn(CUSTOM.ordinal());
        when(delegate.getBitcoinNodes()).thenReturn("aaa.onion,bbb.onion");

        BtcNodesSetupPreferences preferences = new BtcNodesSetupPreferences(delegate, Config.DEFAULT_NUM_CONNECTIONS_FOR_BTC_PUBLIC, null);
        List<BtcNode> nodes = preferences.selectPreferredNodes(mock(BtcNodes.class));

        assertEquals(2, nodes.size());
    }
}
