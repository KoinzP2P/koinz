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

import org.junit.jupiter.api.Test;
import koinz.asset.AbstractAssetTest;

public class MyceTest extends AbstractAssetTest {

    public MyceTest() {
        super(new Myce());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("MCgtattGUWUBAV8n2JAa4uDWCRvbZeVcaD");
        assertValidAddress("MRV2dtuxwo8b1JSkwBXN3uGypJxp85Hbqn");
        assertValidAddress("MEUvfCySnAqzuNvbRh2SZCbSro8e2dxLYK");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("MCgtattGUWUBAV8n2JAa4uDWCRvbZeVcaDx");
        assertInvalidAddress("AUV2dtuxwo8b1JSkwBXN3uGypJxp85Hbqn");
        assertInvalidAddress("SEUvfCySnAqzuNvbRh2SZCbSro8e2dxLYK");
    }
}
