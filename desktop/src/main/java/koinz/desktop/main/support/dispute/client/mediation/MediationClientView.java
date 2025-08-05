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

package koinz.desktop.main.support.dispute.client.mediation;

import koinz.desktop.common.view.FxmlView;
import koinz.desktop.main.overlays.popups.Popup;
import koinz.desktop.main.overlays.windows.ContractWindow;
import koinz.desktop.main.overlays.windows.DisputeSummaryWindow;
import koinz.desktop.main.overlays.windows.TradeDetailsWindow;
import koinz.desktop.main.support.dispute.client.DisputeClientView;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.alert.PrivateNotificationManager;
import koinz.core.dao.DaoFacade;
import koinz.core.locale.Res;
import koinz.core.support.SupportType;
import koinz.core.support.dispute.Dispute;
import koinz.core.support.dispute.DisputeSession;
import koinz.core.support.dispute.mediation.MediationManager;
import koinz.core.support.dispute.mediation.MediationSession;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.refund.refundagent.RefundAgentManager;
import koinz.core.trade.TradeManager;
import koinz.core.trade.model.bisq_v1.Contract;
import koinz.core.user.Preferences;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.P2PService;

import koinz.common.config.Config;
import koinz.common.crypto.KeyRing;

import javax.inject.Inject;
import javax.inject.Named;

@FxmlView
public class MediationClientView extends DisputeClientView {
    @Inject
    public MediationClientView(MediationManager mediationManager,
                               KeyRing keyRing,
                               P2PService p2PService,
                               TradeManager tradeManager,
                               @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter formatter,
                               Preferences preferences,
                               DisputeSummaryWindow disputeSummaryWindow,
                               PrivateNotificationManager privateNotificationManager,
                               ContractWindow contractWindow,
                               TradeDetailsWindow tradeDetailsWindow,
                               AccountAgeWitnessService accountAgeWitnessService,
                               MediatorManager mediatorManager,
                               RefundAgentManager refundAgentManager,
                               DaoFacade daoFacade,
                               @Named(Config.USE_DEV_PRIVILEGE_KEYS) boolean useDevPrivilegeKeys) {
        super(mediationManager, keyRing, p2PService, tradeManager, formatter, preferences, disputeSummaryWindow,
                privateNotificationManager, contractWindow, tradeDetailsWindow, accountAgeWitnessService,
                mediatorManager, refundAgentManager, daoFacade, useDevPrivilegeKeys);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected void activate() {
        super.activate();
        activateReOpenDisputeListener();
    }

    @Override
    protected void deactivate() {
        super.deactivate();
        deactivateReOpenDisputeListener();
    }

    @Override
    protected SupportType getType() {
        return SupportType.MEDIATION;
    }

    @Override
    protected DisputeSession getConcreteDisputeChatSession(Dispute dispute) {
        return new MediationSession(dispute, disputeManager.isTrader(dispute));
    }

    @Override
    protected void reOpenDisputeFromButton() {
        new Popup().attention(Res.get("support.reOpenByTrader.prompt"))
                .actionButtonText(Res.get("shared.yes"))
                .onAction(() -> reOpenDispute())
                .show();
    }

    @Override
    protected NodeAddress getAgentNodeAddress(Contract contract) {
        return contract.getMediatorNodeAddress();
    }

    @Override
    protected void maybeAddAgentColumn() {
        tableView.getColumns().add(getAgentColumn());
    }
}
