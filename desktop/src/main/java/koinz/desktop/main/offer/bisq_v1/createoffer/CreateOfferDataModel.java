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

package koinz.desktop.main.offer.bisq_v1.createoffer;

import koinz.desktop.Navigation;
import koinz.desktop.main.offer.bisq_v1.MutableOfferDataModel;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.offer.OfferUtil;
import koinz.core.offer.OpenOfferManager;
import koinz.core.offer.bisq_v1.CreateOfferService;
import koinz.core.payment.PaymentAccount;
import koinz.core.provider.fee.FeeService;
import koinz.core.provider.price.PriceFeedService;
import koinz.core.trade.statistics.TradeStatisticsManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.P2PService;

import com.google.inject.Inject;

import javax.inject.Named;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain for that UI element.
 * Note that the create offer domain has a deeper scope in the application domain (TradeManager).
 * That model is just responsible for the domain specific parts displayed needed in that UI element.
 */
class CreateOfferDataModel extends MutableOfferDataModel {

    @Inject
    public CreateOfferDataModel(CreateOfferService createOfferService,
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

    @Override
    protected Set<PaymentAccount> getUserPaymentAccounts() {
        return new HashSet<>(Objects.requireNonNull(user.getPaymentAccounts()));
    }
}
