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

package koinz.network.p2p.storage.messages;

import koinz.network.p2p.storage.payload.ProtectedStorageEntry;

import koinz.common.app.Version;
import koinz.common.proto.network.NetworkProtoResolver;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public final class RemoveDataMessage extends BroadcastMessage {
    private final ProtectedStorageEntry protectedStorageEntry;

    public RemoveDataMessage(ProtectedStorageEntry protectedStorageEntry) {
        this(protectedStorageEntry, Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private RemoveDataMessage(ProtectedStorageEntry protectedStorageEntry,
                              int messageVersion) {
        super(messageVersion);
        this.protectedStorageEntry = protectedStorageEntry;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setRemoveDataMessage(protobuf.RemoveDataMessage.newBuilder()
                        .setProtectedStorageEntry((protobuf.ProtectedStorageEntry) protectedStorageEntry.toProtoMessage()))
                .build();
    }

    public static RemoveDataMessage fromProto(protobuf.RemoveDataMessage proto, NetworkProtoResolver resolver, int messageVersion) {
        return new RemoveDataMessage(ProtectedStorageEntry.fromProto(proto.getProtectedStorageEntry(), resolver), messageVersion);
    }
}
