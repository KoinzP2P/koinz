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

package koinz.core.trade.protocol.bsq_swap.messages;

import koinz.core.btc.model.RawTransactionInput;
import koinz.core.trade.protocol.TradeMessage;

import koinz.network.p2p.DirectMessage;
import koinz.network.p2p.NodeAddress;

import koinz.common.app.Version;

import com.google.protobuf.ByteString;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public final class BsqSwapFinalizeTxRequest extends TradeMessage implements DirectMessage {
    private final NodeAddress senderNodeAddress;
    private final byte[] tx;
    private final List<RawTransactionInput> btcInputs;
    private final long btcChange;
    private final String bsqPayoutAddress;
    private final String btcChangeAddress;


    public BsqSwapFinalizeTxRequest(String tradeId,
                                    NodeAddress senderNodeAddress,
                                    byte[] tx,
                                    List<RawTransactionInput> btcInputs,
                                    long btcChange,
                                    String bsqPayoutAddress,
                                    String btcChangeAddress) {
        this(Version.getP2PMessageVersion(),
                tradeId,
                UUID.randomUUID().toString(),
                senderNodeAddress,
                tx,
                btcInputs,
                btcChange,
                bsqPayoutAddress,
                btcChangeAddress);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private BsqSwapFinalizeTxRequest(int messageVersion,
                                     String tradeId,
                                     String uid,
                                     NodeAddress senderNodeAddress,
                                     byte[] tx,
                                     List<RawTransactionInput> btcInputs,
                                     long btcChange,
                                     String bsqPayoutAddress,
                                     String btcChangeAddress) {
        super(messageVersion, tradeId, uid);
        this.senderNodeAddress = senderNodeAddress;
        this.tx = tx;
        this.btcInputs = btcInputs;
        this.btcChange = btcChange;
        this.bsqPayoutAddress = bsqPayoutAddress;
        this.btcChangeAddress = btcChangeAddress;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        return getNetworkEnvelopeBuilder()
                .setBsqSwapFinalizeTxRequest(protobuf.BsqSwapFinalizeTxRequest.newBuilder()
                        .setTradeId(tradeId)
                        .setUid(uid)
                        .setSenderNodeAddress(senderNodeAddress.toProtoMessage())
                        .setTx(ByteString.copyFrom(tx))
                        .addAllBtcInputs(btcInputs.stream().map(RawTransactionInput::toProtoMessage).collect(
                                Collectors.toList()))
                        .setBtcChange(btcChange)
                        .setBsqPayoutAddress(bsqPayoutAddress)
                        .setBtcChangeAddress(btcChangeAddress))
                .build();
    }

    public static BsqSwapFinalizeTxRequest fromProto(protobuf.BsqSwapFinalizeTxRequest proto, int messageVersion) {
        return new BsqSwapFinalizeTxRequest(messageVersion,
                proto.getTradeId(),
                proto.getUid(),
                NodeAddress.fromProto(proto.getSenderNodeAddress()),
                proto.getTx().toByteArray(),
                proto.getBtcInputsList().stream()
                        .map(RawTransactionInput::fromProto)
                        .collect(Collectors.toList()),
                proto.getBtcChange(),
                proto.getBsqPayoutAddress(),
                proto.getBtcChangeAddress()
        );
    }

    @Override
    public String toString() {
        return "BsqSwapFinalizeTxRequest{" +
                "\r\n     senderNodeAddress=" + senderNodeAddress +
                ",\r\n     btcInputs=" + btcInputs +
                ",\r\n     btcChange=" + btcChange +
                ",\r\n     bsqPayoutAddress='" + bsqPayoutAddress + '\'' +
                ",\r\n     btcChangeAddress='" + btcChangeAddress + '\'' +
                "\r\n} " + super.toString();
    }
}
