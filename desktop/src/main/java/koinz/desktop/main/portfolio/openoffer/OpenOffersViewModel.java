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

package koinz.desktop.main.portfolio.openoffer;

import koinz.desktop.common.model.ActivatableWithDataModel;
import koinz.desktop.common.model.ViewModel;
import koinz.desktop.util.GUIUtil;

import koinz.core.offer.OpenOffer;
import koinz.core.util.PriceUtil;

import koinz.network.p2p.P2PService;

import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.handlers.ResultHandler;

import com.google.inject.Inject;

class OpenOffersViewModel extends ActivatableWithDataModel<OpenOffersDataModel> implements ViewModel {
    private final P2PService p2PService;
    private final PriceUtil priceUtil;


    @Inject
    public OpenOffersViewModel(OpenOffersDataModel dataModel,
                               P2PService p2PService,
                               PriceUtil priceUtil) {
        super(dataModel);

        this.p2PService = p2PService;
        this.priceUtil = priceUtil;
    }

    @Override
    protected void activate() {
        priceUtil.recalculateBsq30DayAveragePrice();
    }

    void onActivateOpenOffer(OpenOffer openOffer,
                             ResultHandler resultHandler,
                             ErrorMessageHandler errorMessageHandler) {
        dataModel.onActivateOpenOffer(openOffer, resultHandler, errorMessageHandler);
    }

    void onDeactivateOpenOffer(OpenOffer openOffer,
                               ResultHandler resultHandler,
                               ErrorMessageHandler errorMessageHandler) {
        dataModel.onDeactivateOpenOffer(openOffer, resultHandler, errorMessageHandler);
    }

    void onRemoveOpenOffer(OpenOffer openOffer, ResultHandler resultHandler, ErrorMessageHandler errorMessageHandler) {
        dataModel.onRemoveOpenOffer(openOffer, resultHandler, errorMessageHandler);
    }

    boolean isBootstrappedOrShowPopup() {
        return GUIUtil.isBootstrappedOrShowPopup(p2PService);
    }
}
