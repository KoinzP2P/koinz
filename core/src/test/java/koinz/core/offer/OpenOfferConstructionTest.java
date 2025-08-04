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

package koinz.core.offer;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class OpenOfferConstructionTest {
    @Test
    void openOfferInitialStateTest() {
        var openOffer = new OpenOffer(mock(Offer.class));
        assertThat(openOffer.getState(), is(equalTo(OpenOffer.State.AVAILABLE)));
    }

    @Test
    void openOfferWithTriggerPriceInitialStateTest() {
        var openOffer = new OpenOffer(mock(Offer.class), 42_000);
        assertThat(openOffer.getState(), is(equalTo(OpenOffer.State.AVAILABLE)));
    }
}
