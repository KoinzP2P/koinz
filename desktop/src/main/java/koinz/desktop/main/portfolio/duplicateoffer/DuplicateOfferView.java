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

package koinz.desktop.main.portfolio.duplicateoffer;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.offer.bisq_v1.MutableOfferView;
import koinz.desktop.main.overlays.windows.OfferDetailsWindow;

import koinz.core.locale.CurrencyUtil;
import koinz.core.offer.bisq_v1.OfferPayload;
import koinz.core.payment.PaymentAccount;
import koinz.core.user.Preferences;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import com.google.inject.Inject;

import javax.inject.Named;

import javafx.geometry.Insets;

import javafx.collections.ObservableList;

@FxmlView
public class DuplicateOfferView extends MutableOfferView<DuplicateOfferViewModel> {

    @Inject
    private DuplicateOfferView(DuplicateOfferViewModel model,
                               Navigation navigation,
                               Preferences preferences,
                               OfferDetailsWindow offerDetailsWindow,
                               @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                               BsqFormatter bsqFormatter) {
        super(model, navigation, preferences, offerDetailsWindow, btcFormatter, bsqFormatter);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void doActivate() {
        super.doActivate();

        // Workaround to fix margin on top of amount group
        gridPane.setPadding(new Insets(-20, 25, -1, 25));

        updatePriceToggle();

        // To force re-validation of payment account validation
        onPaymentAccountsComboBoxSelected();
    }

    @Override
    protected ObservableList<PaymentAccount> filterPaymentAccounts(ObservableList<PaymentAccount> paymentAccounts) {
        return paymentAccounts;
    }

    public void initWithData(OfferPayload offerPayload) {
        initWithData(offerPayload.getDirection(),
                CurrencyUtil.getTradeCurrency(offerPayload.getCurrencyCode()).get(),
                null);
        model.initWithData(offerPayload);
    }
}
