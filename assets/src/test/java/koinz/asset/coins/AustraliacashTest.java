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

public class AustraliacashTest extends AbstractAssetTest {

    public AustraliacashTest() {
        super(new Australiacash());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("AYf2TGCoQ15HatyE99R3q9jVcXHLx1zRWW");
        assertValidAddress("Aahw1A79we2jUbTaamP5YALh21GSxiWTZa");
        assertValidAddress("ALp3R9W3QsCdqaNNcULySXN31dYvfvDkRU");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("1ALp3R9W3QsCdqaNNcULySXN31dYvfvDkRU");
        assertInvalidAddress("ALp3R9W3QsCdrqaNNcULySXN31dYvfvDkRU");
        assertInvalidAddress("ALp3R9W3QsCdqaNNcULySXN31dYvfvDkRU#");
    }
}
