package scc.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;

import org.glassfish.jersey.client.*;

public class TestMedia {
    public static void main(String[] args) {

        try {
            String hostname = "https://scc-backend-41631.azurewebsites.net/";
            if (args.length > 0)
                hostname = args[0];

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            URI baseURI = UriBuilder.fromUri(hostname).build();

            WebTarget target = client.target(baseURI);

            // Upload + download test? Weird...
			/*byte[] image = target.path("/media/slb_logo.png").request().accept(MediaType.APPLICATION_OCTET_STREAM)
					.get(byte[].class);

			// target.path("/media/slb_logo.png")

			System.err.println( image.length);

			Response res = target.path("/media").request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(image,MediaType.APPLICATION_OCTET_STREAM));

			System.err.println( res);*/

            // ==================================================
            // Try to upload an image and use the endpoint for it
            File file = new File("../goodPicture.jpg");

            FileInputStream fis = null;
            byte[] imageArray = new byte[(int) file.length()];

            try {
                fis = new FileInputStream(file);
                fis.read(imageArray);
                fis.close();
            } catch(IOException ioExp) {
                ioExp.printStackTrace();
            }

            Response res_ = target.path("/media")
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(imageArray, MediaType.APPLICATION_OCTET_STREAM));

            System.err.println(res_);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


