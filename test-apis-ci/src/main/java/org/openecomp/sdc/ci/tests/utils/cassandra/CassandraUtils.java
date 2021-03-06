/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.ci.tests.utils.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import org.javatuples.Pair;
import org.openecomp.sdc.be.resources.data.auditing.AuditingTypesConstants;
import org.openecomp.sdc.ci.tests.utils.Utils;
import org.openecomp.sdc.common.datastructure.AuditingFieldsKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CassandraUtils {
    private static Logger logger = LoggerFactory.getLogger(CassandraUtils.class.getName());

    protected static Cluster cluster = null;
    protected static Session session;

    public static void initConnection(String keyspace) throws FileNotFoundException {
        List<String> cassandraHosts = new ArrayList<>();
        try {
            cassandraHosts.add(Utils.getConfig().getCassandraHost());
            long reconnectTimeout = 30000;

            logger.debug("creating cluster to hosts:{} with reconnect timeout:{}", cassandraHosts, reconnectTimeout);
            Cluster.Builder clusterBuilder = Cluster.builder()
                    .withReconnectionPolicy(new ConstantReconnectionPolicy(reconnectTimeout))
                    .withRetryPolicy(DefaultRetryPolicy.INSTANCE);

            cassandraHosts.forEach(host -> clusterBuilder.addContactPoint(host));
            enableAuthentication(clusterBuilder);
            enableSsl(clusterBuilder);
            setLocalDc(clusterBuilder);

            cluster = clusterBuilder.build();
            session = cluster.connect(keyspace);
        } catch (Exception e) {
            logger.info("** CassandraClient isn't connected to {}", cassandraHosts);
        }
    }

    private static void enableAuthentication(Cluster.Builder clusterBuilder) throws FileNotFoundException {
        boolean authenticate = Utils.getConfig().getCassandraAuthenticate();
        if (authenticate) {
            String username = Utils.getConfig().getCassandraUsername();
            String password = Utils.getConfig().getCassandraPassword();
            if (username == null || password == null) {
                logger.error("authentication is enabled but username or password were not supplied.");
            } else {
                clusterBuilder.withCredentials(username, password);
            }

        }
    }

    private static void enableSsl(Cluster.Builder clusterBuilder) throws FileNotFoundException {
        boolean ssl = Utils.getConfig().getCassandraSsl();
        if (ssl) {
            String truststorePath = Utils.getConfig().getCassandraTruststorePath();
            String truststorePassword = Utils.getConfig().getCassandraTruststorePassword();
            if (truststorePath == null || truststorePassword == null) {
                logger.error("ssl is enabled but truststorePath or truststorePassword were not supplied.");
            } else {
                System.setProperty("javax.net.ssl.trustStore", truststorePath);
                System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
                clusterBuilder.withSSL();
            }
        }
    }


    private static void setLocalDc(Cluster.Builder clusterBuilder) throws FileNotFoundException {
        String localDataCenter = Utils.getConfig().getLocalDataCenter();
        if (localDataCenter != null) {
            logger.info("localDatacenter was provided, setting Cassndra clint to use datacenter: {} as local.", localDataCenter);
            LoadBalancingPolicy tokenAwarePolicy = new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().withLocalDc(localDataCenter).build());
            clusterBuilder.withLoadBalancingPolicy(tokenAwarePolicy);
        } else {
            logger.info("localDatacenter was provided, the driver will use the datacenter of the first contact point that was reached at initialization");
        }
    }

    public static void truncateTable(String keyspace, String tableName) throws FileNotFoundException {

        if (session == null || session.isClosed()) {
            initConnection(keyspace);
        }

        try (Cluster cluster = CassandraUtils.cluster){

            if (session != null) {
                session.execute(QueryBuilder.truncate(keyspace, tableName));
                logger.debug("The table {}.{} was cleaned", keyspace, tableName);
            } else {
                throw new RuntimeException("Keyspace " + keyspace + " not connected");
            }
        }
    }

    public static void close() {
        if (cluster != null) {
            cluster.close();
        }
    }

    public static void truncateAllKeyspaces() throws FileNotFoundException {
        // truncateAllTables(AuditingTypesConstants.ARTIFACT_KEYSPACE);
        truncateAllTables(AuditingTypesConstants.AUDIT_KEYSPACE);
    }

    public static void truncateAllTables(String keyspace) throws FileNotFoundException {

        if (session == null || session.isClosed()) {
            initConnection(keyspace);
        }
        try {

            if (session != null) {
                Metadata metadata = cluster.getMetadata();
                KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace(keyspace);
                if (keyspaceMetadata != null) {
                    Collection<TableMetadata> tables = keyspaceMetadata.getTables();
                    tables.forEach(table -> {
                        session.execute(QueryBuilder.truncate(table));
                        logger.debug("Table trunceted - {}", table.getName());
                    });
                }
            } else {
                throw new RuntimeException("Keyspace " + keyspace + " not connected");
            }

        } finally {
             if (cluster != null) {
             cluster.close();
             }
        }
    }

    public static List<Row> fetchFromTable(String keyspace, String tableName, List<Pair<AuditingFieldsKey, String>> fields) throws FileNotFoundException {

        List<Pair<String, String>> fieldsConverted = new ArrayList<>();

//		fields.forEach(pair -> {
//			Pair<String, String> newPair = new Pair(pair.getValue0().getDisplayName(), pair.getValue1());
//			fieldsConverted.add(newPair);
//		});

        fields.forEach(pair -> {
            Pair<String, String> newPair;
            if (pair.getValue0() == AuditingFieldsKey.AUDIT_DISTRIBUTION_RESOURCE_URL) {
                newPair = new Pair<String, String>("RESOURE_URL", pair.getValue1());

            } else {
                newPair = new Pair<String, String>(pair.getValue0().getDisplayName(), pair.getValue1());
            }
            fieldsConverted.add(newPair);

        });

        return fetchFromTableQuery(keyspace, tableName, fieldsConverted);
    }

    public static List<Row> fetchFromTableQuery(String keyspace, String tableName, List<Pair<String, String>> fields)
            throws FileNotFoundException {

        if (session == null || session.isClosed()) {
            initConnection(keyspace);
        }
        try {

            if (session != null) {
                Select select = QueryBuilder.select().all().from(keyspace, tableName);
                if (fields != null) {
                    // Set<Entry<AuditingFieldsKey, String>> entrySet =
                    // fields.entrySet();
                    // fields.
                    boolean multiple = (fields.size() > 1) ? true : false;
                    Where where = null;
                    int size = 0;

                    for (Pair<String, String> pair : fields) {
                        ++size;
                        if (size == 1) {
                            where = select.where(QueryBuilder.eq(pair.getValue0(), pair.getValue1()));
                        } else {
                            where.and(QueryBuilder.eq(pair.getValue0(), pair.getValue1()));
                        }
                    }
                    if (multiple) {
                        select.allowFiltering();
                    }

                }

                List<Row> rows = session.execute(select).all();
                for (Row row : rows) {
                    logger.debug("{}", row);
                }
                return rows;
            }
        } finally {
            // if (cluster != null) {
            // cluster.close();
            // }
        }
        return null;
    }




}
