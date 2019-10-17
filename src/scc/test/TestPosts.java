package scc.test;

import org.glassfish.jersey.client.ClientConfig;
import scc.resources.Post;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class TestPosts {
    public static void main(String[] args) {
        String hostname = "https://scc-backend-41631.azurewebsites.net/";

        if (args.length > 0)
            hostname = args[0];

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        URI baseURI = UriBuilder.fromUri(hostname).build();

        WebTarget target = client.target(baseURI);

        Post post = new Post();

    }
}
