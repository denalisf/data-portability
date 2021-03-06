/*
 * Copyright 2018 The Data-Portability Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataportabilityproject.cloud.microsoft.cosmos;

import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates and initializes a {@link CosmosStore} instance. Supports Azure and local setup.
 */
public class CosmosStoreInitializer extends AbstractCosomosStoreInitializer {

    /**
     * Returns a new {@link CosmosStore} instance configured for Azure.
     */
    public CosmosStore createStore(ObjectMapper mapper) {
        CassandraCluster.Builder builder = CassandraCluster.Builder.newInstance();
        // TODO configure builder
        CassandraCluster cassandraCluster = builder.build();
        Session session = cassandraCluster.createSession(true);

        createKeyspace(session);
        createTables(session);

        return new CosmosStore(session, mapper);
    }

}
