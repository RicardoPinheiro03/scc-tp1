package scc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class AzureProperties {
    public static final String BLOB_KEY = "BLOB_KEY";
    public static final String COSMOSDB_KEY = "COSMOSDB_KEY";
    public static final String COSMOSDB_URL = "COSMOSDB_URL";
    public static final String COSMOSDB_DATABASE = "COSMOSDB_DATABASE";
    public static final String REDIS_KEY = "REDIS_KEY";
    public static final String REDIS_URL = "REDIS_URL";

    public static final String PROPS_FILE = "/home/site/wwwroot/webapps/ROOT/WEB-INF/azure.props";
    public static Properties props; // ==== Try private as well ====

    public static synchronized Properties getProperties()  {
        if( props == null) {
            props = new Properties();

            try(InputStream input = new FileInputStream(PROPS_FILE)) {
                props.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }
}
