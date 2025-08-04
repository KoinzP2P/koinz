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

package koinz.core.api;

import koinz.core.api.exception.NotAvailableException;
import koinz.core.support.SupportType;
import koinz.core.support.dispute.mediation.mediator.Mediator;
import koinz.core.support.dispute.mediation.mediator.MediatorManager;
import koinz.core.support.dispute.refund.refundagent.RefundAgent;
import koinz.core.support.dispute.refund.refundagent.RefundAgentManager;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.P2PService;

import koinz.common.app.DevEnv;
import koinz.common.config.Config;
import koinz.common.crypto.KeyRing;

import org.bitcoinj.core.ECKey;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static koinz.core.support.SupportType.ARBITRATION;
import static koinz.core.support.SupportType.MEDIATION;
import static koinz.core.support.SupportType.REFUND;
import static koinz.core.support.SupportType.TRADE;
import static java.lang.String.format;
import static java.net.InetAddress.getLoopbackAddress;
import static java.util.Arrays.asList;

@Singleton
@Slf4j
class CoreDisputeAgentsService {

    private final Config config;
    private final KeyRing keyRing;
    private final MediatorManager mediatorManager;
    private final RefundAgentManager refundAgentManager;
    private final P2PService p2PService;
    private final NodeAddress nodeAddress;
    private final List<String> languageCodes;

    @Inject
    public CoreDisputeAgentsService(Config config,
                                    KeyRing keyRing,
                                    MediatorManager mediatorManager,
                                    RefundAgentManager refundAgentManager,
                                    P2PService p2PService) {
        this.config = config;
        this.keyRing = keyRing;
        this.mediatorManager = mediatorManager;
        this.refundAgentManager = refundAgentManager;
        this.p2PService = p2PService;
        this.nodeAddress = new NodeAddress(getLoopbackAddress().getHostAddress(), config.nodePort);
        this.languageCodes = asList("de", "en", "es", "fr");
    }

    void registerDisputeAgent(String disputeAgentType, String registrationKey) {
        if (!p2PService.isBootstrapped())
            throw new NotAvailableException("p2p service is not bootstrapped yet");

        if (config.getBaseCurrencyNetwork().isMainnet()
                || config.getBaseCurrencyNetwork().isDaoBetaNet()
                || !config.useLocalhostForP2P)
            throw new UnsupportedOperationException("dispute agents must be registered in a Bisq UI");

        if (!registrationKey.equals(DevEnv.getDEV_PRIVILEGE_PRIV_KEY()))
            throw new IllegalArgumentException("invalid registration key");

        Optional<SupportType> supportType = getSupportType(disputeAgentType);
        if (supportType.isPresent()) {
            ECKey ecKey;
            String signature;
            switch (supportType.get()) {
                case ARBITRATION:
                    throw new UnsupportedOperationException("arbitrators must be registered in a Bisq UI");
                case MEDIATION:
                    ecKey = mediatorManager.getRegistrationKey(registrationKey);
                    signature = mediatorManager.signStorageSignaturePubKey(Objects.requireNonNull(ecKey));
                    registerMediator(nodeAddress, languageCodes, ecKey, signature);
                    return;
                case REFUND:
                    ecKey = refundAgentManager.getRegistrationKey(registrationKey);
                    signature = refundAgentManager.signStorageSignaturePubKey(Objects.requireNonNull(ecKey));
                    registerRefundAgent(nodeAddress, languageCodes, ecKey, signature);
                    return;
                case TRADE:
                    throw new UnsupportedOperationException("trade agent registration not supported");
            }
        } else {
            throw new IllegalArgumentException(format("unknown dispute agent type '%s'", disputeAgentType));
        }
    }

    private void registerMediator(NodeAddress nodeAddress,
                                  List<String> languageCodes,
                                  ECKey ecKey,
                                  String signature) {
        Mediator mediator = new Mediator(nodeAddress,
                keyRing.getPubKeyRing(),
                languageCodes,
                new Date().getTime(),
                ecKey.getPubKey(),
                signature,
                null,
                null,
                null
        );
        mediatorManager.addDisputeAgent(mediator, () -> {
        }, errorMessage -> {
        });
        mediatorManager.getDisputeAgentByNodeAddress(nodeAddress).orElseThrow(() ->
                new IllegalStateException("could not register mediator"));
    }

    private void registerRefundAgent(NodeAddress nodeAddress,
                                     List<String> languageCodes,
                                     ECKey ecKey,
                                     String signature) {
        RefundAgent refundAgent = new RefundAgent(nodeAddress,
                keyRing.getPubKeyRing(),
                languageCodes,
                new Date().getTime(),
                ecKey.getPubKey(),
                signature,
                null,
                null,
                null
        );
        refundAgentManager.addDisputeAgent(refundAgent, () -> {
        }, errorMessage -> {
        });
        refundAgentManager.getDisputeAgentByNodeAddress(nodeAddress).orElseThrow(() ->
                new IllegalStateException("could not register refund agent"));
    }

    private Optional<SupportType> getSupportType(String disputeAgentType) {
        switch (disputeAgentType.toLowerCase()) {
            case "arbitrator":
                return Optional.of(ARBITRATION);
            case "mediator":
                return Optional.of(MEDIATION);
            case "refundagent":
            case "refund_agent":
                return Optional.of(REFUND);
            case "tradeagent":
            case "trade_agent":
                return Optional.of(TRADE);
            default:
                return Optional.empty();
        }
    }
}
