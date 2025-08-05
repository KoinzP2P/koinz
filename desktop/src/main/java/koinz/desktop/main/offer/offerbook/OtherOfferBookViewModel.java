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
import koinz.desktop.main.offer.OfferViewUtil;
import koinz.desktop.util.GUIUtil;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.api.CoreApi;
import koinz.core.btc.setup.WalletsSetup;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.dao.state.DaoStateService;
import koinz.core.locale.CryptoCurrency;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.GlobalSettings;
import koinz.core.locale.TradeCurrency;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferDirection;
import koinz.core.offer.OfferFilterService;
import koinz.core.offer.OpenOfferManager;
import koinz.core.payment.payload.PaymentMethod;
import koinz.core.provider.price.PriceFeedService;
import koinz.core.trade.ClosedTradableManager;
import koinz.core.trade.bsq_swap.BsqSwapTradeManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.PriceUtil;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.P2PService;

import com.google.inject.Inject;

import javax.inject.Named;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

public class OtherOfferBookViewModel extends OfferBookViewModel {

    @Inject
    public OtherOfferBookViewModel(User user,
                                   OpenOfferManager openOfferManager,
                                   OfferBook offerBook,
                                   Preferences preferences,
                                   WalletsSetup walletsSetup,
                                   P2PService p2PService,
                                   PriceFeedService priceFeedService,
                                   ClosedTradableManager closedTradableManager,
                                   BsqSwapTradeManager bsqSwapTradeManager,
                                   AccountAgeWitnessService accountAgeWitnessService,
                                   Navigation navigation,
                                   PriceUtil priceUtil,
                                   OfferFilterService offerFilterService,
                                   @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                                   BsqFormatter bsqFormatter,
                                   BsqWalletService bsqWalletService,
                                   CoreApi coreApi,
                                   DaoStateService daoStateService) {
        super(user, openOfferManager, offerBook, preferences, walletsSetup, p2PService, priceFeedService,
                closedTradableManager, bsqSwapTradeManager, accountAgeWitnessService, navigation, priceUtil,
                offerFilterService, btcFormatter, bsqFormatter, bsqWalletService, coreApi, daoStateService);
    }

    @Override
    void saveSelectedCurrencyCodeInPreferences(OfferDirection direction, String code) {
        if (direction == OfferDirection.BUY) {
            preferences.setBuyScreenCryptoCurrencyCode(code);
        } else {
            preferences.setSellScreenCryptoCurrencyCode(code);
        }
    }

    @Override
    protected ObservableList<PaymentMethod> filterPaymentMethods(ObservableList<PaymentMethod> list,
                                                                 TradeCurrency selectedTradeCurrency) {
        return FXCollections.observableArrayList(list.stream().filter(PaymentMethod::isBlockchain).collect(Collectors.toList()));
    }

    @Override
    void fillCurrencies(ObservableList<TradeCurrency> tradeCurrencies,
                        ObservableList<TradeCurrency> allCurrencies) {

        tradeCurrencies.add(new CryptoCurrency(GUIUtil.SHOW_ALL_FLAG, ""));
        tradeCurrencies.addAll(preferences.getCryptoCurrenciesAsObservable().stream()
                .filter(withoutBSQAndTopAltcoin())
                .collect(Collectors.toList()));
        tradeCurrencies.add(new CryptoCurrency(GUIUtil.EDIT_FLAG, ""));

        allCurrencies.add(new CryptoCurrency(GUIUtil.SHOW_ALL_FLAG, ""));
        allCurrencies.addAll(CurrencyUtil.getAllSortedCryptoCurrencies().stream()
                .filter(withoutBSQAndTopAltcoin())
                .collect(Collectors.toList()));
        allCurrencies.add(new CryptoCurrency(GUIUtil.EDIT_FLAG, ""));
    }

    @Override
    Predicate<OfferBookListItem> getCurrencyAndMethodPredicate(OfferDirection direction,
                                                               TradeCurrency selectedTradeCurrency) {
        return offerBookListItem -> {
            Offer offer = offerBookListItem.getOffer();
            // BUY Altcoin is actually SELL Bitcoin
            boolean directionResult = offer.getDirection() == direction;
            boolean currencyResult = CurrencyUtil.isCryptoCurrency(offer.getCurrencyCode()) &&
                    ((showAllTradeCurrenciesProperty.get() &&
                            !offer.getCurrencyCode().equals(GUIUtil.TOP_ALTCOIN.getCode()) &&
                            !offer.getCurrencyCode().equals(GUIUtil.BSQ.getCode())) ||
                            offer.getCurrencyCode().equals(selectedTradeCurrency.getCode()));
            boolean paymentMethodResult = showAllPaymentMethods ||
                    offer.getPaymentMethod().equals(selectedPaymentMethod);
            boolean notMyOfferOrShowMyOffersActivated = !isMyOffer(offerBookListItem.getOffer()) || preferences.isShowOwnOffersInOfferBook();
            return directionResult && currencyResult && paymentMethodResult && notMyOfferOrShowMyOffersActivated;
        };
    }

    @Override
    TradeCurrency getDefaultTradeCurrency() {
        TradeCurrency defaultTradeCurrency = GlobalSettings.getDefaultTradeCurrency();

        if (!CurrencyUtil.isFiatCurrency(defaultTradeCurrency.getCode()) &&
                !defaultTradeCurrency.equals(GUIUtil.BSQ) &&
                !defaultTradeCurrency.equals(GUIUtil.TOP_ALTCOIN) &&
                hasPaymentAccountForCurrency(defaultTradeCurrency)) {
            return defaultTradeCurrency;
        }

        ObservableList<TradeCurrency> tradeCurrencies = FXCollections.observableArrayList(getTradeCurrencies());
        if (!tradeCurrencies.isEmpty()) {
            // drop show all entry and select first currency with payment account available
            tradeCurrencies.remove(0);
            List<TradeCurrency> sortedList = tradeCurrencies.stream().sorted((o1, o2) ->
                    Boolean.compare(!hasPaymentAccountForCurrency(o1),
                            !hasPaymentAccountForCurrency(o2))).collect(Collectors.toList());
            return sortedList.get(0);
        } else {
            return OfferViewUtil.getMainCryptoCurrencies().sorted((o1, o2) ->
                    Boolean.compare(!hasPaymentAccountForCurrency(o1),
                            !hasPaymentAccountForCurrency(o2))).collect(Collectors.toList()).get(0);
        }
    }

    @Override
    String getCurrencyCodeFromPreferences(OfferDirection direction) {
        return direction == OfferDirection.BUY ? preferences.getBuyScreenCryptoCurrencyCode() :
                preferences.getSellScreenCryptoCurrencyCode();
    }

    @NotNull
    private Predicate<CryptoCurrency> withoutBSQAndTopAltcoin() {
        return cryptoCurrency ->
                !cryptoCurrency.equals(GUIUtil.BSQ) &&
                        !cryptoCurrency.equals(GUIUtil.TOP_ALTCOIN);
    }
}
