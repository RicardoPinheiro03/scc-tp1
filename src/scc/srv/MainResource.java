package scc.srv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.management.redis.RedisCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import scc.resources.Post;
import scc.utils.AzureProperties;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Path("/pages")
public class MainResource {

    public JedisPool getJedisClient() {
        Properties props = AzureProperties.getProperties();
        String RedisHostname = props.getProperty("REDIS_URL");
        String cacheKey = props.getProperty("REDIS_KEY");
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration. ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration. ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        JedisPool jedisPool = new JedisPool(poolConfig, RedisHostname, 6380, 1000, cacheKey,
                true);
        //JedisShardInfo shardInfo = new JedisShardInfo(RedisHostname,
        //       6380, true);
        //shardInfo.setPassword(cacheKey);
        //Jedis jedis = new Jedis(shardInfo);
        return jedisPool;
    }

    @Path("/thread/{pid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThread(@PathParam("pid") String pid) {
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

    @Path("/initial")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMainPosts() {
        // Cache algorithm:
        // Search in the cache for posts
        // If doesnt find anything: go get the data from the database and put it on cache
        // else return what haves on cache

        AsyncDocumentClient client = CDBConnection.getDocumentClient();
        String PostsCollection = CDBConnection.getCollectionString("Posts");
        // String emptyString = "''";

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setMaxDegreeOfParallelism(-1);

        JsonArray postsArray = new JsonArray();
        Gson g = new Gson();
        //Jedis jedis = getJedisClient();
        JedisPool jp = getJedisClient();

        try(Jedis jedis = jp.getResource()) {
            List<String> lst = jedis.lrange("MostLikedPosts", 0, 5);

            if(lst.isEmpty()) {
                // The query misses the number of likes. TODO
                Iterator<FeedResponse<Document>> it = client
                        .queryDocuments(PostsCollection, "SELECT * FROM Posts p WHERE p.refParent = '' AND p.numberLikes > 5", queryOptions)
                        .toBlocking()
                        .getIterator();
                while(it.hasNext()) {
                    for(Document d : it.next().getResults()) {
                        Post post = g.fromJson(d.toJson(), Post.class);
                        postsArray.add(g.toJson(post));
                        Long cnt = jedis.lpush("MostLikedPosts", d.toJson());
                        if(cnt > 5)
                            jedis.ltrim("MostLikedPosts", 0, 5);
                        else {
                            jedis.ltrim("MostLikedPosts", 0, cnt); // If it has less than 5 main page posts
                        }
                    }
                }
            } else {
                for (String a : lst) {
                    postsArray.add(a);
                }
            }
        }

        return Response
                .status(Response.Status.OK)
                .entity(g.toJson(postsArray))
                .build();
    }
}
