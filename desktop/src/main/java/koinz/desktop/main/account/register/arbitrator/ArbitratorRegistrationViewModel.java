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

package koinz.desktop.main.account.register.arbitrator;

import koinz.desktop.main.account.register.AgentRegistrationViewModel;

import koinz.core.btc.model.AddressEntry;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.support.dispute.arbitration.arbitrator.Arbitrator;
import koinz.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import koinz.core.user.User;

import koinz.network.p2p.P2PService;

import koinz.common.crypto.KeyRing;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Date;

public class ArbitratorRegistrationViewModel extends AgentRegistrationViewModel<Arbitrator, ArbitratorManager> {

    @Inject
    public ArbitratorRegistrationViewModel(ArbitratorManager arbitratorManager,
                                           User user,
                                           P2PService p2PService,
                                           BtcWalletService walletService,
                                           KeyRing keyRing) {
        super(arbitratorManager, user, p2PService, walletService, keyRing);
    }

    @Override
    protected Arbitrator getDisputeAgent(String registrationSignature,
                                         String emailAddress) {
        AddressEntry arbitratorAddressEntry = walletService.getArbitratorAddressEntry();
        return new Arbitrator(
                p2PService.getAddress(),
                arbitratorAddressEntry.getPubKey(),
                arbitratorAddressEntry.getAddressString(),
                keyRing.getPubKeyRing(),
                new ArrayList<>(languageCodes),
                new Date().getTime(),
                registrationKey.getPubKey(),
                registrationSignature,
                emailAddress,
                null,
                null
        );
    }

    @Override
    protected Arbitrator getRegisteredDisputeAgentFromUser() {
        return user.getRegisteredArbitrator();
    }
}
