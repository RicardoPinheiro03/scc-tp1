package scc.srv;

import scc.utils.AzureProperties;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class MainApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add( CommunityResource.class );
        set.add( MediaResource.class );
        set.add( PostResource.class );
        set.add( UserResource.class );
        set.add( CDBConnection.class );
        set.add( MainResource.class );
        set.add( AzureProperties.class );
        set.add( RedisCache.class );
        return set;
    }
}