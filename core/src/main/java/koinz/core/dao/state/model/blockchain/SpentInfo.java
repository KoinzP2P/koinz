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

package koinz.core.dao.state.model.blockchain;

import koinz.core.dao.state.model.ImmutableDaoStateModel;

import koinz.common.proto.persistable.PersistablePayload;

import lombok.Value;

import javax.annotation.concurrent.Immutable;

@Immutable
@Value
public final class SpentInfo implements PersistablePayload, ImmutableDaoStateModel {
    private final long blockHeight;
    // Spending tx
    private final String txId;
    private final int inputIndex;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static SpentInfo fromProto(protobuf.SpentInfo proto) {
        return new SpentInfo(proto.getBlockHeight(),
                proto.getTxId(),
                proto.getInputIndex());
    }

    public protobuf.SpentInfo toProtoMessage() {
        return protobuf.SpentInfo.newBuilder()
                .setBlockHeight(blockHeight)
                .setTxId(txId)
                .setInputIndex(inputIndex)
                .build();
    }

    @Override
    public String toString() {
        return "SpentInfo{" +
                "\n     blockHeight=" + blockHeight +
                ",\n     txId='" + txId + '\'' +
                ",\n     inputIndex=" + inputIndex +
                "\n}";
    }
}
