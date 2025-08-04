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

package koinz.core.trade.protocol;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.btc.wallet.TradeWalletService;
import koinz.core.btc.wallet.WalletsManager;
import koinz.core.dao.DaoFacade;
import koinz.core.dao.burningman.BtcFeeReceiverService;
import koinz.core.dao.burningman.DelayedPayoutTxReceiverService;
import koinz.core.filter.FilterManager;
import koinz.core.offer.OpenOfferManager;
import koinz.core.provider.fee.FeeService;
import koinz.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.refund.refundagent.RefundAgentManager;
import koinz.core.trade.statistics.ReferralIdService;
import koinz.core.trade.statistics.TradeStatisticsManager;
import koinz.core.user.User;

import koinz.network.p2p.P2PService;

import koinz.common.crypto.KeyRing;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class Provider {
    private final OpenOfferManager openOfferManager;
    private final P2PService p2PService;
    private final BtcWalletService btcWalletService;
    private final BsqWalletService bsqWalletService;
    private final TradeWalletService tradeWalletService;
    private final WalletsManager walletsManager;
    private final DaoFacade daoFacade;
    private final ReferralIdService referralIdService;
    private final User user;
    private final FilterManager filterManager;
    private final AccountAgeWitnessService accountAgeWitnessService;
    private final TradeStatisticsManager tradeStatisticsManager;
    private final ArbitratorManager arbitratorManager;
    private final MediatorManager mediatorManager;
    private final RefundAgentManager refundAgentManager;
    private final KeyRing keyRing;
    private final FeeService feeService;
    private final BtcFeeReceiverService btcFeeReceiverService;
    private final DelayedPayoutTxReceiverService delayedPayoutTxReceiverService;

    @Inject
    public Provider(OpenOfferManager openOfferManager,
                    P2PService p2PService,
                    BtcWalletService btcWalletService,
                    BsqWalletService bsqWalletService,
                    TradeWalletService tradeWalletService,
                    WalletsManager walletsManager,
                    DaoFacade daoFacade,
                    ReferralIdService referralIdService,
                    User user,
                    FilterManager filterManager,
                    AccountAgeWitnessService accountAgeWitnessService,
                    TradeStatisticsManager tradeStatisticsManager,
                    ArbitratorManager arbitratorManager,
                    MediatorManager mediatorManager,
                    RefundAgentManager refundAgentManager,
                    KeyRing keyRing,
                    FeeService feeService,
                    BtcFeeReceiverService btcFeeReceiverService,
                    DelayedPayoutTxReceiverService delayedPayoutTxReceiverService) {

        this.openOfferManager = openOfferManager;
        this.p2PService = p2PService;
        this.btcWalletService = btcWalletService;
        this.bsqWalletService = bsqWalletService;
        this.tradeWalletService = tradeWalletService;
        this.walletsManager = walletsManager;
        this.daoFacade = daoFacade;
        this.referralIdService = referralIdService;
        this.user = user;
        this.filterManager = filterManager;
        this.accountAgeWitnessService = accountAgeWitnessService;
        this.tradeStatisticsManager = tradeStatisticsManager;
        this.arbitratorManager = arbitratorManager;
        this.mediatorManager = mediatorManager;
        this.refundAgentManager = refundAgentManager;
        this.keyRing = keyRing;
        this.feeService = feeService;
        this.btcFeeReceiverService = btcFeeReceiverService;
        this.delayedPayoutTxReceiverService = delayedPayoutTxReceiverService;
    }
}
