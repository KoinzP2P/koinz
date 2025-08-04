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

import koinz.asset.Base58AddressValidator;
import koinz.asset.Coin;
import koinz.asset.NetworkParametersAdapter;

public class Australiacash extends Coin {
    public Australiacash() {
        super("Australiacash", "AUS", new Base58AddressValidator(new AustraliacashParams()));
    }
	   public static class AustraliacashParams extends NetworkParametersAdapter {

        public AustraliacashParams() {
            addressHeader = 23;
            p2shHeader = 5;
        }
    }
}
