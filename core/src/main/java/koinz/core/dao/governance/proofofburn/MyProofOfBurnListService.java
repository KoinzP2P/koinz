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

package koinz.core.dao.governance.proofofburn;

import koinz.core.dao.DaoSetupService;

import koinz.common.persistence.PersistenceManager;
import koinz.common.proto.persistable.PersistedDataHost;

import javax.inject.Inject;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages the persistence of MyProofOfBurn objects.
 */
@Slf4j
public class MyProofOfBurnListService implements PersistedDataHost, DaoSetupService {

    private final PersistenceManager<MyProofOfBurnList> persistenceManager;
    private final MyProofOfBurnList myProofOfBurnList = new MyProofOfBurnList();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public MyProofOfBurnListService(PersistenceManager<MyProofOfBurnList> persistenceManager) {
        this.persistenceManager = persistenceManager;
        persistenceManager.initialize(myProofOfBurnList, PersistenceManager.Source.PRIVATE);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PersistedDataHost
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void readPersisted(Runnable completeHandler) {
        persistenceManager.readPersisted(persisted -> {
                    myProofOfBurnList.setAll(persisted.getList());
                    completeHandler.run();
                },
                completeHandler);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // DaoSetupService
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addListeners() {
    }

    @Override
    public void start() {
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void addMyProofOfBurn(MyProofOfBurn myProofOfBurn) {
        if (!myProofOfBurnList.contains(myProofOfBurn)) {
            myProofOfBurnList.add(myProofOfBurn);
            requestPersistence();
        }
    }

    public List<MyProofOfBurn> getMyProofOfBurnList() {
        return myProofOfBurnList.getList();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void requestPersistence() {
        persistenceManager.requestPersistence();
    }
}
