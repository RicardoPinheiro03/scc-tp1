package scc.test.WebTargetTest;

import org.glassfish.jersey.client.ClientConfig;
import scc.resources.Community;
import scc.resources.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;

public class TestCommunities {
    public static void main(String[] args) {
        try {
            String hostname = "https://scc-backend-41631.azurewebsites.net/";

            if (args.length > 0)
                hostname = args[0];

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            URI baseURI = UriBuilder.fromUri(hostname).build();

            WebTarget target = client.target(baseURI);

            Community comm = new Community();
            comm.setName("outra");

            String res = target.path("/comms")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(comm, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("ID: " + res);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
