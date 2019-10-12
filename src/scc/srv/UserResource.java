package scc.srv;

import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import rx.Observable;
import scc.resources.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Path("/name")
public class UserResource {
    static final String COSMOS_DB_ENDPOINT = "https://scc1920-cosmos.documents.azure.com:443/";
    static final String COSMOS_DB_MASTER_KEY = "3vL9v7JBqZ1t0yzSNI5rUff1hWOCQRRi52eZ0A86KDw0yMAgT0OFPHPyLHmCcgeUP0cqQvfOlwoAMLsFABcHMg==";
    static final String COSMOS_DB_DATABASE = "SCC-41631";

    private static AsyncDocumentClient client;

    static synchronized AsyncDocumentClient getClient() {
        if (client == null) {
            ConnectionPolicy connectionPolicy = new ConnectionPolicy();
            connectionPolicy.setConnectionMode(ConnectionMode.Direct);
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
     * Returns the string to access a CosmosDB collection names col
     *
     * @param col Name of collection
     * @return
     */
    static String getCollectionString(String col) {
        return String.format("/dbs/%s/colls/%s", COSMOS_DB_DATABASE, col);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addUser(User user) {
        try {
            client = getClient();
            String UsersCollection = getCollectionString("Users");
            Observable<ResourceResponse<Document>> resp = client.createDocument(UsersCollection, user, null, false);
            return resp.toBlocking().first().getResource().getId();
        } catch (Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    @Path("/{uid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("uid") String uid) {
        client = getClient();
        String UsersCollection = getCollectionString("Users");
        User toReturn = null;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                UsersCollection,
                "SELECT * FROM Users u WHERE u.id ='" + uid + "'",
                queryOptions).toBlocking().getIterator();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Gson g = new Gson();
                toReturn = g.fromJson(d.toJson(), User.class);
            }
        }

        return Response
                .status(Response.Status.OK)
                .entity(toReturn)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.status(Response.Status.OK).build();
    }
}
