package scc.test;

import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

public class TestCDBConnection {
    static final String COSMOS_DB_ENDPOINT = "https://scc1920-cosmos-41631.documents.azure.com:443/";
    static final String COSMOS_DB_MASTER_KEY = "msVbRjM6Tg2I8rHNPWPK6aAouYVmlR3686k34QFKhHkCD73Dkw9sJ3FU4SiKVJaGK4omUnkOnum4YJajO1YDsw==";
    static final String COSMOS_DB_DATABASE = "SCC-4204";

    private static AsyncDocumentClient client;

    static synchronized AsyncDocumentClient getDocumentClient() {
        if(client == null) {
            ConnectionPolicy connectionPolicy = ConnectionPolicy.GetDefault();
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
}
