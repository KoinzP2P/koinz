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
import koinz.asset.Coin;

public class Zero extends Coin {

    public Zero() {
        super("Zero", "ZER", new ZeroAddressValidator());
    }


    public static class ZeroAddressValidator implements AddressValidator {

        @Override
        public AddressValidationResult validate(String address) {
            // We only support t addresses (transparent transactions)
            if (!address.startsWith("t1"))
                return AddressValidationResult.invalidAddress("", "validation.altcoin.zAddressesNotSupported");

            return AddressValidationResult.validAddress();
        }
    }
}
