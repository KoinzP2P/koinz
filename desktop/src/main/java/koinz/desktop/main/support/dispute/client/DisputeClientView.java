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

package koinz.desktop.main.support.dispute.client;

import koinz.desktop.main.overlays.windows.ContractWindow;
import koinz.desktop.main.overlays.windows.DisputeSummaryWindow;
import koinz.desktop.main.overlays.windows.TradeDetailsWindow;
import koinz.desktop.main.support.dispute.DisputeView;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.alert.PrivateNotificationManager;
import koinz.core.dao.DaoFacade;
import koinz.core.support.dispute.Dispute;
import koinz.core.support.dispute.DisputeList;
import koinz.core.support.dispute.DisputeManager;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.refund.refundagent.RefundAgentManager;
import koinz.core.trade.TradeManager;
import koinz.core.user.Preferences;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.P2PService;

import koinz.common.crypto.KeyRing;

public abstract class DisputeClientView extends DisputeView {
    public DisputeClientView(DisputeManager<? extends DisputeList<Dispute>> DisputeManager,
                             KeyRing keyRing,
                             P2PService p2PService,
                             TradeManager tradeManager,
                             CoinFormatter formatter,
                             Preferences preferences,
                             DisputeSummaryWindow disputeSummaryWindow,
                             PrivateNotificationManager privateNotificationManager,
                             ContractWindow contractWindow,
                             TradeDetailsWindow tradeDetailsWindow,
                             AccountAgeWitnessService accountAgeWitnessService,
                             MediatorManager mediatorManager,
                             RefundAgentManager refundAgentManager,
                             DaoFacade daoFacade,
                             boolean useDevPrivilegeKeys) {
        super(DisputeManager, keyRing, p2PService, tradeManager, formatter, preferences, disputeSummaryWindow,
                privateNotificationManager, contractWindow, tradeDetailsWindow,
                accountAgeWitnessService, mediatorManager, refundAgentManager, daoFacade, useDevPrivilegeKeys);
    }

    @Override
    protected DisputeView.FilterResult getFilterResult(Dispute dispute, String filterString) {
        // As we are in the client view we hide disputes where we are the agent
        if (dispute.getAgentPubKeyRing().equals(keyRing.getPubKeyRing())) {
            return FilterResult.NO_MATCH;
        }

        return super.getFilterResult(dispute, filterString);
    }

    @Override
    protected void maybeAddChatColumnForClient() {
        tableView.getColumns().add(getChatColumn());
    }

    @Override
    protected boolean senderFlag() {
        return false;
    }
}
