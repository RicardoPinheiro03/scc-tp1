package scc.srv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import javafx.application.Application;
import rx.Observable;
import scc.resources.Post;
//import scc.resources.User;

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
            /* if(!post.getRefParent().equals(""))
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build()); */

            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setMaxDegreeOfParallelism(-1);

            Iterator<FeedResponse<Document>> it = client.queryDocuments(
                    CommunityCollection,
                    "SELECT * FROM Communities c WHERE c.name ='" + post.getCommunity() + "'",
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

    /*@Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPosts() {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostsCollection = CDBConnection.getCollectionString("Posts");
        String emptyString = "''";

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client
                .queryDocuments(PostsCollection, "SELECT * FROM Posts WHERE p.refParent = '" + emptyString + "'" , queryOptions)
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
    } */

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

    /**
     * Retrieve a thread (main post and replies) by main post ID.
     * @param pid
     * @return JSON Array with all the replies
     */
    @Path("/{pid}/all")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReplies(@PathParam("pid") String pid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        JsonArray repliesArray = new JsonArray();
        Gson g = new Gson();

        Iterator<FeedResponse<Document>> it = client.queryDocuments(PostCollection,
                "SELECT * FROM Posts p WHERE p.id ='" + pid + "'",
                queryOptions).toBlocking().getIterator();

        if(it.hasNext()) {
            String doc = it.next().getResults().get(0).toJson();
            Post mainPost = g.fromJson(doc, Post.class);
            repliesArray.add(g.toJson(mainPost));
        }

        it = client.queryDocuments(
                PostCollection,
                "SELECT * FROM Posts p WHERE p.refParent ='" + pid + "'",
                queryOptions).toBlocking().getIterator();

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

    @Path("/{pid}/like/{uid}")
    @GET
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String likePost(@PathParam("pid") String pid, @PathParam("uid") String uid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");
        String UserCollection = CDBConnection.getCollectionString("Users");
        Post p;
        Observable<ResourceResponse<Document>> resp;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(PostCollection,
                "SELECT * FROM Posts p WHERE p.id ='" + pid + "'",
                queryOptions).toBlocking().getIterator();


        Iterator<FeedResponse<Document>> it_ = client.queryDocuments(UserCollection,
                "SELECT * FROM Users u WHERE u.id ='" + uid + "'",
                queryOptions).toBlocking().getIterator();

        if(!it_.hasNext()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        } // If the iterator does not contain any user, throws exception (Bad Request)

        Gson g = new Gson();
        if(it.hasNext()) {
            Document doc0 = it.next().getResults().get(0);
            String doc = doc0.toJson();
            p = g.fromJson(doc, Post.class);
            p.setNumberLikes(uid);
            // p.setNumberLikes();
            resp = client.replaceDocument(doc0.getSelfLink(), p, null);
            //client.replaceDocument(doc_, p, null);

            //client.replaceDocument(doc, p, requestOptions);
            //client.deleteDocument(doc, requestOptions);
            //resp = client.createDocument(PostCollection, p, null, false);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }

        //System.out.println("POST: " + p);

        /*return Response
                .status(Response.Status.OK)
                .entity(g.toJson(p))
                .build();*/
        return resp.toBlocking().first().getResource().getId();
    }

    // 8 of november

    // lab 7 zip is one maven project with azure functions

    // for report:
    // -- deploy app for eu-west and use artillery to test the performance
    // -- deploy app on us asia
    // -- deploy app without cache (turned off -- how to do this?)

    // -- don't forget to create tests for the front page (artillery)

    @Path("/{pid}/unlike/{uid}")
    @GET
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlikePost(@PathParam("uid") String uid, @PathParam("pid") String pid) {
        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostCollection = CDBConnection.getCollectionString("Posts");
        String UserCollection = CDBConnection.getCollectionString("Users");
        Post p;
        //Observable<ResourceResponse<Document>> resp;

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(PostCollection,
                "SELECT * FROM Posts p WHERE p.id ='" + pid + "'",
                queryOptions).toBlocking().getIterator();

        Iterator<FeedResponse<Document>> it_ = client.queryDocuments(UserCollection,
                "SELECT * FROM Users u WHERE u.id ='" + uid + "'",
                queryOptions).toBlocking().getIterator();

        if(!it_.hasNext()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }

        Gson g = new Gson();
        if(it.hasNext()) {
            Document d0 = it.next().getResults().get(0);
            p = g.fromJson(d0.toJson(), Post.class);
            p.unsetNumberLikes(uid);
            //p.unsetNumberLikes();
            client.replaceDocument(d0.getSelfLink(), p, null);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(p))
                .build();
        //return resp.toBlocking().first().getResource().getId();
    }
}
