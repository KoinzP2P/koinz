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

import koinz.core.offer.bisq_v1.OfferPayload;

import koinz.common.app.Capabilities;
import koinz.common.app.Capability;
import koinz.common.config.Config;
import koinz.common.util.Utilities;

import org.bitcoinj.core.Coin;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class OfferRestrictions {
    public static final Date TOLERATED_SMALL_TRADE_AMOUNT_CHANGE_ACTIVATION_DATE = Utilities.getUTCDate(2025, GregorianCalendar.FEBRUARY, 17);

    // The date when traders who have not upgraded to a Tor v3 Node Address cannot take offers and their offers become
    // invisible.
    private static final Date REQUIRE_TOR_NODE_ADDRESS_V3_DATE = Utilities.getUTCDate(2021, GregorianCalendar.AUGUST, 15);

    public static boolean requiresNodeAddressUpdate() {
        return new Date().after(REQUIRE_TOR_NODE_ADDRESS_V3_DATE) && !Config.baseCurrencyNetwork().isRegtest();
    }

    public static Coin TOLERATED_SMALL_TRADE_AMOUNT = new Date().after(TOLERATED_SMALL_TRADE_AMOUNT_CHANGE_ACTIVATION_DATE)
            ? Coin.parseCoin("0.002")
            : Coin.parseCoin("0.01");

    static boolean hasOfferMandatoryCapability(Offer offer, Capability mandatoryCapability) {
        Map<String, String> extraDataMap = offer.getExtraDataMap();
        if (extraDataMap != null && extraDataMap.containsKey(OfferPayload.CAPABILITIES)) {
            String commaSeparatedOrdinals = extraDataMap.get(OfferPayload.CAPABILITIES);
            Capabilities capabilities = Capabilities.fromStringList(commaSeparatedOrdinals);
            return Capabilities.hasMandatoryCapability(capabilities, mandatoryCapability);
        }
        return false;
    }
}
