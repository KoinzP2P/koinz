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

public class CRowdCLassicTest extends AbstractAssetTest {

    public CRowdCLassicTest() {
        super(new CRowdCLassic());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("CfvddKQHdd975N5XQgmpVGTuK9mumvDBQo");
        assertValidAddress("CU7pAhQjw2mjgQEAkxpsvAmeLU4Gs7ogQb");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("0xmnuL9poRmnuLd55bzKe7t48xtYv2bRES");
        assertInvalidAddress("cvaAgcLKrno2AC7kYhHVDC");
        assertInvalidAddress("19p49poRmnuLdnu55bzKe7t48xtYv2bRES");
        assertInvalidAddress("csabbfjqwr12fbdf2gvffbdb12vdssdcaa");
    }
}
