### Instructions to deploy
Execute this command on the source of the project:

```mvn azure-webapp:config & mvn package azure-webapp:deploy```

### Instructions to make local environment
    mvn package wildfly:deploy

    mvn wildfly:deploy - deploy any changes to the application to the application server

    mvn wildfly:undeploy - undeploy the quickstart

### Instructions to debug the redis server
- Install stunnel and then create a file on `/etc/stunnel/` named `redis-cli.conf`
- Insert the following information:
    ```bash
      [redis-cli]
      client = yes
      accept = 127.0.0.1:6380
      connect = sccredis41631.redis.cache.windows.net:6380
    ```
- Run:
    ```bash
    sudo stunnel4 /etc/stunnel/redis-cli.conf
    ```
- Connect to the redis client:
    ```bash
    redis-cli -p $REDIS_ACCESS_PORT -a $REDIS_ACCESS_KEY
    ```

TODO: write here the previous steps to turn on the stunnel with the configurations and the new way to access the 
client -- https://stackoverflow.com/questions/52571211/error-connection-reset-by-peer-while-connecting-to-elastic-cache-using-stunnal.

### Instructions to run all the tests
On the test directory, inside source, execute the following commands:
    ```bash
       chmod +x ./run_all_tests.sh
       ./run_all_tests.sh
    ```

### TODO notes

1) Add the code for the script to create all the containers on Cosmos. *DONE*

2) Correct the code on the update of the post number of likes. *DONE*

3) The caching system with REDIS is to correct. *SEMI-DONE* -- maybe add caching in some other features. Also, test the redis-cli
to see if the data is there. The data is not being saved on REDIS.

-- The redis-cli is supposed to be turned off when flagged. This is a TODO implementation.

-- The cache is good for now. Go to 4. Meanwhile, remember to come here.

3.a) Commit changes to GitHub *DONE*

4) The testing framework is to implement as well. *ATM*

-- Make the scripts with more test cases.   

5) Implement the serverless functions. *ALMOST DONE*

-- It misses the testing for the cache that is coded there, and then change for MostLikedPosts.

6) See the new file about the project -- geo-replication, testing, ...

7) Implement the Advanced Search.

8) Azure Mgmt needs geo-replication as well.

