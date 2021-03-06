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

import static org.dataportabilityproject.cloud.microsoft.cosmos.MicrosoftCloudConstants.DATA_TABLE;
import static org.dataportabilityproject.cloud.microsoft.cosmos.MicrosoftCloudConstants.JOB_TABLE;
import static org.dataportabilityproject.cloud.microsoft.cosmos.MicrosoftCloudConstants.KEY_SPACE;

/**
 * Base {@link CosmosStore} initializer.
 */
public abstract class AbstractCosomosStoreInitializer {

    protected void createKeyspace(Session session) {
        String query = "CREATE KEYSPACE IF NOT EXISTS " + KEY_SPACE + " WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 }";
        session.execute(query);
    }

    protected void createTables(Session session) {
        String jobTableQuery = "CREATE TABLE IF NOT EXISTS " + JOB_TABLE + " (job_id uuid PRIMARY KEY, job_data text)";
        session.execute(jobTableQuery);
        String dataTableQuery = "CREATE TABLE IF NOT EXISTS " + DATA_TABLE + " (data_id uuid  PRIMARY KEY, data_model text)";
        session.execute(dataTableQuery);
    }


}
