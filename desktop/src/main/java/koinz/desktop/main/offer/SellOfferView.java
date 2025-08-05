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

package koinz.desktop.main.offer;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.common.view.ViewLoader;

import koinz.core.offer.OfferDirection;
import koinz.core.user.Preferences;
import koinz.core.user.User;

import koinz.network.p2p.P2PService;

import javax.inject.Inject;

@FxmlView
public class SellOfferView extends OfferView {

    @Inject
    public SellOfferView(ViewLoader viewLoader,
                         Navigation navigation,
                         Preferences preferences,
                         User user,
                         P2PService p2PService) {
        super(viewLoader,
                navigation,
                preferences,
                user,
                p2PService,
                OfferDirection.SELL);
    }
}
