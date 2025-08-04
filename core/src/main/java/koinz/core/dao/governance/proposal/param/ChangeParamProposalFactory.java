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

package koinz.core.dao.governance.proposal.param;

import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.dao.governance.param.Param;
import koinz.core.dao.governance.proposal.BaseProposalFactory;
import koinz.core.dao.governance.proposal.ProposalValidationException;
import koinz.core.dao.governance.proposal.ProposalWithTransaction;
import koinz.core.dao.governance.proposal.TxException;
import koinz.core.dao.state.DaoStateService;
import koinz.core.dao.state.model.governance.ChangeParamProposal;

import org.bitcoinj.core.InsufficientMoneyException;

import javax.inject.Inject;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates ChangeParamProposal and transaction.
 */
@Slf4j
public class ChangeParamProposalFactory extends BaseProposalFactory<ChangeParamProposal> {
    private Param param;
    private String paramValue;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public ChangeParamProposalFactory(BsqWalletService bsqWalletService,
                                      BtcWalletService btcWalletService,
                                      DaoStateService daoStateService,
                                      ChangeParamValidator proposalValidator) {
        super(bsqWalletService,
                btcWalletService,
                daoStateService,
                proposalValidator);
    }

    public ProposalWithTransaction createProposalWithTransaction(String name,
                                                                 String link,
                                                                 Param param,
                                                                 String paramValue)
            throws ProposalValidationException, InsufficientMoneyException, TxException {
        this.param = param;
        this.paramValue = paramValue;

        return super.createProposalWithTransaction(name, link);
    }

    @Override
    protected ChangeParamProposal createProposalWithoutTxId() {
        return new ChangeParamProposal(
                name,
                link,
                param,
                paramValue,
                new HashMap<>());
    }
}
