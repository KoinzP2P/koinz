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

import koinz.asset.AltCoinAccountDisclaimer;
import koinz.asset.Coin;
import koinz.asset.RegexAddressValidator;

@AltCoinAccountDisclaimer("account.altcoin.popup.upx.msg")
public class uPlexa extends Coin {

    public uPlexa() {
        super("uPlexa", "UPX", new RegexAddressValidator("^((UPX)[1-9A-Za-z^OIl]{95}|(UPi)[1-9A-Za-z^OIl]{106}|(UmV|UmW)[1-9A-Za-z^OIl]{94})$"));
    }
}
