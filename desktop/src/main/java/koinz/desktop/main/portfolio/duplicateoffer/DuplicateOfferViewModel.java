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

package koinz.desktop.main.portfolio.duplicateoffer;

import koinz.desktop.Navigation;
import koinz.desktop.main.offer.bisq_v1.MutableOfferViewModel;
import koinz.desktop.util.validation.BsqValidator;
import koinz.desktop.util.validation.BtcValidator;
import koinz.desktop.util.validation.FiatVolumeValidator;
import koinz.desktop.util.validation.SecurityDepositValidator;

import koinz.core.account.witness.AccountAgeWitnessService;
import koinz.core.offer.Offer;
import koinz.core.offer.OfferUtil;
import koinz.core.offer.bisq_v1.OfferPayload;
import koinz.core.provider.price.PriceFeedService;
import koinz.core.user.Preferences;
import koinz.core.util.FormattingUtils;
import koinz.core.util.coin.BsqFormatter;
import koinz.core.util.coin.CoinFormatter;
import koinz.core.util.validation.AltcoinValidator;
import koinz.core.util.validation.FiatPriceValidator;

import com.google.inject.Inject;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DuplicateOfferViewModel extends MutableOfferViewModel<DuplicateOfferDataModel> {

    @Inject
    public DuplicateOfferViewModel(DuplicateOfferDataModel dataModel,
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

    public void initWithData(OfferPayload offerPayload) {
        this.offer = new Offer(offerPayload);
        offer.setPriceFeedService(priceFeedService);
    }

    @Override
    public void activate() {
        super.activate();
        dataModel.populateData(offer);
        triggerFocusOutOnAmountFields();
        onFocusOutPriceAsPercentageTextField(true, false);
    }
}
