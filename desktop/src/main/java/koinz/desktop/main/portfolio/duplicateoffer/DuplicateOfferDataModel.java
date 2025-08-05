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
import koinz.desktop.main.offer.bisq_v1.MutableOfferDataModel;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.Restrictions;
import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.TradeCurrency;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferUtil;
import koinz.core.offer.OpenOfferManager;
import koinz.core.offer.bisq_v1.CreateOfferService;
import koinz.core.payment.BsqSwapAccount;
import koinz.core.payment.PaymentAccount;
import koinz.core.provider.fee.FeeService;
import koinz.core.provider.price.PriceFeedService;
import koinz.core.trade.statistics.TradeStatisticsManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.coin.CoinUtil;

import koinz.network.p2p.P2PService;

import org.bitcoinj.core.Coin;

import com.google.inject.Inject;

import javax.inject.Named;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class DuplicateOfferDataModel extends MutableOfferDataModel {

    @Inject
    DuplicateOfferDataModel(CreateOfferService createOfferService,
                            OpenOfferManager openOfferManager,
                            OfferUtil offerUtil,
                            BtcWalletService btcWalletService,
                            BsqWalletService bsqWalletService,
                            Preferences preferences,
                            User user,
                            P2PService p2PService,
                            PriceFeedService priceFeedService,
                            AccountAgeWitnessService accountAgeWitnessService,
                            FeeService feeService,
                            @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                            TradeStatisticsManager tradeStatisticsManager,
                            Navigation navigation) {

        super(createOfferService,
                openOfferManager,
                offerUtil,
                btcWalletService,
                bsqWalletService,
                preferences,
                user,
                p2PService,
                priceFeedService,
                accountAgeWitnessService,
                feeService,
                btcFormatter,
                tradeStatisticsManager,
                navigation);
    }

    public void populateData(Offer offer) {
        if (offer == null)
            return;

        PaymentAccount account = user.getPaymentAccount(offer.getMakerPaymentAccountId());
        if (account != null) {
            this.paymentAccount = account;
        }
        setMinAmount(offer.getMinAmount());
        setAmount(offer.getAmount());
        setPrice(offer.getPrice());
        setVolume(offer.getVolume());
        setUseMarketBasedPrice(offer.isUseMarketBasedPrice());

        setBuyerSecurityDeposit(getBuyerSecurityAsPercent(offer));

        if (offer.isUseMarketBasedPrice()) {
            setMarketPriceMargin(offer.getMarketPriceMargin());
        }
    }

    private double getBuyerSecurityAsPercent(Offer offer) {
        Coin offerBuyerSecurityDeposit = getBoundedBuyerSecurityDepositAsCoin(offer.getBuyerSecurityDeposit());
        double offerBuyerSecurityDepositAsPercent = CoinUtil.getAsPercentPerBtc(offerBuyerSecurityDeposit,
                offer.getAmount());
        return Math.min(offerBuyerSecurityDepositAsPercent,
                Restrictions.getMaxBuyerSecurityDepositAsPercent());
    }

    @Override
    protected Set<PaymentAccount> getUserPaymentAccounts() {
        return Objects.requireNonNull(user.getPaymentAccounts()).stream()
                .filter(account -> !account.getPaymentMethod().isBsqSwap())
                .collect(Collectors.toSet());
    }

    @Override
    protected PaymentAccount getPreselectedPaymentAccount() {
        // If trade currency is BSQ don't use the BSQ swap payment account as it will automatically
        // close the duplicate offer view
        Optional<TradeCurrency> bsqOptional = CurrencyUtil.getTradeCurrency("KNZ");
        if (bsqOptional.isPresent() && tradeCurrency.equals(bsqOptional.get()) && user.getPaymentAccounts() != null) {
            Optional<PaymentAccount> firstBsqPaymentAccount = user.getPaymentAccounts().stream().filter(paymentAccount1 -> {
                Optional<TradeCurrency> tradeCurrency = paymentAccount1.getTradeCurrency();
                return tradeCurrency.isPresent() &&
                        tradeCurrency.get().equals(bsqOptional.get()) &&
                        !paymentAccount1.getId().equals(BsqSwapAccount.ID);
            }).findFirst();

            if (firstBsqPaymentAccount.isPresent()) {
                return firstBsqPaymentAccount.get();
            }
        }

        return super.getPreselectedPaymentAccount();
    }
}
