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

package koinz.desktop.main.dao.monitor.daostate;

import koinz.core.dao.monitoring.model.DaoStateBlock;
import koinz.core.dao.monitoring.model.DaoStateHash;
import koinz.core.locale.Res;

import java.util.Locale;
import java.util.function.IntSupplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DaoStateBlockListItemTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(new Locale("en", "US"));
        Res.setBaseCurrencyCode("BTC");
        Res.setBaseCurrencyName("Bitcoin");
    }

    @Test
    public void testEqualsAndHashCode() {
        var block = new DaoStateBlock(new DaoStateHash(0, new byte[0], true));
        var item1 = new DaoStateBlockListItem(block, newSupplier(1));
        var item2 = new DaoStateBlockListItem(block, newSupplier(2));
        var item3 = new DaoStateBlockListItem(block, newSupplier(1));
        assertNotEquals(item1, item2);
        assertNotEquals(item2, item3);
        assertEquals(item1, item3);
        assertEquals(item1.hashCode(), item3.hashCode());
    }

    private IntSupplier newSupplier(int i) {
        //noinspection Convert2Lambda
        return new IntSupplier() {
            @Override
            public int getAsInt() {
                return i;
            }
        };
    }
}
