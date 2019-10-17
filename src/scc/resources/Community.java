package scc.resources;

import java.io.Serializable;

public class Community implements Serializable {
    private String id;
    private String communityName;

    public String getCommunityName() {
        return communityName;
    }

    public void setName(String communityName) {
        this.communityName = communityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
