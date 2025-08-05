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

package koinz.desktop.main.dao.monitor.proposals;

import koinz.desktop.main.dao.monitor.StateInConflictListItem;

import koinz.core.dao.monitoring.model.ProposalStateHash;

import koinz.network.p2p.NodeAddress;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@EqualsAndHashCode(callSuper = true)
class ProposalStateInConflictListItem extends StateInConflictListItem<ProposalStateHash> {
    private final String numProposals;

    ProposalStateInConflictListItem(String peerAddress, ProposalStateHash stateHash, int cycleIndex,
                                    Set<NodeAddress> seedNodeAddresses) {
        super(peerAddress, stateHash, cycleIndex, seedNodeAddresses);

        numProposals = String.valueOf(stateHash.getNumProposals());
    }
}
