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

package koinz.core.offer.bsq_swap;

import koinz.core.offer.OfferDirection;
import koinz.core.offer.OfferPayloadBase;
import koinz.core.payment.BsqSwapAccount;
import koinz.core.payment.payload.PaymentMethod;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.storage.payload.CapabilityRequiringPayload;
import koinz.network.p2p.storage.payload.ProofOfWorkPayload;

import koinz.common.app.Capabilities;
import koinz.common.app.Capability;
import koinz.common.crypto.ProofOfWork;
import koinz.common.crypto.PubKeyRing;
import koinz.common.proto.ProtoUtil;
import koinz.common.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Slf4j
public final class BsqSwapOfferPayload extends OfferPayloadBase
        implements ProofOfWorkPayload, CapabilityRequiringPayload {

    public static BsqSwapOfferPayload from(BsqSwapOfferPayload original,
                                           String offerId,
                                           ProofOfWork proofOfWork) {
        return new BsqSwapOfferPayload(offerId,
                original.getDate(),
                original.getOwnerNodeAddress(),
                original.getPubKeyRing(),
                original.getDirection(),
                original.getPrice(),
                original.getAmount(),
                original.getMinAmount(),
                proofOfWork,
                original.getExtraDataMap(),
                original.getVersionNr(),
                original.getProtocolVersion()
        );
    }

    private final ProofOfWork proofOfWork;

    public BsqSwapOfferPayload(String id,
                               long date,
                               NodeAddress ownerNodeAddress,
                               PubKeyRing pubKeyRing,
                               OfferDirection direction,
                               long price,
                               long amount,
                               long minAmount,
                               ProofOfWork proofOfWork,
                               @Nullable Map<String, String> extraDataMap,
                               String versionNr,
                               int protocolVersion) {
        super(id,
                date,
                ownerNodeAddress,
                pubKeyRing,
                "KNZ",
                "BTC",
                direction,
                price,
                amount,
                minAmount,
                PaymentMethod.BSQ_SWAP_ID,
                BsqSwapAccount.ID,
                extraDataMap,
                versionNr,
                protocolVersion);

        this.proofOfWork = proofOfWork;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static protobuf.OfferDirection toProtoMessage(OfferDirection direction) {
        return protobuf.OfferDirection.valueOf(direction.name());
    }

    public static OfferDirection fromProto(protobuf.OfferDirection offerDirection) {
        return ProtoUtil.enumFromProto(OfferDirection.class, offerDirection.name());
    }

    @Override
    public protobuf.StoragePayload toProtoMessage() {
        protobuf.BsqSwapOfferPayload.Builder builder = protobuf.BsqSwapOfferPayload.newBuilder()
                .setId(id)
                .setDate(date)
                .setOwnerNodeAddress(ownerNodeAddress.toProtoMessage())
                .setPubKeyRing(pubKeyRing.toProtoMessage())
                .setDirection(toProtoMessage(direction))
                .setPrice(price)
                .setAmount(amount)
                .setMinAmount(minAmount)
                .setProofOfWork(proofOfWork.toProtoMessage())
                .setVersionNr(versionNr)
                .setProtocolVersion(protocolVersion);

        Optional.ofNullable(extraDataMap).ifPresent(builder::putAllExtraData);

        return protobuf.StoragePayload.newBuilder().setBsqSwapOfferPayload(builder).build();
    }

    public static BsqSwapOfferPayload fromProto(protobuf.BsqSwapOfferPayload proto) {
        Map<String, String> extraDataMapMap = CollectionUtils.isEmpty(proto.getExtraDataMap()) ?
                null : proto.getExtraDataMap();
        return new BsqSwapOfferPayload(proto.getId(),
                proto.getDate(),
                NodeAddress.fromProto(proto.getOwnerNodeAddress()),
                PubKeyRing.fromProto(proto.getPubKeyRing()),
                fromProto(proto.getDirection()),
                proto.getPrice(),
                proto.getAmount(),
                proto.getMinAmount(),
                ProofOfWork.fromProto(proto.getProofOfWork()),
                extraDataMapMap,
                proto.getVersionNr(),
                proto.getProtocolVersion()
        );
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // ProofOfWorkPayload
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Capabilities getRequiredCapabilities() {
        return new Capabilities(Capability.BSQ_SWAP_OFFER);
    }

    @Override
    public String toString() {
        return "BsqSwapOfferPayload{" +
                "\r\n     proofOfWork=" + proofOfWork +
                "\r\n} " + super.toString();
    }
}
