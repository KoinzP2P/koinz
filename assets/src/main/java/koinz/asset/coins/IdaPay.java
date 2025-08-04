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
import koinz.asset.Base58AddressValidator;
import koinz.asset.Coin;
import koinz.asset.NetworkParametersAdapter;

public class IdaPay extends Coin {

    public IdaPay() {
        super("IdaPay", "IDA", new IdaPayAddressValidator());
    }


    public static class IdaPayAddressValidator extends Base58AddressValidator {

        public IdaPayAddressValidator() {
            super(new IdaPayParams());
        }

        @Override
        public AddressValidationResult validate(String address) {
            if (!address.matches("^[CD][a-km-zA-HJ-NP-Z1-9]{33}$"))
                return AddressValidationResult.invalidStructure();

            return super.validate(address);
        }
    }


    public static class IdaPayParams extends NetworkParametersAdapter {

        public IdaPayParams() {
            super();
            addressHeader = 29;
            p2shHeader = 36;
        }
    }
}
