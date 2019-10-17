package scc.srv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import rx.Observable;
import scc.resources.Community;
import scc.resources.Post;
import scc.resources.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Path("/community")
public class CommunityResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addCommunity(Community comm) {
        try {
            AsyncDocumentClient client = CDBConnection.getDocumentClient();
            String PostCollection = CDBConnection.getCollectionString("Communities");
            Observable<ResourceResponse<Document>> resp = client.createDocument(PostCollection, comm, null, false);
            return resp.toBlocking().first().getResource().getId();
        } catch(Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    @Path("/{cid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommunity(@PathParam("cid") String cid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String CommunityCollection = CDBConnection.getCollectionString("Communities");
        Community toReturn = null;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                CommunityCollection,
                "SELECT * FROM Communities p WHERE p.id ='" + cid + "'",
                queryOptions).toBlocking().getIterator();

        Gson g = new Gson();
        if(it.hasNext()) {
            String doc = it.next().getResults().get(0).toJson();
            toReturn = g.fromJson(doc, Community.class);
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(toReturn))
                .build();
    }


    /**
     * Get the list of all the communities present in the database.
     * @return Communities returned within an JSON.
     */
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCommunities() {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String CommunityCollection = CDBConnection.getCollectionString("Communities");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client
                .queryDocuments(CommunityCollection, "SELECT * FROM Communities", queryOptions)
                .toBlocking()
                .getIterator();

        JsonArray commsArray = new JsonArray();
        Gson g = new Gson();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Community comm = g.fromJson(d.toJson(), Community.class);
                commsArray.add(g.toJson(comm));
            }
        }

        //client.close();

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(commsArray))
                .build();
    }
}
