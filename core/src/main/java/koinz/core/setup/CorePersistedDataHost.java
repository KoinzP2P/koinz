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

package koinz.core.setup;

import koinz.core.btc.model.AddressEntryList;
import koinz.core.dao.governance.ballot.BallotListService;
import koinz.core.dao.governance.blindvote.MyBlindVoteListService;
import koinz.core.dao.governance.bond.reputation.MyReputationListService;
import koinz.core.dao.governance.myvote.MyVoteListService;
import koinz.core.dao.governance.proofofburn.MyProofOfBurnListService;
import koinz.core.dao.governance.proposal.MyProposalListService;
import koinz.core.dao.state.unconfirmed.UnconfirmedBsqChangeOutputListService;
import koinz.core.offer.OpenOfferManager;
import koinz.core.support.dispute.arbitration.ArbitrationDisputeListService;
import koinz.core.support.dispute.mediation.MediationDisputeListService;
import koinz.core.support.dispute.refund.RefundDisputeListService;
import koinz.core.trade.ClosedTradableManager;
import koinz.core.trade.TradeManager;
import koinz.core.trade.bisq_v1.FailedTradesManager;
import koinz.core.trade.bsq_swap.BsqSwapTradeManager;
import koinz.core.user.Preferences;
import koinz.core.user.User;

import koinz.network.p2p.mailbox.IgnoredMailboxService;
import koinz.network.p2p.mailbox.MailboxMessageService;
import koinz.network.p2p.peers.PeerManager;
import koinz.network.p2p.storage.P2PDataStorage;
import koinz.network.p2p.storage.persistence.RemovedPayloadsService;

import koinz.common.proto.persistable.PersistedDataHost;

import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorePersistedDataHost {

    // All classes which are persisting objects need to be added here
    public static List<PersistedDataHost> getPersistedDataHosts(Injector injector) {
        List<PersistedDataHost> persistedDataHosts = new ArrayList<>();
        persistedDataHosts.add(injector.getInstance(Preferences.class));
        persistedDataHosts.add(injector.getInstance(User.class));
        persistedDataHosts.add(injector.getInstance(AddressEntryList.class));
        persistedDataHosts.add(injector.getInstance(OpenOfferManager.class));
        persistedDataHosts.add(injector.getInstance(TradeManager.class));
        persistedDataHosts.add(injector.getInstance(ClosedTradableManager.class));
        persistedDataHosts.add(injector.getInstance(BsqSwapTradeManager.class));
        persistedDataHosts.add(injector.getInstance(FailedTradesManager.class));
        persistedDataHosts.add(injector.getInstance(ArbitrationDisputeListService.class));
        persistedDataHosts.add(injector.getInstance(MediationDisputeListService.class));
        persistedDataHosts.add(injector.getInstance(RefundDisputeListService.class));
        persistedDataHosts.add(injector.getInstance(P2PDataStorage.class));
        persistedDataHosts.add(injector.getInstance(PeerManager.class));
        persistedDataHosts.add(injector.getInstance(MailboxMessageService.class));
        persistedDataHosts.add(injector.getInstance(IgnoredMailboxService.class));
        persistedDataHosts.add(injector.getInstance(RemovedPayloadsService.class));
        persistedDataHosts.add(injector.getInstance(BallotListService.class));
        persistedDataHosts.add(injector.getInstance(MyBlindVoteListService.class));
        persistedDataHosts.add(injector.getInstance(MyVoteListService.class));
        persistedDataHosts.add(injector.getInstance(MyProposalListService.class));
        persistedDataHosts.add(injector.getInstance(MyReputationListService.class));
        persistedDataHosts.add(injector.getInstance(MyProofOfBurnListService.class));
        persistedDataHosts.add(injector.getInstance(UnconfirmedBsqChangeOutputListService.class));
        return persistedDataHosts;
    }
}
