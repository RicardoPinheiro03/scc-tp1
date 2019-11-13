package scc.srv;

import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import scc.utils.AzureProperties;

import java.util.Properties;

public class CDBConnection {
    static String COSMOS_DB_ENDPOINT = "";
    static String COSMOS_DB_MASTER_KEY = "";
    static String COSMOS_DB_DATABASE = "";
    /* static final String COSMOS_DB_ENDPOINT = AzureProperties.COSMOSDB_URL;
    //"https://scc1920-cosmos-41631.documents.azure.com:443/";
    static final String COSMOS_DB_MASTER_KEY = AzureProperties.COSMOSDB_KEY;
    //"OsaUANGQMKhi3hHoywsV40M6SyVSzCcZUISzq23XF/3pqt9HEBqfhUd29ONdjVTqA51uOMZ6xMToAVv4VZPegw==";
    static final String COSMOS_DB_DATABASE = AzureProperties.COSMOSDB_DATABASE;
    //"SCC-4204"; */

    private static AsyncDocumentClient client;

    static synchronized AsyncDocumentClient getDocumentClient() {
        Properties props = AzureProperties.getProperties();
        COSMOS_DB_ENDPOINT = props.getProperty("COSMOSDB_URL");
        COSMOS_DB_MASTER_KEY = props.getProperty("COSMOSDB_KEY");
        COSMOS_DB_DATABASE = props.getProperty("COSMOSDB_DATABASE");
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
