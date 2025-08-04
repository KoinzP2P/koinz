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

package koinz.core.dao.burningman.accounting.balance;

import koinz.common.util.DateUtil;

import java.util.Date;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class MonthlyBalanceEntry implements BalanceEntry {
    private final long receivedBtc;
    private final long burnedBsq;
    private final Date date;
    private final Date month;
    private final Set<Type> types;

    public MonthlyBalanceEntry(long receivedBtc, long burnedBsq, Date date, Set<Type> types) {
        this.receivedBtc = receivedBtc;
        this.burnedBsq = burnedBsq;
        this.date = date;
        month = DateUtil.getStartOfMonth(date);
        this.types = types;
    }
}
