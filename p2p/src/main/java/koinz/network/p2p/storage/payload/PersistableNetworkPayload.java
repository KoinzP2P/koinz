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

package koinz.network.p2p.storage.payload;

import koinz.common.proto.ProtoResolver;
import koinz.common.proto.network.NetworkPayload;
import koinz.common.proto.persistable.PersistablePayload;


/**
 * Marker interface for NetworkPayload which gets persisted in PersistableNetworkPayloadMap.
 * We store it as a list in PB to keep storage size small (map would use hash as key which is in data object anyway).
 * Not using a map also give more tolerance with data structure changes.
 * This data structure does not use a verification of the owners signature. ProtectedStoragePayload is used if that is required.
 * Currently we use it only for the AccountAgeWitness and TradeStatistics data.
 * It is used for an append only data storage because removal would require owner verification.
 */
public interface PersistableNetworkPayload extends NetworkPayload, PersistablePayload {

    static PersistableNetworkPayload fromProto(protobuf.PersistableNetworkPayload payload, ProtoResolver resolver) {
        return (PersistableNetworkPayload) resolver.fromProto(payload);
    }

    protobuf.PersistableNetworkPayload toProtoMessage();

    // Hash which will be used as key in the in-memory hashMap
    byte[] getHash();

    boolean verifyHashSize();
}
