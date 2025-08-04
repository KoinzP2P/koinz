package koinz.core.trade.protocol;

import koinz.core.offer.Offer;
import koinz.core.trade.TradeManager;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.P2PService;

import koinz.common.proto.persistable.PersistablePayload;
import koinz.common.taskrunner.Model;

public interface ProtocolModel<T extends TradePeer> extends Model, PersistablePayload {
    void applyTransient(Provider provider, TradeManager tradeManager, Offer offer);

    P2PService getP2PService();

    T getTradePeer();

    void setTempTradingPeerNodeAddress(NodeAddress nodeAddress);

    NodeAddress getTempTradingPeerNodeAddress();

    TradeManager getTradeManager();

    void setTradeMessage(TradeMessage tradeMessage);

    NodeAddress getMyNodeAddress();
}
