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

package koinz.core.fee;

import koinz.core.dao.state.DaoStateService;
import koinz.core.filter.FilterManager;
import koinz.core.offer.Offer;
import koinz.core.offer.OpenOffer;
import koinz.core.provider.mempool.FeeValidationStatus;
import koinz.core.provider.mempool.TxValidator;

import org.bitcoinj.core.Coin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

public class FeeValidationTests {
    @Test
    void createOpenBsqOffer() {
        var openOffer = new OpenOffer(mock(Offer.class));
        assertThat(openOffer.getFeeValidationStatus(), is(equalTo(FeeValidationStatus.NOT_CHECKED_YET)));
    }

    @Test
    void createOpenOfferWithTriggerPrice() {
        var openOffer = new OpenOffer(mock(Offer.class), 42_000);
        assertThat(openOffer.getFeeValidationStatus(), is(equalTo(FeeValidationStatus.NOT_CHECKED_YET)));
    }

    @Test
    void notCheckedYetStatusIsNotFail() {
        assertThat(FeeValidationStatus.NOT_CHECKED_YET.fail(), is(false));
    }

    @Test
    void txValidatorInitialStateIsNotCheckedYet() {
        var txValidator = new TxValidator(mock(DaoStateService.class),
                "a_tx_id",
                mock(Coin.class),
                true,
                106,
                mock(FilterManager.class));

        assertThat(txValidator.getStatus(), is(equalTo(FeeValidationStatus.NOT_CHECKED_YET)));
    }

    @Test
    void txValidatorSecondConstructorInitialStateIsNotCheckedYet() {
        var txValidator = new TxValidator(mock(DaoStateService.class), "a_tx_id", mock(FilterManager.class));
        assertThat(txValidator.getStatus(), is(equalTo(FeeValidationStatus.NOT_CHECKED_YET)));
    }
}
