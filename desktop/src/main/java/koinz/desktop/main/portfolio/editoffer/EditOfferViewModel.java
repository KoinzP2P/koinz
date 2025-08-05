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

package koinz.desktop.main.portfolio.editoffer;

import koinz.desktop.Navigation;
import koinz.desktop.main.offer.OfferViewUtil;
import koinz.desktop.main.offer.bisq_v1.MutableOfferViewModel;
import koinz.desktop.util.validation.BsqValidator;
import koinz.desktop.util.validation.BtcValidator;
import koinz.desktop.util.validation.FiatVolumeValidator;
import koinz.desktop.util.validation.SecurityDepositValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.offer.OfferUtil;
import koinz.core.offer.OpenOffer;
import koinz.core.provider.price.PriceFeedService;
import koinz.core.user.Preferences;
import koinz.core.util.FormattingUtils;
import koinz.core.util.PriceUtil;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.AltcoinValidator;
import koinz.core.util.validation.FiatPriceValidator;

import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.handlers.ResultHandler;

import com.google.inject.Inject;

import javax.inject.Named;

class EditOfferViewModel extends MutableOfferViewModel<EditOfferDataModel> {

    @Inject
    public EditOfferViewModel(EditOfferDataModel dataModel,
                              FiatVolumeValidator fiatVolumeValidator,
                              FiatPriceValidator fiatPriceValidator,
                              AltcoinValidator altcoinValidator,
                              BtcValidator btcValidator,
                              BsqValidator bsqValidator,
                              SecurityDepositValidator securityDepositValidator,
                              PriceFeedService priceFeedService,
                              AccountAgeWitnessService accountAgeWitnessService,
                              Navigation navigation,
                              Preferences preferences,
                              @Named(FormattingUtils.BTC_FORMATTER_KEY) CoinFormatter btcFormatter,
                              BsqFormatter bsqFormatter,
                              OfferUtil offerUtil) {
        super(dataModel,
                fiatVolumeValidator,
                fiatPriceValidator,
                altcoinValidator,
                btcValidator,
                bsqValidator,
                securityDepositValidator,
                priceFeedService,
                accountAgeWitnessService,
                navigation,
                preferences,
                btcFormatter,
                bsqFormatter,
                offerUtil);
        syncMinAmountWithAmount = false;
    }

    @Override
    public void activate() {
        super.activate();

        dataModel.populateData();

        long triggerPriceAsLong = dataModel.getTriggerPrice();
        dataModel.setTriggerPrice(triggerPriceAsLong);
        if (triggerPriceAsLong > 0) {
            triggerPrice.set(PriceUtil.formatMarketPrice(triggerPriceAsLong, dataModel.getCurrencyCode()));
        } else {
            triggerPrice.set("");
        }
        onTriggerPriceTextFieldChanged();
    }

    public void applyOpenOffer(OpenOffer openOffer) {
        dataModel.reset();
        dataModel.applyOpenOffer(openOffer);
    }

    public void onStartEditOffer(ErrorMessageHandler errorMessageHandler) {
        dataModel.onStartEditOffer(errorMessageHandler);
    }

    public void onPublishOffer(ResultHandler resultHandler, ErrorMessageHandler errorMessageHandler) {
        dataModel.onPublishOffer(resultHandler, errorMessageHandler);
    }

    public void onCancelEditOffer(ErrorMessageHandler errorMessageHandler) {
        dataModel.onCancelEditOffer(errorMessageHandler);
    }

    public void onInvalidateMarketPriceMargin() {
        marketPriceMargin.set(FormattingUtils.formatToPercent(dataModel.getMarketPriceMargin()));
    }

    public void onInvalidatePrice() {
        price.set(FormattingUtils.formatPrice(null));
        price.set(FormattingUtils.formatPrice(dataModel.getPrice().get()));
    }

    public boolean isSecurityDepositValid() {
        return securityDepositValidator.validate(buyerSecurityDeposit.get()).isValid;
    }

    @Override
    public void triggerFocusOutOnAmountFields() {
        // do not update BTC Amount or minAmount here
        // issue 2798: "after a few edits of offer the BTC amount has increased"
    }

    public boolean isShownAsSellOffer() {
        return OfferViewUtil.isShownAsSellOffer(getTradeCurrency(), dataModel.getDirection());
    }
}
