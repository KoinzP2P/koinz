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

package koinz.core.network.p2p.inventory;

import koinz.core.network.p2p.inventory.messages.GetInventoryRequest;
import koinz.core.network.p2p.inventory.messages.GetInventoryResponse;
import koinz.core.network.p2p.inventory.model.InventoryItem;

import koinz.network.p2p.NodeAddress;
import koinz.network.p2p.network.CloseConnectionReason;
import koinz.network.p2p.network.Connection;
import koinz.network.p2p.network.ConnectionListener;
import koinz.network.p2p.network.MessageListener;
import koinz.network.p2p.network.NetworkNode;

import koinz.common.Timer;
import koinz.common.UserThread;
import koinz.common.app.Version;
import koinz.common.handlers.ErrorMessageHandler;
import koinz.common.proto.network.NetworkEnvelope;

import java.util.Map;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetInventoryRequester implements MessageListener, ConnectionListener {
    private final static int TIMEOUT_SEC = 180;

    private final NetworkNode networkNode;
    private final NodeAddress nodeAddress;
    private final Consumer<Map<InventoryItem, String>> resultHandler;
    private final ErrorMessageHandler errorMessageHandler;
    private Timer timer;

    public GetInventoryRequester(NetworkNode networkNode,
                                 NodeAddress nodeAddress,
                                 Consumer<Map<InventoryItem, String>> resultHandler,
                                 ErrorMessageHandler errorMessageHandler) {
        this.networkNode = networkNode;
        this.nodeAddress = nodeAddress;
        this.resultHandler = resultHandler;
        this.errorMessageHandler = errorMessageHandler;
    }

    public void request() {
        networkNode.addMessageListener(this);
        networkNode.addConnectionListener(this);

        timer = UserThread.runAfter(this::onTimeOut, TIMEOUT_SEC);

        GetInventoryRequest getInventoryRequest = new GetInventoryRequest(Version.VERSION);
        networkNode.sendMessage(nodeAddress, getInventoryRequest);
    }

    private void onTimeOut() {
        errorMessageHandler.handleErrorMessage("Request timeout");
        shutDown();
    }

    @Override
    public void onMessage(NetworkEnvelope networkEnvelope, Connection connection) {
        if (networkEnvelope instanceof GetInventoryResponse) {
            connection.getPeersNodeAddressOptional().ifPresent(peer -> {
                if (peer.equals(nodeAddress)) {
                    GetInventoryResponse getInventoryResponse = (GetInventoryResponse) networkEnvelope;
                    resultHandler.accept(getInventoryResponse.getInventory());
                    shutDown();

                    // We shut down our connection after work as our node is not helpful for the network.
                    UserThread.runAfter(() -> connection.shutDown(CloseConnectionReason.CLOSE_REQUESTED_BY_PEER), 1);
                }
            });
        }
    }

    public void shutDown() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        networkNode.removeMessageListener(this);
        networkNode.removeConnectionListener(this);
    }

    @Override
    public void onConnection(Connection connection) {
    }

    @Override
    public void onDisconnect(CloseConnectionReason closeConnectionReason,
                             Connection connection) {
        connection.getPeersNodeAddressOptional().ifPresent(address -> {
            if (address.equals(nodeAddress)) {
                if (!closeConnectionReason.isIntended) {
                    errorMessageHandler.handleErrorMessage("Connected closed because of " + closeConnectionReason.name());
                }
                shutDown();
            }
        });
    }
}
