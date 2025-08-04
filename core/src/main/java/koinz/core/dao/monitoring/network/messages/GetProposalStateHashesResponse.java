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

package koinz.core.dao.monitoring.network.messages;

import koinz.core.dao.monitoring.model.ProposalStateHash;

import koinz.network.p2p.InitialDataRequest;

import koinz.common.app.Version;
import koinz.common.proto.network.NetworkEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class GetProposalStateHashesResponse extends GetStateHashesResponse<ProposalStateHash> {
    public GetProposalStateHashesResponse(List<ProposalStateHash> proposalStateHashes, int requestNonce) {
        super(proposalStateHashes, requestNonce, Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private GetProposalStateHashesResponse(List<ProposalStateHash> proposalStateHashes,
                                           int requestNonce,
                                           int messageVersion) {
        super(proposalStateHashes, requestNonce, messageVersion);
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setGetProposalStateHashesResponse(protobuf.GetProposalStateHashesResponse.newBuilder()
                        .addAllStateHashes(stateHashes.stream()
                                .map(ProposalStateHash::toProtoMessage)
                                .collect(Collectors.toList()))
                        .setRequestNonce(requestNonce))
                .build();
    }

    public static NetworkEnvelope fromProto(protobuf.GetProposalStateHashesResponse proto, int messageVersion) {
        return new GetProposalStateHashesResponse(proto.getStateHashesList().isEmpty() ?
                new ArrayList<>() :
                proto.getStateHashesList().stream()
                        .map(ProposalStateHash::fromProto)
                        .collect(Collectors.toList()),
                proto.getRequestNonce(),
                messageVersion);
    }

    @Override
    public Class<? extends InitialDataRequest> associatedRequest() {
        return GetProposalStateHashesRequest.class;
    }
}
