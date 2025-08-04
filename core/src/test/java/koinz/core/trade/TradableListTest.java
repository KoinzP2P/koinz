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

package koinz.core.trade;

import koinz.core.offer.Offer;
import koinz.core.offer.OpenOffer;
import koinz.core.offer.bisq_v1.OfferPayload;
import koinz.core.trade.model.TradableList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static protobuf.PersistableEnvelope.MessageCase.TRADABLE_LIST;

public class TradableListTest {

    @Test
    public void protoTesting() {
        OfferPayload offerPayload = mock(OfferPayload.class, RETURNS_DEEP_STUBS);
        TradableList<OpenOffer> openOfferTradableList = new TradableList<>();
        protobuf.PersistableEnvelope message = (protobuf.PersistableEnvelope) openOfferTradableList.toProtoMessage();
        assertEquals(message.getMessageCase(), TRADABLE_LIST);

        // test adding an OpenOffer and convert toProto
        Offer offer = new Offer(offerPayload);
        OpenOffer openOffer = new OpenOffer(offer);
        openOfferTradableList.add(openOffer);
        message = (protobuf.PersistableEnvelope) openOfferTradableList.toProtoMessage();
        assertEquals(message.getMessageCase(), TRADABLE_LIST);
        assertEquals(1, message.getTradableList().getTradableList().size());
    }
}
