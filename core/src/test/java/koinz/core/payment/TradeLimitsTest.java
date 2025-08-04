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

package koinz.core.payment;

import koinz.core.dao.governance.period.PeriodService;
import koinz.core.dao.state.DaoStateService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class TradeLimitsTest {
    @Test
    public void testGetFirstMonthRiskBasedTradeLimit() {
        TradeLimits tradeLimits = new TradeLimits(mock(DaoStateService.class), mock(PeriodService.class));
        long expected, result;

        expected = 0;
        result = tradeLimits.getFirstMonthRiskBasedTradeLimit(0, 1);
        assertEquals(expected, result);

        expected = 25000000;
        result = tradeLimits.getFirstMonthRiskBasedTradeLimit(100000000, 1);
        assertEquals(expected, result);

        expected = 3130000; //0.03125 -> 0.0313 -> 0.0313
        result = tradeLimits.getFirstMonthRiskBasedTradeLimit(100000000, 8);
        assertEquals(expected, result);

        expected = 6250000;
        result = tradeLimits.getFirstMonthRiskBasedTradeLimit(200000000, 8);
        assertEquals(expected, result);
    }
}
