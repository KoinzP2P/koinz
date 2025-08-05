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

package koinz.desktop.main.offer.bisq_v1.createoffer;

import koinz.desktop.Navigation;
import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.offer.OfferView;
import koinz.desktop.main.offer.bisq_v1.MutableOfferView;
import koinz.desktop.main.overlays.windows.OfferDetailsWindow;
import koinz.desktop.util.GUIUtil;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.TradeCurrency;
import koinz.core.offer.OfferDirection;
import koinz.core.payment.PaymentAccount;
import koinz.core.user.Preferences;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import com.google.inject.Inject;

import javax.inject.Named;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.stream.Collectors;

@FxmlView
public class CreateOfferView extends MutableOfferView<CreateOfferViewModel> {
    @Inject
    private CreateOfferView(CreateOfferViewModel model,
                            Navigation navigation,
                            Preferences preferences,
                            OfferDetailsWindow offerDetailsWindow,
                            @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                            BsqFormatter bsqFormatter) {
        super(model, navigation, preferences, offerDetailsWindow, btcFormatter, bsqFormatter);
    }

    @Override
    public void initWithData(OfferDirection direction,
                             TradeCurrency tradeCurrency,
                             OfferView.OfferActionHandler offerActionHandler) {
        // Invert direction for non-Fiat trade currencies -> BUY BSQ is to SELL Bitcoin
        OfferDirection offerDirection = CurrencyUtil.isFiatCurrency(tradeCurrency.getCode()) ? direction :
                direction == OfferDirection.BUY ? OfferDirection.SELL : OfferDirection.BUY;
        super.initWithData(offerDirection, tradeCurrency, offerActionHandler);
    }

    @Override
    protected ObservableList<PaymentAccount> filterPaymentAccounts(ObservableList<PaymentAccount> paymentAccounts) {
        return FXCollections.observableArrayList(
                paymentAccounts.stream().filter(paymentAccount -> {
                    if (model.getTradeCurrency().equals(GUIUtil.BSQ)) {
                        return Objects.equals(paymentAccount.getSingleTradeCurrency(), GUIUtil.BSQ);
                    } else if (model.getTradeCurrency().equals(GUIUtil.TOP_ALTCOIN)) {
                        return Objects.equals(paymentAccount.getSingleTradeCurrency(), GUIUtil.TOP_ALTCOIN);
                    } else if (CurrencyUtil.isFiatCurrency(model.getTradeCurrency().getCode())) {
                        return !paymentAccount.getPaymentMethod().isAltcoin();
                    } else {
                        return paymentAccount.getPaymentMethod().isAltcoin() &&
                                !(Objects.equals(paymentAccount.getSingleTradeCurrency(), GUIUtil.BSQ) ||
                                        Objects.equals(paymentAccount.getSingleTradeCurrency(), GUIUtil.TOP_ALTCOIN));
                    }
                }).collect(Collectors.toList()));
    }
}
