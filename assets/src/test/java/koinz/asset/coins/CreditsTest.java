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

import koinz.asset.AbstractAssetTest;

import org.junit.jupiter.api.Test;

public class CreditsTest extends AbstractAssetTest {

    public CreditsTest() {
        super(new Credits());
    }


    @Test
    public void testValidAddresses() {
        assertValidAddress("CfXBhPhSxx1wqxGQCryfgn6iU1M1XFUuCo");
        assertValidAddress("CMde7YERCFWkCL2W5i8uyJmnpCVj8Chhww");
        assertValidAddress("CcbqU3MLZuGAED2CuhUkquyJxKaSJqv6Vb");
        assertValidAddress("CKaig5pznaUgiLqe6WkoCNGagNMhNLtqhK");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("1fXBhPhSxx1wqxGQCryfgn6iU1M1XFUuCo32");
        assertInvalidAddress("CMde7YERCFWkCL2W5i8uyJmnpCVj8Chh");
        assertInvalidAddress("CcbqU3MLZuGAED2CuhUkquyJxKaSJqv6V6#");
        assertInvalidAddress("bKaig5pznaUgiLqe6WkoCNGagNMhNLtqhKkggg");
    }
}
