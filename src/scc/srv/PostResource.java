package scc.srv;

import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import javafx.application.Application;
import rx.Observable;
import scc.resources.Post;
import scc.resources.User;

import javax.ws.rs.*;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Path("/post")
public class PostResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addPost(Post post) {
        try {
            AsyncDocumentClient client = CDBConnection.getDocumentClient();
            String PostCollection = CDBConnection.getCollectionString("Posts");
            Observable<ResourceResponse<Document>> resp = client.createDocument(PostCollection, post, null, false);

            if(post.getRefParent().equals("")) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
            }

            return resp.toBlocking().first().getResource().getId();
        } catch(Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    @Path("/{pid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPost(@PathParam("pid") String pid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");
        Post toReturn = null;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                PostCollection,
                "SELECT * FROM Posts p WHERE p.id ='" + pid + "'",
                queryOptions).toBlocking().getIterator();

        Gson g = new Gson();
        if(it.hasNext()) {
            String doc = it.next().getResults().get(0).toJson();
            toReturn = g.fromJson(doc, Post.class);
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(toReturn))
                .build();
    }

    @Path("/{pid}/replies")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addReply(@PathParam("pid") String pid, Post post) {
        // TODO
        return "";
    }
}
