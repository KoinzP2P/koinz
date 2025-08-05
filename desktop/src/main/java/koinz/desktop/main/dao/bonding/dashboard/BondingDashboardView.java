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

package koinz.desktop.main.dao.bonding.dashboard;

import koinz.desktop.common.view.ActivatableView;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.dao.wallet.BsqBalanceUtil;

import javax.inject.Inject;

import javafx.scene.layout.GridPane;

@FxmlView
public class BondingDashboardView extends ActivatableView<GridPane, Void> {
    private final BsqBalanceUtil bsqBalanceUtil;

    private int gridRow = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private BondingDashboardView(BsqBalanceUtil bsqBalanceUtil) {
        this.bsqBalanceUtil = bsqBalanceUtil;
    }

    public void initialize() {
        gridRow = bsqBalanceUtil.addGroup(root, gridRow);
        gridRow = bsqBalanceUtil.addBondBalanceGroup(root, gridRow, "last");
    }

    @Override
    protected void activate() {
        bsqBalanceUtil.activate();
    }

    @Override
    protected void deactivate() {
        bsqBalanceUtil.deactivate();
    }
}

