package scc.resources;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private String id;
    private String communityName;
    private String userCreator;
    private Date dateOfCreation;
    private String linkMultimedia;
    private String refParent;
    private int numberLikes;
    private String title;
    private String textMessage;

    public String getCommunityName() { return communityName; }

    public String getUserCreator() { return userCreator; }

    public Date getDateOfCreation() { return dateOfCreation; }

    public String getLinkMultimedia() { return linkMultimedia; }

    public String getRefParent() { return refParent; }

    public String getTitle() { return title; }

    public String getId() {
        return id;
    }

    public String getTextMessage() { return textMessage; }

    public int getNumberLikes() { return numberLikes; }

    public void setCommunityName(String communityName) { this.communityName = communityName; }

    public void setUserCreator(String userCreator) { this.userCreator = userCreator; }

    public void setDateOfCreation(Date dateOfCreation) { this.dateOfCreation = dateOfCreation; }

    public void setLinkMultimedia(String linkMultimedia) { this.linkMultimedia = linkMultimedia; }

    public void setRefParent(String refParent) { this.refParent = refParent; }

    public void setNumberLikes() { this.numberLikes++; } // Doesn't we have to have the reference to the user who liked?

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) { this.title = title; }

    public void setTextMessage(String textMessage) { this.textMessage = textMessage; }
}
