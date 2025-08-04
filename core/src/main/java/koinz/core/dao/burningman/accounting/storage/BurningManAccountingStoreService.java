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

package koinz.core.dao.burningman.accounting.storage;

import koinz.core.dao.burningman.accounting.blockchain.AccountingBlock;
import koinz.core.dao.burningman.accounting.exceptions.BlockHashNotConnectingException;
import koinz.core.dao.burningman.accounting.exceptions.BlockHeightNotConnectingException;

import koinz.network.p2p.storage.persistence.ResourceDataStoreService;
import koinz.network.p2p.storage.persistence.StoreService;

import koinz.common.UserThread;
import koinz.common.config.Config;
import koinz.common.file.FileUtil;
import koinz.common.persistence.PersistenceManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.nio.file.Path;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class BurningManAccountingStoreService extends StoreService<BurningManAccountingStore> {
    private static final String FILE_NAME = "BurningManAccountingStore_v3";
    private volatile boolean removeAllBlocksCalled;

    @Inject
    public BurningManAccountingStoreService(ResourceDataStoreService resourceDataStoreService,
                                            @Named(Config.STORAGE_DIR) File storageDir,
                                            PersistenceManager<BurningManAccountingStore> persistenceManager) {
        super(storageDir, persistenceManager);

        resourceDataStoreService.addService(this);
    }

    protected void readFromResources(String postFix, Runnable completeHandler) {
        super.readFromResources(postFix, completeHandler);

        UserThread.runAfter(() -> {
            try {
                // Delete old BurningManAccountingStore file which was missing some data.
                FileUtil.deleteFileIfExists(Path.of(absolutePathOfStorageDir, "BurningManAccountingStore").toFile());
                FileUtil.deleteFileIfExists(Path.of(absolutePathOfStorageDir, "BurningManAccountingStore_v2").toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 5);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void requestPersistence() {
        persistenceManager.requestPersistence();
    }

    public void addIfNewBlock(AccountingBlock block) throws BlockHashNotConnectingException, BlockHeightNotConnectingException {
        if (removeAllBlocksCalled) {
            return;
        }
        store.addIfNewBlock(block);
        requestPersistence();
    }

    public void forEachBlock(Consumer<AccountingBlock> consumer) {
        store.forEachBlock(consumer);
    }

    public void purgeLastTenBlocks() {
        if (removeAllBlocksCalled) {
            return;
        }
        store.purgeLastTenBlocks();
        requestPersistence();
    }

    public void removeAllBlocks(Runnable resultHandler) {
        removeAllBlocksCalled = true;
        store.removeAllBlocks();
        persistenceManager.persistNow(resultHandler);
    }

    public void deleteStorageFile() {
        try {
            FileUtil.deleteFileIfExists(Path.of(absolutePathOfStorageDir, FILE_NAME).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<AccountingBlock> getLastBlock() {
        return store.getLastBlock();
    }

    public Optional<AccountingBlock> getBlockAtHeight(int height) {
        return store.getBlockAtHeight(height);
    }

    public List<AccountingBlock> getBlocksAtLeastWithHeight(int minHeight) {
        return store.getBlocksAtLeastWithHeight(minHeight);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected BurningManAccountingStore createStore() {
        return new BurningManAccountingStore(new ArrayList<>());
    }

    @Override
    protected void initializePersistenceManager() {
        persistenceManager.initialize(store, FILE_NAME, PersistenceManager.Source.NETWORK);
    }

    @Override
    public String getFileName() {
        return FILE_NAME;
    }
}
