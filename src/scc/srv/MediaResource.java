package scc.srv;

//import sun.plugin2.message.Message;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

@Path("/media")
public class MediaResource {

    //public static int NO_OF_IMAGES = 10000;
    //private HashMap<String, byte[]> images = new HashMap<>(NO_OF_IMAGES);
    //private String nameOfImage;

	//TODO: Method to be removed
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello world !!! -- TO BE REMOVED";
    }*/

    public CloudBlobContainer getContainer() {
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=scc41631;AccountKey=OsaUANGQMKhi3hHoywsV40M6SyVSzCcZUISzq23XF/3pqt9HEBqfhUd29ONdjVTqA51uOMZ6xMToAVv4VZPegw==;EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount = null;
        CloudBlobContainer container = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference("images");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        // TODO: WebApplication Exception
        return container;
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(byte[] contents) throws URISyntaxException, InvalidKeyException, StorageException, IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        //md.update(contents);
        byte[] digest = md.digest(contents); // get the hash of the contents for the UID (Unique Identifier)
        String nameOfImage = Base64.getEncoder().encodeToString(digest); // Convert the byte array to String

        CloudBlobContainer myContainer = getContainer();

        // Get reference to blob
        CloudBlob blob = myContainer.getBlockBlobReference(nameOfImage);

        /* if(myContainer.getBlobReferenceFromServer(nameOfImage).exists()) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(nameOfImage)
                    .build(); // If the blob content already exists, reply with a conflict exception
        }
        This breaks the deploy. The storage (blob containers) already does this verification. */

        // Upload contents from byte array
        blob.uploadFromByteArray(contents, 0, contents.length);

        return Response
                .status(Response.Status.OK)
                .entity(nameOfImage)
                .build();
    }

    @Path("/{uid}")
    @GET
    @Produces({"image/png", "image/jpeg", "image/gif"})
    public Response download(@PathParam("uid") String uid) {
        CloudBlobContainer myContainer = getContainer();
        byte[] contents = null;
        try {
            CloudBlob blob = myContainer.getBlobReferenceFromServer(uid);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            blob.download(out);
            out.close();
            contents = out.toByteArray();
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return Response
                .status(Response.Status.OK)
                .entity(contents)
                .build();
    }
}
