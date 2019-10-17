package scc.srv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
    /**
     * Create new post. This post is vanilla, i.e. this method creates a parent post.
     * @param post
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addPost(Post post) {
        try {
            AsyncDocumentClient client = CDBConnection.getDocumentClient();
            String PostCollection = CDBConnection.getCollectionString("Posts");
            String CommunityCollection = CDBConnection.getCollectionString("Communities");
            Observable<ResourceResponse<Document>> resp = client.createDocument(PostCollection, post, null, false);

            // Root post
            if(!post.getRefParent().equals(""))
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = client.queryDocuments(
                    CommunityCollection,
                    "SELECT * FROM Communities c WHERE c.name ='" + post.getCommunityName() + "'",
                    queryOptions).toBlocking().getIterator();

            //client.close();

            if(it.hasNext()) {
                return resp.toBlocking().first().getResource().getId();
            } else {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
            }
        } catch(Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    /**
     * Retrieves post by given ID.
     * @param pid
     * @return
     */
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

        // client.close();

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(toReturn))
                .build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPosts() {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostsCollection = CDBConnection.getCollectionString("Posts");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client
                .queryDocuments(PostsCollection, "SELECT * FROM Posts WHERE p.refParent IS NULL", queryOptions)
                .toBlocking()
                .getIterator();

        JsonArray postsArray = new JsonArray();
        Gson g = new Gson();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Post post = g.fromJson(d.toJson(), Post.class);
                postsArray.add(g.toJson(post));
            }
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(postsArray))
                .build();
    }

    @Path("/{pid}/replies")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addReply(@PathParam("pid") String pid, Post post) {
        post.setRefParent(pid);
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");
        Observable<ResourceResponse<Document>> resp = client.createDocument(PostCollection, post, null, false);
        return resp.toBlocking().first().getResource().getId();
    }

    @Path("/{pid}/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReplies(@PathParam("pid") String pid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                PostCollection,
                "SELECT * FROM Posts p WHERE p.refParent ='" + pid + "'",
                queryOptions).toBlocking().getIterator();

        JsonArray repliesArray = new JsonArray();
        Gson g = new Gson();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Post reply = g.fromJson(d.toJson(), Post.class);
                repliesArray.add(g.toJson(reply));
            }
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(repliesArray))
                .build();
    }

}
