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

package koinz.core.dao.monitoring.network.messages;

import koinz.network.p2p.DirectMessage;
import koinz.network.p2p.InitialDataRequest;
import koinz.network.p2p.storage.payload.CapabilityRequiringPayload;

import koinz.common.app.Capabilities;
import koinz.common.app.Capability;
import koinz.common.proto.network.NetworkEnvelope;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public abstract class GetStateHashesRequest extends NetworkEnvelope implements DirectMessage,
        CapabilityRequiringPayload, InitialDataRequest {
    protected final int height;
    protected final int nonce;

    protected GetStateHashesRequest(int height, int nonce, int messageVersion) {
        super(messageVersion);
        this.height = height;
        this.nonce = nonce;
    }

    @Override
    public Capabilities getRequiredCapabilities() {
        return new Capabilities(Capability.DAO_STATE);
    }

    @Override
    public String toString() {
        return "GetStateHashesRequest{" +
                ",\n     height=" + height +
                ",\n     nonce=" + nonce +
                "\n} " + super.toString();
    }
}
