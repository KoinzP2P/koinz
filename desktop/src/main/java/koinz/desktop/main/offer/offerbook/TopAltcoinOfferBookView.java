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

package koinz.desktop.main.offer.offerbook;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.overlays.windows.BsqSwapOfferDetailsWindow;
import koinz.desktop.main.overlays.windows.OfferDetailsWindow;

import koinz.core.account.sign.SignedWitnessService;
import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.alert.PrivateNotificationManager;
import koinz.core.locale.Res;
import koinz.core.offer.OfferDirection;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import koinz.common.config.Config;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.scene.layout.GridPane;

@FxmlView
public class TopAltcoinOfferBookView extends OfferBookView<GridPane, TopAltcoinOfferBookViewModel> {

    @Inject
    TopAltcoinOfferBookView(TopAltcoinOfferBookViewModel model,
                            Navigation navigation,
                            OfferDetailsWindow offerDetailsWindow,
                            BsqSwapOfferDetailsWindow bsqSwapOfferDetailsWindow,
                            @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter formatter,
                            PrivateNotificationManager privateNotificationManager,
                            @Named(Config.USE_DEV_PRIVILEGE_KEYS) boolean useDevPrivilegeKeys,
                            AccountAgeWitnessService accountAgeWitnessService,
                            SignedWitnessService signedWitnessService) {
        super(model, navigation, offerDetailsWindow, bsqSwapOfferDetailsWindow, formatter, privateNotificationManager, useDevPrivilegeKeys, accountAgeWitnessService, signedWitnessService);
    }

    @Override
    protected String getMarketTitle() {
        return model.getDirection().equals(OfferDirection.BUY) ?
                Res.get("offerbook.availableOffersToBuy", TopAltcoinOfferBookViewModel.TOP_ALTCOIN.getCode(), Res.getBaseCurrencyCode()) :
                Res.get("offerbook.availableOffersToSell", TopAltcoinOfferBookViewModel.TOP_ALTCOIN.getCode(), Res.getBaseCurrencyCode());
    }

    @Override
    protected void activate() {
        model.onSetTradeCurrency(TopAltcoinOfferBookViewModel.TOP_ALTCOIN);

        super.activate();

        currencyComboBoxContainer.setVisible(false);
        currencyComboBoxContainer.setManaged(false);
    }

    @Override
    String getTradeCurrencyCode() {
        return TopAltcoinOfferBookViewModel.TOP_ALTCOIN.getCode();
    }
}
