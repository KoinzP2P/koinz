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

package koinz.desktop.main.portfolio.presentation;

import koinz.desktop.Navigation;
import koinz.desktop.main.MainView;
import koinz.desktop.main.offer.BuyOfferView;
import koinz.desktop.main.offer.SellOfferView;
import koinz.desktop.main.offer.bisq_v1.createoffer.CreateOfferView;
import koinz.desktop.main.offer.offerbook.BsqOfferBookView;
import koinz.desktop.main.portfolio.PortfolioView;
import koinz.desktop.main.portfolio.duplicateoffer.DuplicateOfferView;

import koinz.core.offer.OfferDirection;
import koinz.core.offer.OfferPayloadBase;
import koinz.core.offer.bsq_swap.BsqSwapOfferPayload;

public class PortfolioUtil {

    public static void duplicateOffer(Navigation navigation, OfferPayloadBase offerPayload) {
        if (offerPayload instanceof BsqSwapOfferPayload) {
            // BUY Bitcoin means SELL BSQ
            var offerViewClass = offerPayload.getDirection() == OfferDirection.BUY ? SellOfferView.class : BuyOfferView.class;
            navigation.navigateToWithData(offerPayload, MainView.class, offerViewClass, BsqOfferBookView.class, CreateOfferView.class);
        } else {
            navigation.navigateToWithData(offerPayload, MainView.class, PortfolioView.class, DuplicateOfferView.class);
        }
    }
}
