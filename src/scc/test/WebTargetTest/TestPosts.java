package scc.test.WebTargetTest;

import org.glassfish.jersey.client.ClientConfig;
import scc.resources.Post;

import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;

public class TestPosts {
    public static void main(String[] args) {
        try {
            String hostname = "https://scc-backend-41631.azurewebsites.net/";

            if (args.length > 0)
                hostname = args[0];

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            URI baseURI = UriBuilder.fromUri(hostname).build();

            WebTarget target = client.target(baseURI);

            /*Date date = new Date();
            Post post = new Post();
            post.setCommunityName("something");
            post.setTextMessage("new");
            post.setDateOfCreation(date.getTime());
            post.setRefParent("");
            post.setTitle("title");

            // how to set the user who creates? by session cookies or something?

            String postRes = target.path("/post")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(post, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("Res: " + postRes + "\n");

            Post postA = new Post();
            Date dateA = new Date();
            postA.setCommunityName("something");
            postA.setTextMessage("something bla bla bla");
            postA.setDateOfCreation(dateA.getTime());
            postA.setTitle(post.getTitle());
            System.out.println("Post A Comm : " + postA.getCommunityName());

            String postRes_ = target.path("/post/" + postRes + "/replies")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(postA, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("Res: " + postRes_ + "\n");

            /*Post postB = new Post();
            postB.setCommunityName("gaming");
            postB.setTitle("title");
            Date date = new Date();
            postB.setDateOfCreation(date.getTime());

            String postRes = target.path("/post/" + "3a511d11-66f5-4d9a-9ca5-e43f63159192" + "/replies")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(postB, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("Res: " + postRes + "\n");

            Post postRes_ = target.path("/post/" + postRes + "/like/9de590ae-6149-41af-8c13-e7577869b83b")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Post.class);

            System.out.println("Must be 1:" + postRes_.getNumberLikes());

            Post postRes__ = target.path("/post/" + postRes + "/unlike/cde590ae-6149-41af-8c13-e7577869b83b")
                    .request()postIds
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Post.class);

            System.out.println("Must be 2: " + postRes__.getNumberLikes());

            /* String resA = target.path("/post/" + postRes + "/all")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Post.class);*/

            /*Post postRes_ = null;



            System.out.println("Response: " + response);*/

            /*String post = target.path("/post/" + "" + "/like/9cb4a5dc-1ce8-4b2a-8d75-b6b5e723c3cd")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .get(String.class);

            String post2 = target.path("/post/" + ""+ "/like/blablabla")
                    .request()
                    .put(Entity.entity(post1, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);*/

            // System.out.println("post 1: " + post + "post 2: " + post2);
            /* Post post1 = new Post();
            post1.setCommunity("something");
            post1.setTitle("benfica");
            post1.setTextMessage("benfica europeu");
            post1.setRefParent(""); */

            Post post1 = new Post();
            post1.setCommunity("s/ducimus");
            post1.setTitle("luisfelipevieira");
            post1.setTextMessage("estezzz");
            post1.setRefParent("");

            String response =  target.path("/post")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(post1, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println(response + "\n");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
