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

import koinz.core.dao.state.model.governance.Proposal;

import org.bitcoinj.core.Transaction;

import lombok.Value;

@Value
public class ProposalWithTransaction {
    private final Proposal proposal;
    private final Transaction transaction;

    ProposalWithTransaction(Proposal proposal, Transaction transaction) {
        this.proposal = proposal;
        this.transaction = transaction;
    }
}
