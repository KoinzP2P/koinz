/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation,
either version 3 of the License,
or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful,
but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not,
see <http://www.gnu.org/licenses/>.
 */

package koinz.desktop.main.offer.bsq_swap.create_offer;

import koinz.desktop.main.offer.bsq_swap.BsqSwapOfferDataModel;

import koinz.core.locale.TradeCurrency;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferDirection;
import koinz.core.offer.OfferUtil;
import koinz.core.offer.bsq_swap.BsqSwapOfferModel;
import koinz.core.offer.bsq_swap.BsqSwapOfferPayload;
import koinz.core.offer.bsq_swap.OpenBsqSwapOfferService;
import koinz.core.payment.PaymentAccount;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.P2PService;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Comparator.comparing;

@Slf4j
class BsqSwapCreateOfferDataModel extends BsqSwapOfferDataModel {
    private final OpenBsqSwapOfferService openBsqSwapOfferService;
    Offer offer;
    private SetChangeListener<PaymentAccount> paymentAccountsChangeListener;
    @Getter
    private final ObservableList<PaymentAccount> paymentAccounts = FXCollections.observableArrayList();
    @Getter
    private PaymentAccount paymentAccount;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    BsqSwapCreateOfferDataModel(BsqSwapOfferModel bsqSwapOfferModel,
                                OpenBsqSwapOfferService openBsqSwapOfferService,
                                User user,
                                P2PService p2PService,
                                @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter) {
        super(bsqSwapOfferModel,
                user,
                p2PService,
                btcFormatter);

        this.openBsqSwapOfferService = openBsqSwapOfferService;

        setOfferId(OfferUtil.getRandomOfferId());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    void initWithData(OfferDirection direction, @Nullable BsqSwapOfferPayload offerPayload) {
        bsqSwapOfferModel.init(direction, true, offerPayload != null ? new Offer(offerPayload) : null);

        fillPaymentAccounts();
        applyPaymentAccount();
        applyTradeCurrency();
    }

    protected void requestNewOffer(Consumer<Offer> resultHandler) {
        openBsqSwapOfferService.requestNewOffer(getOfferId(),
                getDirection(),
                getBtcAmount().get(),
                getMinAmount().get(),
                getPrice().get(),
                offer -> {
                    this.offer = offer;
                    resultHandler.accept(offer);
                });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // UI actions
    ///////////////////////////////////////////////////////////////////////////////////////////

    void onPlaceOffer(Runnable resultHandler) {
        openBsqSwapOfferService.placeBsqSwapOffer(offer,
                resultHandler,
                log::error);
    }

    @Override
    protected void createListeners() {
        super.createListeners();
        paymentAccountsChangeListener = change -> fillPaymentAccounts();
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        user.getPaymentAccountsAsObservable().addListener(paymentAccountsChangeListener);
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        user.getPaymentAccountsAsObservable().removeListener(paymentAccountsChangeListener);
    }

    private void fillPaymentAccounts() {
        Set<PaymentAccount> userPaymentAccounts = getUserPaymentAccounts();
        if (userPaymentAccounts != null) {
            paymentAccounts.setAll(new HashSet<>(userPaymentAccounts.stream().filter(paymentAccount1 -> {
                Optional<TradeCurrency> tradeCurrency = paymentAccount1.getTradeCurrency();
                return tradeCurrency.map(currency -> currency.getCode().equals("KNZ")).orElse(false);
            }).collect(Collectors.toList())));
        }
        paymentAccounts.sort(comparing(PaymentAccount::getAccountName));
    }

    private Set<PaymentAccount> getUserPaymentAccounts() {
        return user.getPaymentAccounts();
    }

    private void applyPaymentAccount() {
        Optional<PaymentAccount> bsqAccountOptional = Objects.requireNonNull(getUserPaymentAccounts()).stream()
                .filter(e -> e.getPaymentMethod().isBsqSwap()).findFirst();
        checkArgument(bsqAccountOptional.isPresent(), "BSQ account must be present");
        this.paymentAccount = bsqAccountOptional.get();
    }

    private void applyTradeCurrency() {
        Optional<TradeCurrency> optionalTradeCurrency = paymentAccount.getTradeCurrency();
        checkArgument(optionalTradeCurrency.isPresent(), "BSQ tradeCurrency must be present");
        tradeCurrency = optionalTradeCurrency.get();
    }
}
