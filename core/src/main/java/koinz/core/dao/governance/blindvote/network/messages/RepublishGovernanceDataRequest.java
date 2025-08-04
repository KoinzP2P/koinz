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

package koinz.core.dao.governance.blindvote.network.messages;

import koinz.network.p2p.DirectMessage;
import koinz.network.p2p.storage.payload.CapabilityRequiringPayload;

import koinz.common.app.Capabilities;
import koinz.common.app.Capability;
import koinz.common.app.Version;
import koinz.common.proto.network.NetworkEnvelope;

import lombok.EqualsAndHashCode;
import lombok.Getter;


// This message is sent only to full DAO nodes
@EqualsAndHashCode(callSuper = true)
@Getter
public final class RepublishGovernanceDataRequest extends NetworkEnvelope implements DirectMessage, CapabilityRequiringPayload {

    public RepublishGovernanceDataRequest() {
        this(Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private RepublishGovernanceDataRequest(int messageVersion) {
        super(messageVersion);
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setRepublishGovernanceDataRequest(protobuf.RepublishGovernanceDataRequest.newBuilder())
                .build();
    }

    public static NetworkEnvelope fromProto(protobuf.RepublishGovernanceDataRequest proto, int messageVersion) {
        return new RepublishGovernanceDataRequest(messageVersion);
    }

    @Override
    public Capabilities getRequiredCapabilities() {
        return new Capabilities(Capability.DAO_FULL_NODE);
    }

    @Override
    public String toString() {
        return "RepublishGovernanceDataRequest{" +
                "\n} " + super.toString();
    }
}
