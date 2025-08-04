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
import koinz.asset.RegexAddressValidator;

public class Ryo extends Coin {

    public Ryo() {
        super("Ryo", "RYO", new RegexAddressValidator("^((RYoL|RYoS)[1-9A-HJ-NP-Za-km-z]{95}|(RYoK)[1-9A-HJ-NP-Za-km-z]{51})$"));
    }
}
