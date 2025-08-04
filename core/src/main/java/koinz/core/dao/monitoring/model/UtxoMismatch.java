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

package koinz.core.dao.monitoring.model;

import lombok.Value;

@Value
public class UtxoMismatch {
    private final int height;
    private final long sumUtxo;
    private final long sumBsq;

    public UtxoMismatch(int height, long sumUtxo, long sumBsq) {
        this.height = height;
        this.sumUtxo = sumUtxo;
        this.sumBsq = sumBsq;
    }
}
