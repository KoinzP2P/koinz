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

package koinz.core.network;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.network.BanFilter;

import koinz.common.config.Config;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoreBanFilter implements BanFilter {
    private final Set<NodeAddress> bannedPeersFromOptions = new HashSet<>();
    private Predicate<NodeAddress> bannedNodePredicate;

    /**
     * @param banList  List of banned peers from program argument
     */
    @Inject
    public CoreBanFilter(@Named(Config.BAN_LIST) List<String> banList) {
        banList.stream().map(NodeAddress::new).forEach(bannedPeersFromOptions::add);
    }

    @Override
    public void setBannedNodePredicate(Predicate<NodeAddress> bannedNodePredicate) {
        this.bannedNodePredicate = bannedNodePredicate;
    }

    @Override
    public boolean isPeerBanned(NodeAddress nodeAddress) {
        return bannedPeersFromOptions.contains(nodeAddress) ||
                bannedNodePredicate != null && bannedNodePredicate.test(nodeAddress);
    }
}
