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

package koinz.desktop.main.dao.bonding.roles;

import koinz.core.dao.DaoFacade;
import koinz.core.dao.governance.bond.BondState;
import koinz.core.dao.governance.bond.role.BondedRole;
import koinz.core.dao.state.model.governance.BondedRoleType;
import koinz.core.dao.state.model.governance.Role;
import koinz.core.locale.Res;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class RolesListItem {
    private final DaoFacade daoFacade;
    private final BondedRole bondedRole;

    RolesListItem(BondedRole bondedRole, DaoFacade daoFacade) {
        this.daoFacade = daoFacade;
        this.bondedRole = bondedRole;
    }

    public String getLockupTxId() {
        return this.bondedRole.getLockupTxId();
    }

    public Role getRole() {
        return this.bondedRole.getBondedAsset();
    }

    public String getName() {
        return this.getRole().getName();
    }

    public String getLink() {
        return this.getRole().getLink();
    }

    public BondedRoleType getType() {
        return this.getRole().getBondedRoleType();
    }

    public String getTypeAsString() {
        return this.getRole().getBondedRoleType().getDisplayString();
    }

    public long getLockupDate() {
        return this.bondedRole.getLockupDate();
    }

    public boolean iAmOwner() {
        return this.daoFacade.isMyRole(this.getRole());
    }

    public String getBondStateAsString() {
        return Res.get("dao.bond.bondState." + bondedRole.getBondState().name());
    }

    public boolean isLockupButtonVisible() {
        return iAmOwner() && (this.bondedRole.getBondState() == BondState.READY_FOR_LOCKUP);
    }

    public boolean isRevokeButtonVisible() {
        return iAmOwner() && (this.bondedRole.getBondState() == BondState.LOCKUP_TX_CONFIRMED);
    }

    public boolean isSignButtonVisible() {
        return iAmOwner() && this.bondedRole.isActive();
    }

    public boolean isVerifyButtonVisible() {
        return this.bondedRole.isActive();
    }
}
