### Instructions to deploy
Execute this command on the source of the project:

```mvn azure-webapp:config & mvn package azure-webapp:deploy```

### Instructions to make local environment
    mvn package wildfly:deploy

    mvn wildfly:deploy - deploy any changes to the application to the application server

    mvn wildfly:undeploy - undeploy the quickstart

### Instructions to debug the redis server
- Install stunnel and then create a file on /etc/stunnel/ named redis-cli.conf
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

### TODO notes

1) Add the code for the script to create all the containers on Cosmos. *DONE*

2) Correct the code on the update of the post number of likes. *DONE*

3) The caching system with REDIS is to correct. *ATM* -- maybe add caching in some other features. Also, test the redis-cli
to see if the data is there. The data is not being saved on REDIS.

-- The redis-cli is supposed to be turned off when flagged. This is a TODO implementation.

3.a) Commit changes to GitHub

4) The testing framework is to implement as well.

5) Implement the serverless functions. Go to the pratical lecture on Friday.

6) See the new file about the project -- geo-replication, testing, 

- Executar o azuremanagement (ou seja mandar tudo abaixo e deixar a classe executar) DONE

- Refactor no MediaResource (em principio, a cosmosdb n precisa de mais alteracoes) DONE

- Fazer deploy e ver se ta tudo correcto DONE

- GOTO 2

