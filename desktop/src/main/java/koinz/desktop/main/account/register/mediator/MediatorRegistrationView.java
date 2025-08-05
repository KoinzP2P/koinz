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

package koinz.desktop.main.account.register.mediator;


import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.account.register.AgentRegistrationView;

import koinz.core.locale.Res;
import koinz.core.support.dispute.mediation.mediator.Mediator;

import koinz.common.config.Config;

import javax.inject.Named;

import javax.inject.Inject;

@FxmlView
public class MediatorRegistrationView extends AgentRegistrationView<Mediator, MediatorRegistrationViewModel> {

    @Inject
    public MediatorRegistrationView(MediatorRegistrationViewModel model,
                                    @Named(Config.USE_DEV_PRIVILEGE_KEYS) boolean useDevPrivilegeKeys) {
        super(model, useDevPrivilegeKeys);
    }

    @Override
    protected String getRole() {
        return Res.get("shared.mediator");
    }
}
