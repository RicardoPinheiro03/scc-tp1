package scc.srv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import rx.Observable;
import scc.resources.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Iterator;

@Path("/users")
public class UserResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addUser(User user) {
        try {
            AsyncDocumentClient client = CDBConnection.getDocumentClient();
            String UsersCollection = CDBConnection.getCollectionString("Users");
            Observable<ResourceResponse<Document>> resp = client.createDocument(UsersCollection, user, null, false);
            client.close();
            return resp.toBlocking().first().getResource().getId();
        } catch (Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    /**
     * Get user from the endpoint with ID as a parameter.
     * @param uid ID of the user.
     * @return User found returned within an JSON.
     */
    @Path("/{uid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("uid") String uid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String UsersCollection = CDBConnection.getCollectionString("Users");
        User toReturn = null;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                UsersCollection,
                "SELECT * FROM Users u WHERE u.id ='" + uid + "'",
                queryOptions).toBlocking().getIterator();

        String doc = "";
        Gson g = new Gson();
        if(it.hasNext()) {
            doc = it.next().getResults().get(0).toJson();
            toReturn = g.fromJson(doc, User.class);
        }

        System.out.println("ToReturn: \n " + toReturn.getId() + "" + toReturn.getName());

        /*while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Gson g = new Gson();
                toReturn = g.fromJson(d.toJson(), User.class);
            }
        }*/
        client.close();
        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(toReturn))
                .build();
    }

    /**
     * Get the list of all the users present in the database.
     * @return Users returned within an JSON.
     */
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String UsersCollection = CDBConnection.getCollectionString("Users");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client
                .queryDocuments(UsersCollection, "SELECT * FROM Users", queryOptions)
                .toBlocking()
                .getIterator();

        JsonArray usersArray = new JsonArray();
        Gson g = new Gson();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                User user = g.fromJson(d.toJson(), User.class);
                usersArray.add(g.toJson(user));
            }
        }

        client.close();

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(usersArray))
                .build();
    }
}
