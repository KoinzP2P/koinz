package koinz.core.api.model;

import koinz.common.Payload;

import lombok.Getter;

@Getter
public class BalancesInfo implements Payload {

    // Getter names are shortened for readability's sake, i.e.,
    // balancesInfo.getBtc().getAvailableBalance() is cleaner than
    // balancesInfo.getBtcBalanceInfo().getAvailableBalance().
    private final BsqBalanceInfo bsq;
    private final BtcBalanceInfo btc;

    public BalancesInfo(BsqBalanceInfo bsq, BtcBalanceInfo btc) {
        this.bsq = bsq;
        this.btc = btc;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public koinz.proto.grpc.BalancesInfo toProtoMessage() {
        return koinz.proto.grpc.BalancesInfo.newBuilder()
                .setBsq(bsq.toProtoMessage())
                .setBtc(btc.toProtoMessage())
                .build();
    }

    public static BalancesInfo fromProto(koinz.proto.grpc.BalancesInfo proto) {
        return new BalancesInfo(BsqBalanceInfo.fromProto(proto.getBsq()),
                BtcBalanceInfo.fromProto(proto.getBtc()));
    }

    @Override
    public String toString() {
        return "BalancesInfo{" + "\n" +
                "  " + bsq.toString() + "\n" +
                ", " + btc.toString() + "\n" +
                '}';
    }
}
