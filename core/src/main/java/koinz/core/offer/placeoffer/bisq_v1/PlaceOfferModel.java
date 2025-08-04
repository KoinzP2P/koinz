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

package koinz.core.offer.placeoffer.bisq_v1;

import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.TradeWalletService;
import koinz.core.dao.DaoFacade;
import koinz.core.dao.burningman.BtcFeeReceiverService;
import koinz.core.filter.FilterManager;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferBookService;
import koinz.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import koinz.core.trade.statistics.TradeStatisticsManager;
import koinz.core.user.User;

import koinz.common.taskrunner.Model;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class PlaceOfferModel implements Model {
    // Immutable
    private final Offer offer;
    private final Coin reservedFundsForOffer;
    private final boolean useSavingsWallet;
    private final boolean isSharedMakerFee;
    private final BtcWalletService walletService;
    private final TradeWalletService tradeWalletService;
    private final BsqWalletService bsqWalletService;
    private final OfferBookService offerBookService;
    private final ArbitratorManager arbitratorManager;
    private final TradeStatisticsManager tradeStatisticsManager;
    private final DaoFacade daoFacade;
    private final BtcFeeReceiverService btcFeeReceiverService;
    private final User user;
    @Getter
    private final FilterManager filterManager;

    // Mutable
    @Setter
    private boolean offerAddedToOfferBook;
    @Setter
    private Transaction transaction;

    public PlaceOfferModel(Offer offer,
                           Coin reservedFundsForOffer,
                           boolean useSavingsWallet,
                           boolean isSharedMakerFee,
                           BtcWalletService walletService,
                           TradeWalletService tradeWalletService,
                           BsqWalletService bsqWalletService,
                           OfferBookService offerBookService,
                           ArbitratorManager arbitratorManager,
                           TradeStatisticsManager tradeStatisticsManager,
                           DaoFacade daoFacade,
                           BtcFeeReceiverService btcFeeReceiverService,
                           User user,
                           FilterManager filterManager) {
        this.offer = offer;
        this.reservedFundsForOffer = reservedFundsForOffer;
        this.useSavingsWallet = useSavingsWallet;
        this.isSharedMakerFee = isSharedMakerFee;
        this.walletService = walletService;
        this.tradeWalletService = tradeWalletService;
        this.bsqWalletService = bsqWalletService;
        this.offerBookService = offerBookService;
        this.arbitratorManager = arbitratorManager;
        this.tradeStatisticsManager = tradeStatisticsManager;
        this.daoFacade = daoFacade;
        this.btcFeeReceiverService = btcFeeReceiverService;
        this.user = user;
        this.filterManager = filterManager;
    }

    @Override
    public void onComplete() {
    }
}
