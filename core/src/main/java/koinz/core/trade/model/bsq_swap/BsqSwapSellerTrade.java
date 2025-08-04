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

package koinz.core.trade.model.bsq_swap;

import koinz.core.offer.Offer;
import koinz.core.trade.protocol.bsq_swap.model.BsqSwapProtocolModel;

import koinz.network.p2p.NodeAddress;

import org.bitcoinj.core.Coin;

import javax.annotation.Nullable;

public abstract class BsqSwapSellerTrade extends BsqSwapTrade {

    public BsqSwapSellerTrade(String uid,
                              Offer offer,
                              Coin amount,
                              long takeOfferDate,
                              NodeAddress peerNodeAddress,
                              long txFeePerVbyte,
                              long makerFee,
                              long takerFee,
                              BsqSwapProtocolModel bsqSwapProtocolModel,
                              @Nullable String errorMessage,
                              State state,
                              @Nullable String txId) {
        super(uid,
                offer,
                amount,
                takeOfferDate,
                peerNodeAddress,
                txFeePerVbyte,
                makerFee,
                takerFee,
                bsqSwapProtocolModel,
                errorMessage,
                state,
                txId);
    }
}
