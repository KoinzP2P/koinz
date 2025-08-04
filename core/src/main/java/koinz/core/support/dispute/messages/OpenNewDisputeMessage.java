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

package koinz.core.support.dispute.messages;

import koinz.core.proto.CoreProtoResolver;
import koinz.core.support.SupportType;
import koinz.core.support.dispute.Dispute;

import koinz.network.p2p.NodeAddress;

import koinz.common.app.Version;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public final class OpenNewDisputeMessage extends DisputeMessage {
    private final Dispute dispute;
    private final NodeAddress senderNodeAddress;

    public OpenNewDisputeMessage(Dispute dispute,
                                 NodeAddress senderNodeAddress,
                                 String uid,
                                 SupportType supportType) {
        this(dispute,
                senderNodeAddress,
                uid,
                Version.getP2PMessageVersion(),
                supportType);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private OpenNewDisputeMessage(Dispute dispute,
                                  NodeAddress senderNodeAddress,
                                  String uid,
                                  int messageVersion,
                                  SupportType supportType) {
        super(messageVersion, uid, supportType);
        this.dispute = dispute;
        this.senderNodeAddress = senderNodeAddress;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setOpenNewDisputeMessage(protobuf.OpenNewDisputeMessage.newBuilder()
                        .setUid(uid)
                        .setDispute(dispute.toProtoMessage())
                        .setSenderNodeAddress(senderNodeAddress.toProtoMessage())
                        .setType(SupportType.toProtoMessage(supportType)))
                .build();
    }

    public static OpenNewDisputeMessage fromProto(protobuf.OpenNewDisputeMessage proto,
                                                  CoreProtoResolver coreProtoResolver,
                                                  int messageVersion) {
        return new OpenNewDisputeMessage(Dispute.fromProto(proto.getDispute(), coreProtoResolver),
                NodeAddress.fromProto(proto.getSenderNodeAddress()),
                proto.getUid(),
                messageVersion,
                SupportType.fromProto(proto.getType()));
    }

    @Override
    public String getTradeId() {
        return dispute.getTradeId();
    }

    @Override
    public String toString() {
        return "OpenNewDisputeMessage{" +
                "\n     dispute=" + dispute +
                ",\n     senderNodeAddress=" + senderNodeAddress +
                ",\n     OpenNewDisputeMessage.uid='" + uid + '\'' +
                ",\n     messageVersion=" + messageVersion +
                ",\n     supportType=" + supportType +
                "\n} " + super.toString();
    }
}
