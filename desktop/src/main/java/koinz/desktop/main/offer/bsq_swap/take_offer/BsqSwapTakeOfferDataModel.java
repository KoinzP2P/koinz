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

package koinz.desktop.main.offer.bsq_swap.take_offer;

import koinz.desktop.main.offer.bsq_swap.BsqSwapOfferDataModel;
import koinz.desktop.main.offer.offerbook.OfferBook;

import koinz.core.offer.Offer;
import koinz.core.offer.bsq_swap.BsqSwapTakeOfferModel;
import koinz.core.trade.bisq_v1.TradeResultHandler;
import koinz.core.trade.model.bsq_swap.BsqSwapTrade;
import koinz.core.user.User;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.CoinFormatter;

import koinz.network.p2p.P2PService;

import koinz.common.handlers.ErrorMessageHandler;

import org.bitcoinj.core.Coin;

import com.google.inject.Inject;

import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;

class BsqSwapTakeOfferDataModel extends BsqSwapOfferDataModel {
    // We use the BsqSwapTakeOfferModel from core as delegate
    // This contains all non UI specific domain aspects and is re-used from the API.
    private final BsqSwapTakeOfferModel bsqSwapTakeOfferModel;

    private final OfferBook offerBook;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    BsqSwapTakeOfferDataModel(BsqSwapTakeOfferModel bsqSwapTakeOfferModel,
                              OfferBook offerBook,
                              User user,
                              @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                              P2PService p2PService) {
        super(bsqSwapTakeOfferModel,
                user,
                p2PService,
                btcFormatter);
        this.bsqSwapTakeOfferModel = bsqSwapTakeOfferModel;

        this.offerBook = offerBook;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    void initWithData(Offer offer) {
        bsqSwapTakeOfferModel.initWithData(offer);
    }

    void onShowFeeInfoScreen() {
        calculateInputAndPayout();
    }

    void removeOffer() {
        offerBook.removeOffer(checkNotNull(getOffer()));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // UI actions
    ///////////////////////////////////////////////////////////////////////////////////////////

    void onTakeOffer(TradeResultHandler<BsqSwapTrade> tradeResultHandler,
                     ErrorMessageHandler warningHandler,
                     ErrorMessageHandler errorHandler) {
        bsqSwapTakeOfferModel.onTakeOffer(tradeResultHandler, warningHandler, errorHandler, false);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Setter
    ///////////////////////////////////////////////////////////////////////////////////////////

    void applyAmount(Coin amount) {
        bsqSwapTakeOfferModel.applyAmount(amount);
        setBtcAmount(Coin.valueOf(Math.min(amount.value, getMaxTradeLimit())));
        calculateVolume();
        calculateInputAndPayout();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    boolean isAmountLargerThanOfferAmount() {
        if (getBtcAmount().get() != null && getOffer() != null)
            return getBtcAmount().get().isGreaterThan(getOffer().getAmount());
        return true;
    }

    Offer getOffer() {
        return bsqSwapTakeOfferModel.getOffer();
    }
}
