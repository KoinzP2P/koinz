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

package koinz.core.dao.governance.blindvote.storage;

import koinz.network.p2p.storage.P2PDataStorage;
import koinz.network.p2p.storage.payload.PersistableNetworkPayload;
import koinz.network.p2p.storage.persistence.MapStoreService;

import koinz.common.config.Config;
import koinz.common.persistence.PersistenceManager;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.File;

import java.util.Map;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlindVoteStorageService extends MapStoreService<BlindVoteStore, PersistableNetworkPayload> {
    private static final String FILE_NAME = "BlindVoteStore";

    // At startup it is true, so the data we receive from the seed node are not checked against the phase as we have
    // not started up the DAO domain at that moment.
    @Setter
    private boolean notInVoteRevealPhase = true;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public BlindVoteStorageService(@Named(Config.STORAGE_DIR) File storageDir,
                                   PersistenceManager<BlindVoteStore> persistenceManager) {
        super(storageDir, persistenceManager);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getFileName() {
        return FILE_NAME;
    }

    @Override
    protected void initializePersistenceManager() {
        persistenceManager.initialize(store, PersistenceManager.Source.NETWORK);
    }

    @Override
    public Map<P2PDataStorage.ByteArray, PersistableNetworkPayload> getMap() {
        return store.getMap();
    }

    @Override
    public boolean canHandle(PersistableNetworkPayload payload) {
        return payload instanceof BlindVotePayload && notInVoteRevealPhase;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected BlindVoteStore createStore() {
        return new BlindVoteStore();
    }
}
