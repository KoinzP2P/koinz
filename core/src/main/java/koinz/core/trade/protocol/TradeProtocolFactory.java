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

package koinz.core.trade.protocol;

import koinz.core.trade.model.TradeModel;
import koinz.core.trade.model.bisq_v1.BuyerAsMakerTrade;
import koinz.core.trade.model.bisq_v1.BuyerAsTakerTrade;
import koinz.core.trade.model.bisq_v1.SellerAsMakerTrade;
import koinz.core.trade.model.bisq_v1.SellerAsTakerTrade;
import koinz.core.trade.model.bsq_swap.BsqSwapBuyerAsMakerTrade;
import koinz.core.trade.model.bsq_swap.BsqSwapBuyerAsTakerTrade;
import koinz.core.trade.model.bsq_swap.BsqSwapSellerAsMakerTrade;
import koinz.core.trade.model.bsq_swap.BsqSwapSellerAsTakerTrade;
import koinz.core.trade.protocol.bisq_v1.BuyerAsMakerProtocol;
import koinz.core.trade.protocol.bisq_v1.BuyerAsTakerProtocol;
import koinz.core.trade.protocol.bisq_v1.SellerAsMakerProtocol;
import koinz.core.trade.protocol.bisq_v1.SellerAsTakerProtocol;
import koinz.core.trade.protocol.bsq_swap.BsqSwapBuyerAsMakerProtocol;
import koinz.core.trade.protocol.bsq_swap.BsqSwapBuyerAsTakerProtocol;
import koinz.core.trade.protocol.bsq_swap.BsqSwapSellerAsMakerProtocol;
import koinz.core.trade.protocol.bsq_swap.BsqSwapSellerAsTakerProtocol;

public class TradeProtocolFactory {
    public static TradeProtocol getNewTradeProtocol(TradeModel tradeModel) {
        if (tradeModel instanceof BuyerAsMakerTrade) {
            return new BuyerAsMakerProtocol((BuyerAsMakerTrade) tradeModel);
        } else if (tradeModel instanceof BuyerAsTakerTrade) {
            return new BuyerAsTakerProtocol((BuyerAsTakerTrade) tradeModel);
        } else if (tradeModel instanceof SellerAsMakerTrade) {
            return new SellerAsMakerProtocol((SellerAsMakerTrade) tradeModel);
        } else if (tradeModel instanceof SellerAsTakerTrade) {
            return new SellerAsTakerProtocol((SellerAsTakerTrade) tradeModel);
        } else if (tradeModel instanceof BsqSwapBuyerAsMakerTrade) {
            return new BsqSwapBuyerAsMakerProtocol((BsqSwapBuyerAsMakerTrade) tradeModel);
        } else if (tradeModel instanceof BsqSwapBuyerAsTakerTrade) {
            return new BsqSwapBuyerAsTakerProtocol((BsqSwapBuyerAsTakerTrade) tradeModel);
        } else if (tradeModel instanceof BsqSwapSellerAsMakerTrade) {
            return new BsqSwapSellerAsMakerProtocol((BsqSwapSellerAsMakerTrade) tradeModel);
        } else if (tradeModel instanceof BsqSwapSellerAsTakerTrade) {
            return new BsqSwapSellerAsTakerProtocol((BsqSwapSellerAsTakerTrade) tradeModel);
        } else
            throw new IllegalStateException("Trade not of expected type. Trade=" + tradeModel);
    }
}
