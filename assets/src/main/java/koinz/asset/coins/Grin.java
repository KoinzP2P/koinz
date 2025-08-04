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

package koinz.asset.coins;

import koinz.asset.AddressValidationResult;
import koinz.asset.AddressValidator;
import koinz.asset.AltCoinAccountDisclaimer;
import koinz.asset.Coin;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Bech32;

@AltCoinAccountDisclaimer("account.altcoin.popup.grin.msg")
public class Grin extends Coin {

    static String coinName = "Grin";

    public Grin() {
        super(coinName, coinName.toUpperCase(), new GrinAddressValidator());
    }

    public static class GrinAddressValidator implements AddressValidator {

        @Override
        public AddressValidationResult validate(String address) {
            try {
                Bech32.Bech32Data bechData = Bech32.decode(address);
                if (!bechData.hrp.equals(coinName.toLowerCase())) {
                    return AddressValidationResult.invalidAddress(String.format("invalid address prefix %x", bechData.hrp));
                }
                if (bechData.data.length != 52) {
                    return AddressValidationResult.invalidAddress(String.format("invalid address length %x", bechData.data.length));
                }
                return AddressValidationResult.validAddress();
            } catch (AddressFormatException e) {
                return AddressValidationResult.invalidStructure();
            }
        }
    }
}
