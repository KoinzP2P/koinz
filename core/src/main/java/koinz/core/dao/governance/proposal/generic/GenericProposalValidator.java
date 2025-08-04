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

package koinz.core.dao.governance.proposal.generic;

import koinz.core.dao.governance.ConsensusCritical;
import koinz.core.dao.governance.period.PeriodService;
import koinz.core.dao.governance.proposal.ProposalValidationException;
import koinz.core.dao.governance.proposal.ProposalValidator;
import koinz.core.dao.state.DaoStateService;
import koinz.core.dao.state.model.governance.Proposal;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

/**
 * Changes here can potentially break consensus!
 */
@Slf4j
public class GenericProposalValidator extends ProposalValidator implements ConsensusCritical {

    @Inject
    public GenericProposalValidator(DaoStateService daoStateService, PeriodService periodService) {
        super(daoStateService, periodService);
    }

    @Override
    public void validateDataFields(Proposal proposal) throws ProposalValidationException {
        try {
            super.validateDataFields(proposal);
        } catch (ProposalValidationException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new ProposalValidationException(throwable);
        }
    }
}
