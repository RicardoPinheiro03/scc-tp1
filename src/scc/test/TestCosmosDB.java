package scc.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

import scc.resources.Post;
import scc.resources.User;
import scc.test.TestCDBConnection;

public class TestCosmosDB {
    public static String USERS_COLLECTION = "Users";

    public static void main(String[] args) {
        try {
            System.out.println("Trying to connect to the client...");
            AsyncDocumentClient client = TestCDBConnection.getDocumentClient();
            //testUsersCollection(client);
            //testUsersRetrieval(client);
            //testGettingUserByID(client);
            getReplies(client);
            client.close();
            // create database if not exists
            /*List<Database> databaseList = client
                    .queryDatabases("SELECT * FROM root r WHERE r.id='" + TestCDBConnection.COSMOS_DB_DATABASE + "'", null).toBlocking()
                    .first().getResults();
            if (databaseList.size() == 0) {
                try {
                    Database databaseDefinition = new Database();
                    databaseDefinition.setId(TestCDBConnection.COSMOS_DB_DATABASE);
                    client.createDatabase(databaseDefinition, null).toCompletable().await();
                } catch (Exception e) {
                    // TODO: Something has gone terribly wrong.
                    e.printStackTrace();
                    return;
                }
            }*/

            /*String collectionName = "Users";
            List<DocumentCollection> collectionList = client.queryCollections(TestCDBConnection.getDatabaseString(),
                    "SELECT * FROM root r WHERE r.id='" + collectionName + "'", null).toBlocking().first().getResults();

            if (collectionList.size() == 0) {
                try {
                    String databaseLink = TestCDBConnection.getDatabaseString();
                    DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId(collectionName);
                    PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
                    partitionKeyDef.setPaths(Arrays.asList("/name"));
                    collectionDefinition.setPartitionKey(partitionKeyDef);

                    UniqueKeyPolicy uniqueKeyDef = new UniqueKeyPolicy();
                    UniqueKey uniqueKey = new UniqueKey();
                    uniqueKey.setPaths(Arrays.asList("/name"));
                    uniqueKeyDef.setUniqueKeys(Arrays.asList(uniqueKey));
                    collectionDefinition.setUniqueKeyPolicy(uniqueKeyDef);

                    client.createCollection(databaseLink, collectionDefinition, null).toCompletable().await();
                } catch (Exception e) {
                    // TODO: Something has gone terribly wrong.
                    e.printStackTrace();
                    return;
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            client.close();
        }*/
    }

    public void createDatabase() {
        // TODO
    }

    public static void testUsersRetrieval(AsyncDocumentClient client) {
        System.out.println("Testing the retrieval of the users...");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        String usersCollection = TestCDBConnection.getCollectionString(USERS_COLLECTION);

        System.out.println("Here is where I fail...");

        Iterator<FeedResponse<Document>> it = client.queryDocuments(usersCollection,
                "SELECT * FROM Users",
                queryOptions).toBlocking().getIterator();

        Gson masterJson = new Gson();
        //List<User> listUsers = new ArrayList<>();
        JsonArray usersArray = new JsonArray();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Gson g = new Gson();
                User userToReturn = g.fromJson(d.toJson(), User.class);
                usersArray.add(g.toJson(userToReturn));
                //listUsers.add(userToReturn);
                /*System.out.println("User \n" + g.toJson(userToReturn));
                masterJson.toJson(g.toJson(userToReturn));*/
            }
        }

        System.out.println("Collecting all the users...");

        System.out.println(masterJson.toJson(usersArray));

        /*for(JsonElement e : usersArray) {
            Gson a = new Gson();
            System.out.println(a.toJson(e));
        }*/

        //System.out.println(masterJson);
    }

    public static void testGettingUserByID(AsyncDocumentClient client) {
        System.out.println("Testing the retrieval of one user...");

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        String id = "9d51e27e-0b6e-4b85-9071-ff22c5a8809b";

        String usersCollection = TestCDBConnection.getCollectionString(USERS_COLLECTION);

        Iterator<FeedResponse<Document>> it = client.queryDocuments(usersCollection,
                "SELECT * FROM Users u WHERE u.id ='" + id + "'",
                queryOptions).toBlocking().getIterator();

        Gson returnJson = new Gson();
        if(it.hasNext()) {
            String doc = it.next().getResults().get(0).toJson();
            User toReturn = returnJson.fromJson(doc, User.class);
            System.out.println("User Object: " + toReturn + "\n" +
                    "User Name: " + toReturn.getName() + "\n ID: " + toReturn.getId() + "\n Json Object: " +
                    returnJson.toJson(toReturn));
        }
    }

    public static void testUsersCollection(AsyncDocumentClient client) {
        System.out.println("Creating a users collection if unavailable...");
        String collectionName = "Users";
        List<DocumentCollection> collectionList = client.queryCollections(TestCDBConnection.getDatabaseString(),
                "SELECT * FROM root r WHERE r.id='" + collectionName + "'", null).toBlocking().first().getResults();

        if (collectionList.size() == 0) {
            try {
                String databaseLink = TestCDBConnection.getDatabaseString();
                DocumentCollection collectionDefinition = new DocumentCollection();
                collectionDefinition.setId(collectionName);
                PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
                partitionKeyDef.setPaths(Arrays.asList("/name"));
                collectionDefinition.setPartitionKey(partitionKeyDef);

                UniqueKeyPolicy uniqueKeyDef = new UniqueKeyPolicy();
                UniqueKey uniqueKey = new UniqueKey();
                uniqueKey.setPaths(Arrays.asList("/name"));
                uniqueKeyDef.setUniqueKeys(Arrays.asList(uniqueKey));
                collectionDefinition.setUniqueKeyPolicy(uniqueKeyDef);

                client.createCollection(databaseLink, collectionDefinition, null).toCompletable().await();
            } catch (Exception e) {
                // TODO: Something has gone terribly wrong.
                e.printStackTrace();
                //return;
            }
        }

        Iterator<DocumentCollection> it = collectionList.iterator();

        while(it.hasNext()) {
            for(DocumentCollection d : collectionList) {
                System.out.println(d.toJson());
            }
        }

        client.close();
    }

    public static void getReplies(AsyncDocumentClient client) {
        System.out.println("Creating a posts collection if unavailable...");
        String collectionName = "Posts";
        String pid = "289e04ca-35b0-4ccc-8247-17d118ed9e44";
        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);
        Iterator<FeedResponse<Document>> it = client.queryDocuments(
                collectionName,
                "SELECT * FROM Posts p WHERE p.id ='" + pid + "'",
                queryOptions).toBlocking().getIterator();
        /*JsonArray repliesArray = new JsonArray();
        Gson g = new Gson();

        while(it.hasNext()) {
            for(Document d : it.next().getResults()) {
                Post reply = g.fromJson(d.toJson(), Post.class);
                repliesArray.add(g.toJson(reply));
            }
        }
        System.out.println(repliesArray);*/

        if(it.hasNext()) {
            System.out.println("Result: " + it.next().getResults().get(0).toJson());
        }
    }
}
