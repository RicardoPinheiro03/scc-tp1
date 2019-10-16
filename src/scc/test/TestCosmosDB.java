package scc.test;

import java.util.Arrays;
import java.util.List;

import com.microsoft.azure.cosmosdb.ConnectionMode;
import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.Database;
import com.microsoft.azure.cosmosdb.DocumentCollection;
import com.microsoft.azure.cosmosdb.PartitionKeyDefinition;
import com.microsoft.azure.cosmosdb.UniqueKey;
import com.microsoft.azure.cosmosdb.UniqueKeyPolicy;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

public class TestCosmosDB {
    static final String COSMOS_DB_ENDPOINT = "https://scc1920-cosmos-41631.documents.azure.com:443/";
    static final String COSMOS_DB_MASTER_KEY = "fKFFnS84oSmIakPzRLBKrdC5AJGfldkwQPabKGT5l5ievYfnVCwxVvc0FZXRvA5ptF34Q9AVHHWgP7F9aq7Dzg==";
    static final String COSMOS_DB_DATABASE = "SCC-4204";

    private static AsyncDocumentClient client;

    static synchronized AsyncDocumentClient getClient() {
        if (client == null) {
            ConnectionPolicy connectionPolicy = ConnectionPolicy.GetDefault();
            //connectionPolicy.setConnectionMode(ConnectionMode.);
            client = new AsyncDocumentClient.Builder()
                    .withServiceEndpoint(COSMOS_DB_ENDPOINT)
                    .withMasterKeyOrResourceToken(COSMOS_DB_MASTER_KEY)
                    .withConnectionPolicy(connectionPolicy)
                    .withConsistencyLevel(ConsistencyLevel.Eventual)
                    .build();
        }
        return client;
    }

    /**
     * Returns the string to access a CosmosDB database
     *
     * @param
     *
     * @return
     */
    static String getDatabaseString() {
        return String.format("/dbs/%s", COSMOS_DB_DATABASE);
    }

    /**
     * Returns the string to access a CosmosDB collection names col
     *
     * @param col Name of collection
     * @return
     */
    static String getCollectionString(String col) {
        return String.format("/dbs/%s/colls/%s", COSMOS_DB_DATABASE, col);
    }

    public static void main(String[] args) {
        try {
            AsyncDocumentClient client = getClient();

            // create database if not exists
            List<Database> databaseList = client
                    .queryDatabases("SELECT * FROM root r WHERE r.id='" + COSMOS_DB_DATABASE + "'", null).toBlocking()
                    .first().getResults();
            if (databaseList.size() == 0) {
                try {
                    Database databaseDefinition = new Database();
                    databaseDefinition.setId(COSMOS_DB_DATABASE);
                    client.createDatabase(databaseDefinition, null).toCompletable().await();
                } catch (Exception e) {
                    // TODO: Something has gone terribly wrong.
                    e.printStackTrace();
                    return;
                }
            }

            String collectionName = "Users";
            List<DocumentCollection> collectionList = client.queryCollections(getDatabaseString(),
                    "SELECT * FROM root r WHERE r.id='" + collectionName + "'", null).toBlocking().first().getResults();

            if (collectionList.size() == 0) {
                try {
                    String databaseLink = getDatabaseString();
                    DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId(collectionName);
                    PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
                    partitionKeyDef.setPaths(Arrays.asList("/name"));
                    collectionDefinition.setPartitionKey(partitionKeyDef);

                    UniqueKeyPolicy uniqueKeyDef = new UniqueKeyPolicy();
                    UniqueKey uniqueKey = new UniqueKey();
                    uniqueKey.setPaths(Arrays.asList("/name"));
                    uniqueKeyDef.setUniqueKeys(Arrays.asList(uniqueKey));
                    collectionDefinition.setUniqueKeyPolicy(uniqueKeyDef);

                    client.createCollection(databaseLink, collectionDefinition, null).toCompletable().await();
                } catch (Exception e) {
                    // TODO: Something has gone terribly wrong.
                    e.printStackTrace();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            client.close();
        }*/
    }
}
