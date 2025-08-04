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

package koinz.core.dao.node;

import koinz.core.dao.node.full.FullNode;
import koinz.core.dao.node.lite.LiteNode;
import koinz.core.user.Preferences;

import com.google.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Returns a FullNode or LiteNode based on the Config.FULL_DAO_NODE option.
 */
@Slf4j
public class BsqNodeProvider {
    @Getter
    private final BsqNode bsqNode;

    @Inject
    public BsqNodeProvider(LiteNode bsqLiteNode,
                           FullNode bsqFullNode,
                           Preferences preferences) {

        boolean rpcDataSet = preferences.getRpcUser() != null &&
                !preferences.getRpcUser().isEmpty()
                && preferences.getRpcPw() != null &&
                !preferences.getRpcPw().isEmpty() &&
                preferences.getBlockNotifyPort() > 0;
        boolean daoFullNode = preferences.isDaoFullNode();
        if (daoFullNode && !rpcDataSet) {
            log.warn("daoFullNode is set in preferences but RPC user and pw are missing. We reset daoFullNode in preferences to false.");
            preferences.setDaoFullNode(false);
        }
        bsqNode = daoFullNode && rpcDataSet ? bsqFullNode : bsqLiteNode;
    }
}
