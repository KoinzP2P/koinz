package koinz.core.account.witness;

import koinz.network.p2p.storage.payload.PersistableNetworkPayload;
import koinz.network.p2p.storage.persistence.HistoricalDataStoreService;
import koinz.network.p2p.storage.persistence.PersistableNetworkPayloadStore;

import com.google.common.collect.ImmutableMap;

import java.io.File;

import java.util.Collections;
import java.util.Map;

public class DummyHistoricalDataStoreService extends HistoricalDataStoreService<AccountAgeWitnessStore> {

    public DummyHistoricalDataStoreService(File storageDir) {
        super(storageDir, null);
        store = new AccountAgeWitnessStore();
        storesByVersion = ImmutableMap.copyOf(Collections.emptyMap());
    }

    @Override
    public boolean canHandle(PersistableNetworkPayload payload) {
        return false;
    }

    @Override
    public String getFileName() {
        return "";
    }

    @Override
    protected void initializePersistenceManager() {

    }

    @Override
    protected AccountAgeWitnessStore createStore() {
        return null;
    }

    public AccountAgeWitnessStore getStore() {
        return store;
    }

    public void setStoresByVersion(Map<String, PersistableNetworkPayloadStore<? extends PersistableNetworkPayload>>
                                           storesByVersion) {
        this.storesByVersion = ImmutableMap.copyOf(storesByVersion);
    }
}

