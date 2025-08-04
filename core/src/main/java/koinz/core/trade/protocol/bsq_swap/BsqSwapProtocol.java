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

package koinz.core.trade.protocol.bsq_swap;


import koinz.core.trade.model.bsq_swap.BsqSwapTrade;
import koinz.core.trade.protocol.TradeProtocol;

import koinz.network.p2p.AckMessage;
import koinz.network.p2p.NodeAddress;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BsqSwapProtocol extends TradeProtocol {

    protected final BsqSwapTrade trade;

    public BsqSwapProtocol(BsqSwapTrade trade) {
        super(trade);

        this.trade = trade;
    }

    @Override
    protected void onAckMessage(AckMessage ackMessage, NodeAddress peer) {
        log.info("Received ackMessage {} from peer {}", ackMessage, peer);
    }
}
