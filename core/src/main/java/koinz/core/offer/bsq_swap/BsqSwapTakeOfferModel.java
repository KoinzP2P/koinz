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

package koinz.core.offer.bsq_swap;

import koinz.core.btc.wallet.BsqWalletService;
import koinz.core.btc.wallet.BtcWalletService;
import koinz.core.filter.FilterManager;
import koinz.core.locale.Res;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferUtil;
import koinz.core.provider.fee.FeeService;
import koinz.core.trade.TradeManager;
import koinz.core.trade.bisq_v1.TradeResultHandler;
import koinz.core.trade.model.bsq_swap.BsqSwapTrade;

import koinz.common.handlers.ErrorMessageHandler;

import org.bitcoinj.core.Coin;

import com.google.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BsqSwapTakeOfferModel extends BsqSwapOfferModel {
    private final TradeManager tradeManager;
    private final FilterManager filterManager;
    @Getter
    private Offer offer;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public BsqSwapTakeOfferModel(OfferUtil offerUtil,
                                 BtcWalletService btcWalletService,
                                 BsqWalletService bsqWalletService,
                                 FeeService feeService,
                                 TradeManager tradeManager,
                                 FilterManager filterManager) {
        super(offerUtil, btcWalletService, bsqWalletService, feeService);
        this.tradeManager = tradeManager;
        this.filterManager = filterManager;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void initWithData(Offer offer) {
        super.init(offer.getDirection(), false, offer);

        this.offer = offer;
        offer.resetState();
    }

    public void doActivate() {
        super.doActivate();
        tradeManager.checkOfferAvailability(offer,
                false,
                () -> {
                },
                log::error);
    }

    public void doDeactivate() {
        super.doDeactivate();

        if (offer != null) {
            offer.cancelAvailabilityRequest();
        }
    }

    public void applyAmount(Coin amount) {
        setBtcAmount(Coin.valueOf(Math.min(amount.value, getMaxTradeLimit())));
        calculateVolume();
        calculateInputAndPayout();
    }

    public void onTakeOffer(TradeResultHandler<BsqSwapTrade> tradeResultHandler,
                            ErrorMessageHandler warningHandler,
                            ErrorMessageHandler errorHandler,
                            boolean isTakerApiUser) {
        if (filterManager.isCurrencyBanned(offer.getCurrencyCode())) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.currencyBanned"));
        } else if (filterManager.isPaymentMethodBanned(offer.getPaymentMethod())) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.paymentMethodBanned"));
        } else if (filterManager.isOfferIdBanned(offer.getId())) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.offerBlocked"));
        } else if (filterManager.isNodeAddressBanned(offer.getMakerNodeAddress())) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.nodeBlocked"));
        } else if (filterManager.requireUpdateToNewVersionForTrading()) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.requireUpdateToNewVersion"));
        } else if (tradeManager.wasOfferAlreadyUsedInTrade(offer.getId())) {
            warningHandler.handleErrorMessage(Res.get("offerbook.warning.offerWasAlreadyUsedInTrade"));
        } else {
            tradeManager.onTakeBsqSwapOffer(offer,
                    getBtcAmount().get(),
                    getTxFeePerVbyte(),
                    getMakerFee().getValue(),
                    getTakerFee().getValue(),
                    isTakerApiUser,
                    tradeResultHandler,
                    errorHandler
            );
        }
    }
}
