package scc.srv;

//import sun.plugin2.message.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@Path("/media")
public class MediaResource {

    public static int NO_OF_IMAGES = 10000;
    private HashMap<String, byte[]> images = new HashMap<>(NO_OF_IMAGES);
    private String nameOfImage;

	//TODO: Method to be removed
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello world !!! -- TO BE REMOVED";
    }*/

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public String upload(byte[] contents) throws NoSuchAlgorithmException {
        /*// TODO the InputStream is missing!
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(contents);
        byte[] digest = md.digest(); // get the hash of the contents for the UID (Unique Identifier)
        images.put(digest.toString(), contents);
        nameOfImage = digest.toString();
        return nameOfImage;*/

        return "hey ya";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public byte[] download(String uid) {
        return null;
    }
}
