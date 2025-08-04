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

package koinz.core.dao.state.model.governance;

import koinz.core.dao.governance.ConsensusCritical;
import koinz.core.dao.state.model.ImmutableDaoStateModel;

import koinz.common.Proto;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;

@Value
public class MeritList implements Proto, ConsensusCritical, ImmutableDaoStateModel {
    private final List<Merit> list;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public protobuf.MeritList toProtoMessage() {
        return getBuilder().build();
    }

    public protobuf.MeritList.Builder getBuilder() {
        return protobuf.MeritList.newBuilder()
                .addAllMerit(getList().stream()
                        .map(Merit::toProtoMessage)
                        .collect(Collectors.toList()));
    }

    public static MeritList fromProto(protobuf.MeritList proto) {
        return new MeritList(new ArrayList<>(proto.getMeritList().stream()
                .map(Merit::fromProto)
                .collect(Collectors.toList())));
    }

    public static MeritList getMeritListFromBytes(byte[] bytes) throws InvalidProtocolBufferException {
        return MeritList.fromProto(protobuf.MeritList.parseFrom(bytes));
    }
}
