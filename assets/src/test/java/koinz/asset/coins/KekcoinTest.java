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

public class KekcoinTest extends AbstractAssetTest {

    public KekcoinTest() {
        super(new Kekcoin());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("KHWHFVU5ZMUfkiYEMMuXRDv1LjD2j1HJ2H");
        assertValidAddress("KSXQWsaKC9qL2e2RoeXNXY4FgQC6qUBpjD");
        assertValidAddress("KNVy3X1iuiF7Gz9a4fSYLF3RehN2yGkFvP");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("1LgfapHEPhZbRF9pMd5WPT35hFXcZS1USrW");
        assertInvalidAddress("1K5B7SDcuZvd2oUTaW9d62gwqsZkteXqA4");
        assertInvalidAddress("1GckU1XSCknLBcTGnayBVRjNsDjxqopNav");
    }
}
