package scc.test;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.*;
import scc.resources.User;

import java.net.URI;
import java.util.Date;

public class TestUsers {
    public static void main(String[] args) {
        try {
            String hostname = "https://scc-backend-41631.azurewebsites.net/";

            if (args.length > 0)
                hostname = args[0];

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            URI baseURI = UriBuilder.fromUri(hostname).build();

            WebTarget target = client.target(baseURI);

            User user = new User();
            user.setName("user-" + new Date().getTime());

            System.out.println("Username: \n" + user.getName());

            String id = target.path("/name")
                    .request()
                    .accept(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(user, MediaType.APPLICATION_JSON))
                    .readEntity(String.class);

            System.out.println("ID: \n" + id);

            /*User u0 = target.path("/name/" + id)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(User.class);*/
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
