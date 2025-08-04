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

import koinz.core.payment.payload.AssetsAccountPayload;
import koinz.core.payment.payload.PaymentMethod;

public abstract class AssetAccount extends PaymentAccount {
    protected AssetAccount(PaymentMethod paymentMethod) {
        super(paymentMethod);
    }

    public void setAddress(String address) {
        ((AssetsAccountPayload) paymentAccountPayload).setAddress(address);
    }

    public String getAddress() {
        return ((AssetsAccountPayload) paymentAccountPayload).getAddress();
    }
}
