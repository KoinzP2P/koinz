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

package koinz.desktop.main.dao.monitor.blindvotes;

import koinz.desktop.main.dao.monitor.StateBlockListItem;

import koinz.core.dao.monitoring.model.BlindVoteStateBlock;
import koinz.core.dao.monitoring.model.BlindVoteStateHash;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@EqualsAndHashCode(callSuper = true)
class BlindVoteStateBlockListItem extends StateBlockListItem<BlindVoteStateHash, BlindVoteStateBlock> {
    private final String numBlindVotes;

    BlindVoteStateBlockListItem(BlindVoteStateBlock stateBlock, int cycleIndex) {
        super(stateBlock, cycleIndex);

        numBlindVotes = String.valueOf(stateBlock.getNumBlindVotes());
    }
}
