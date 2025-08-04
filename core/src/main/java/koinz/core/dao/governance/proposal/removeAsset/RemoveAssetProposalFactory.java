/*
 * This file is part of KOINZ.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.core.dao.governance.proposal.removeAsset;

import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.dao.governance.proposal.BaseProposalFactory;
import koinz.core.dao.governance.proposal.ProposalValidationException;
import koinz.core.dao.governance.proposal.ProposalWithTransaction;
import koinz.core.dao.governance.proposal.TxException;
import koinz.core.dao.state.DaoStateService;
import koinz.core.dao.state.model.governance.RemoveAssetProposal;

import koinz.asset.Asset;

import org.bitcoinj.core.InsufficientMoneyException;

import javax.inject.Inject;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates RemoveAssetProposal and transaction.
 */
@Slf4j
public class RemoveAssetProposalFactory extends BaseProposalFactory<RemoveAssetProposal> {
    private Asset asset;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public RemoveAssetProposalFactory(BsqWalletService bsqWalletService,
                                      BtcWalletService btcWalletService,
                                      DaoStateService daoStateService,
                                      RemoveAssetValidator proposalValidator) {
        super(bsqWalletService,
                btcWalletService,
                daoStateService,
                proposalValidator);
    }

    public ProposalWithTransaction createProposalWithTransaction(String name,
                                                                 String link,
                                                                 Asset asset)
            throws ProposalValidationException, InsufficientMoneyException, TxException {
        this.asset = asset;

        return super.createProposalWithTransaction(name, link);
    }

    @Override
    protected RemoveAssetProposal createProposalWithoutTxId() {
        return new RemoveAssetProposal(
                name,
                link,
                asset.getTickerSymbol(),
                new HashMap<>());
    }
}
