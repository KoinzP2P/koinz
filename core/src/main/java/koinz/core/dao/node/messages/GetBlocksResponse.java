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

package koinz.core.dao.node.messages;

import koinz.core.dao.node.full.RawBlock;

import koinz.network.p2p.DirectMessage;
import koinz.network.p2p.ExtendedDataSizePermission;
import koinz.network.p2p.InitialDataRequest;
import koinz.network.p2p.InitialDataResponse;

import koinz.common.app.Version;
import koinz.common.proto.network.NetworkEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Getter
@Slf4j
public final class GetBlocksResponse extends NetworkEnvelope implements DirectMessage,
        ExtendedDataSizePermission, InitialDataResponse {
    private final List<RawBlock> blocks;
    private final int requestNonce;

    public GetBlocksResponse(List<RawBlock> blocks, int requestNonce) {
        this(blocks, requestNonce, Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private GetBlocksResponse(List<RawBlock> blocks, int requestNonce, int messageVersion) {
        super(messageVersion);
        this.blocks = blocks;
        this.requestNonce = requestNonce;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        protobuf.NetworkEnvelope proto = getNetworkEnvelopeBuilder()
                .setGetBlocksResponse(protobuf.GetBlocksResponse.newBuilder()
                        .addAllRawBlocks(blocks.stream()
                                .map(RawBlock::toProtoMessage)
                                .collect(Collectors.toList()))
                        .setRequestNonce(requestNonce))
                .build();
        log.info("Sending a GetBlocksResponse with {} kB", proto.getSerializedSize() / 1000d);
        return proto;
    }

    public static NetworkEnvelope fromProto(protobuf.GetBlocksResponse proto, int messageVersion) {
        List<RawBlock> list = proto.getRawBlocksList().stream()
                .map(RawBlock::fromProto)
                .collect(Collectors.toList());
        log.info("\n\n<< Received a GetBlocksResponse with {} blocks and {} kB size\n", list.size(), proto.getSerializedSize() / 1000d);
        return new GetBlocksResponse(proto.getRawBlocksList().isEmpty() ?
                new ArrayList<>() :
                list,
                proto.getRequestNonce(),
                messageVersion);
    }


    @Override
    public String toString() {
        return "GetBlocksResponse{" +
                "\n     blocks=" + blocks +
                ",\n     requestNonce=" + requestNonce +
                "\n} " + super.toString();
    }

    @Override
    public Class<? extends InitialDataRequest> associatedRequest() {
        return GetBlocksRequest.class;
    }
}
