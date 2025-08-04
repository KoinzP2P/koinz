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

package koinz.core.dao.burningman.accounting.node;

import koinz.core.dao.burningman.accounting.node.full.AccountingFullNode;
import koinz.core.dao.burningman.accounting.node.lite.AccountingLiteNode;
import koinz.core.user.Preferences;

import koinz.common.config.Config;

import com.google.inject.Inject;

import javax.inject.Named;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountingNodeProvider {
    @Getter
    private final AccountingNode accountingNode;

    @Inject
    public AccountingNodeProvider(AccountingLiteNode liteNode,
                                  AccountingFullNode fullNode,
                                  @Named(Config.IS_BM_FULL_NODE) boolean isBmFullNodeFromOptions,
                                  Preferences preferences) {

        String rpcUser = preferences.getRpcUser();
        String rpcPw = preferences.getRpcPw();
        boolean rpcDataSet = rpcUser != null && !rpcUser.isEmpty() &&
                rpcPw != null && !rpcPw.isEmpty() &&
                preferences.getBlockNotifyPort() > 0;
        boolean fullBMAccountingNode = preferences.isFullBMAccountingNode();
        if ((fullBMAccountingNode || isBmFullNodeFromOptions) && rpcDataSet) {
            accountingNode = fullNode;
        } else {
            accountingNode = liteNode;
        }
    }
}
