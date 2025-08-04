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

public class PENGTest extends AbstractAssetTest {

    public PENGTest() {
        super(new PENG());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("P9KqnVS9UpcJmLtCF1j4SV3fcccMuGEbhs");
        assertValidAddress("PUTXyY73s3HDvEzNJQekXMnjNjTrzFBzE2");
        assertValidAddress("PEfabj5DzRj6WBpc3jtVDorsVM5nddDxie");
        assertValidAddress("PAvXbSUAdCyd9MEtDPYYSmezmeLGL1HcjG");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("Pp9KqnVS9UpcJmLtCF1j4SV3fcccMuGEbhs");
        assertInvalidAddress("PqUTXyY73s3HDvEzNJQekXMnjNjTrzFBzE2");
        assertInvalidAddress("P8Efabj5DzRj6WBpc3jtVDorsVM5nddDxie");
        assertInvalidAddress("P9AvXbSUAdCyd9MEtDPYYSmezmeLGL1HcjG");
    }
}
