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

package koinz.network.p2p;

import koinz.common.app.Version;
import koinz.common.proto.network.NetworkEnvelope;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public final class CloseConnectionMessage extends NetworkEnvelope {
    private final String reason;

    public CloseConnectionMessage(String reason) {
        this(reason, Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private CloseConnectionMessage(String reason, int messageVersion) {
        super(messageVersion);
        this.reason = reason;
    }


    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setCloseConnectionMessage(protobuf.CloseConnectionMessage.newBuilder()
                        .setReason(reason))
                .build();
    }

    public static CloseConnectionMessage fromProto(protobuf.CloseConnectionMessage proto, int messageVersion) {
        return new CloseConnectionMessage(proto.getReason(), messageVersion);
    }
}
