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
import koinz.core.trade.model.MakerTrade;
import koinz.core.trade.model.Tradable;
import koinz.core.trade.protocol.bsq_swap.model.BsqSwapProtocolModel;

import koinz.network.p2p.NodeAddress;

import koinz.common.proto.ProtoUtil;

import org.bitcoinj.core.Coin;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;


@Slf4j
public final class BsqSwapSellerAsMakerTrade extends BsqSwapSellerTrade implements MakerTrade {
    public BsqSwapSellerAsMakerTrade(Offer offer,
                                     Coin amount,
                                     long takeOfferDate,
                                     NodeAddress peerNodeAddress,
                                     long txFeePerVbyte,
                                     long makerFee,
                                     long takerFee,
                                     BsqSwapProtocolModel bsqSwapProtocolModel) {

        super(UUID.randomUUID().toString(),
                offer,
                amount,
                takeOfferDate,
                peerNodeAddress,
                txFeePerVbyte,
                makerFee,
                takerFee,
                bsqSwapProtocolModel,
                null,
                BsqSwapTrade.State.PREPARATION,
                null);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private BsqSwapSellerAsMakerTrade(String uid,
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

    @Override
    public protobuf.Tradable toProtoMessage() {
        return protobuf.Tradable.newBuilder()
                .setBsqSwapSellerAsMakerTrade(protobuf.BsqSwapSellerAsMakerTrade.newBuilder()
                        .setBsqSwapTrade((protobuf.BsqSwapTrade) super.toProtoMessage()))
                .build();
    }

    public static Tradable fromProto(protobuf.BsqSwapSellerAsMakerTrade bsqSwapSellerAsMakerTrade) {
        var proto = bsqSwapSellerAsMakerTrade.getBsqSwapTrade();
        var uid = ProtoUtil.stringOrNullFromProto(proto.getUid());
        if (uid == null) {
            uid = UUID.randomUUID().toString();
        }
        return new BsqSwapSellerAsMakerTrade(
                uid,
                Offer.fromProto(proto.getOffer()),
                Coin.valueOf(proto.getAmount()),
                proto.getTakeOfferDate(),
                proto.hasPeerNodeAddress() ? NodeAddress.fromProto(proto.getPeerNodeAddress()) : null,
                proto.getMiningFeePerByte(),
                proto.getMakerFee(),
                proto.getTakerFee(),
                BsqSwapProtocolModel.fromProto(proto.getBsqSwapProtocolModel()),
                ProtoUtil.stringOrNullFromProto(proto.getErrorMessage()),
                State.fromProto(proto.getState()),
                ProtoUtil.stringOrNullFromProto(proto.getTxId()));
    }
}
