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

import koinz.asset.Coin;
import koinz.asset.I18n;
import koinz.asset.RegexAddressValidator;

public class Decred extends Coin {

	public Decred() {
		super("Decred", "DCR", new DcrAddressValidator());
    }
    
    public static class DcrAddressValidator extends RegexAddressValidator {

        public DcrAddressValidator() {
            super("^[Dk|Ds|De|DS|Dc|Pm][a-zA-Z0-9]{24,34}", I18n.DISPLAY_STRINGS.getString("account.altcoin.popup.validation.DCR"));
        }
    }
}
