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

import koinz.network.p2p.storage.payload.ProtectedMailboxStorageEntry;
import koinz.network.p2p.storage.payload.ProtectedStorageEntry;

import koinz.common.app.Version;
import koinz.common.proto.network.NetworkProtoResolver;

import com.google.protobuf.Message;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public final class AddDataMessage extends BroadcastMessage {
    private final ProtectedStorageEntry protectedStorageEntry;

    public AddDataMessage(ProtectedStorageEntry protectedStorageEntry) {
        this(protectedStorageEntry, Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private AddDataMessage(ProtectedStorageEntry protectedStorageEntry, int messageVersion) {
        super(messageVersion);
        this.protectedStorageEntry = protectedStorageEntry;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        protobuf.StorageEntryWrapper.Builder builder = protobuf.StorageEntryWrapper.newBuilder();
        final Message message = protectedStorageEntry.toProtoMessage();
        if (protectedStorageEntry instanceof ProtectedMailboxStorageEntry)
            builder.setProtectedMailboxStorageEntry((protobuf.ProtectedMailboxStorageEntry) message);
        else
            builder.setProtectedStorageEntry((protobuf.ProtectedStorageEntry) message);

        return getNetworkEnvelopeBuilder()
                .setAddDataMessage(protobuf.AddDataMessage.newBuilder()
                        .setEntry(builder))
                .build();
    }

    public static AddDataMessage fromProto(protobuf.AddDataMessage proto, NetworkProtoResolver resolver, int messageVersion) {
        return new AddDataMessage((ProtectedStorageEntry) resolver.fromProto(proto.getEntry()), messageVersion);
    }
}
