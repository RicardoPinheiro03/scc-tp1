package scc.test;

import org.glassfish.jersey.client.ClientConfig;
import scc.resources.Post;

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

            Date date = new Date();
            Post post = new Post();
            post.setCommunityName("gaming");
            post.setTextMessage("new");
            post.setDateOfCreation(date.getTime());
            post.setRefParent(null);
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
            postA.setCommunityName("gaming");
            postA.setTextMessage("something bla bla bla");
            postA.setDateOfCreation(dateA.getTime());
            postA.setTitle(post.getTitle());

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

            String postRes = target.path("/post/" + "289e04ca-35b0-4ccc-8247-17d118ed9e44" + "/replies")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(postB, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("Res: " + postRes + "\n");*/

            /* String resA = target.path("/post/" + postRes + "/all")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Post.class); */

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
