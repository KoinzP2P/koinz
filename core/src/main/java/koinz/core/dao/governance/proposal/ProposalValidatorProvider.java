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

package koinz.core.dao.governance.proposal;

import koinz.core.dao.governance.proposal.compensation.CompensationValidator;
import koinz.core.dao.governance.proposal.confiscatebond.ConfiscateBondValidator;
import koinz.core.dao.governance.proposal.generic.GenericProposalValidator;
import koinz.core.dao.governance.proposal.param.ChangeParamValidator;
import koinz.core.dao.governance.proposal.reimbursement.ReimbursementValidator;
import koinz.core.dao.governance.proposal.removeAsset.RemoveAssetValidator;
import koinz.core.dao.governance.proposal.role.RoleValidator;
import koinz.core.dao.state.model.governance.Proposal;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProposalValidatorProvider {
    private final CompensationValidator compensationValidator;
    private final ConfiscateBondValidator confiscateBondValidator;
    private final GenericProposalValidator genericProposalValidator;
    private final ChangeParamValidator changeParamValidator;
    private final ReimbursementValidator reimbursementValidator;
    private final RemoveAssetValidator removeAssetValidator;
    private final RoleValidator roleValidator;

    @Inject
    public ProposalValidatorProvider(CompensationValidator compensationValidator,
                                     ConfiscateBondValidator confiscateBondValidator,
                                     GenericProposalValidator genericProposalValidator,
                                     ChangeParamValidator changeParamValidator,
                                     ReimbursementValidator reimbursementValidator,
                                     RemoveAssetValidator removeAssetValidator,
                                     RoleValidator roleValidator) {
        this.compensationValidator = compensationValidator;
        this.confiscateBondValidator = confiscateBondValidator;
        this.genericProposalValidator = genericProposalValidator;
        this.changeParamValidator = changeParamValidator;
        this.reimbursementValidator = reimbursementValidator;
        this.removeAssetValidator = removeAssetValidator;
        this.roleValidator = roleValidator;
    }

    public ProposalValidator getValidator(Proposal proposal) {
        return getValidator(proposal.getType());
    }

    private ProposalValidator getValidator(ProposalType proposalType) {
        switch (proposalType) {
            case COMPENSATION_REQUEST:
                return compensationValidator;
            case REIMBURSEMENT_REQUEST:
                return reimbursementValidator;
            case CHANGE_PARAM:
                return changeParamValidator;
            case BONDED_ROLE:
                return roleValidator;
            case CONFISCATE_BOND:
                return confiscateBondValidator;
            case GENERIC:
                return genericProposalValidator;
            case REMOVE_ASSET:
                return removeAssetValidator;
        }
        throw new RuntimeException("Proposal type " + proposalType.name() + " was not covered by switch case.");
    }
}
