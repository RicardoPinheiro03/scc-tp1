package scc.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post implements Serializable {
    private String id;
    private String community;
    private String userCreator;
    private Long dateOfCreation;
    private String linkMultimedia;
    private String refParent;
    private int numberLikes;
    private String title;
    private String textMessage;
    private List<String> likedPost;

    public List<String> getListWhoLiked() { return likedPost; }

    public String getCommunity() { return community; }

    public String getUserCreator() { return userCreator; }

    public Long getDateOfCreation() { return dateOfCreation; }

    public String getLinkMultimedia() { return linkMultimedia; }

    public String getRefParent() { return refParent; }

    public String getTitle() { return title; }

    public String getId() {
        return id;
    }

    public String getTextMessage() { return textMessage; }

    public int getNumberLikes() { return numberLikes; }

    public void setCommunity(String community) { this.community = community; }

    public void setUserCreator(String userCreator) { this.userCreator = userCreator; }

    public void setDateOfCreation(Long dateOfCreation) { this.dateOfCreation = dateOfCreation; }

    public void setLinkMultimedia(String linkMultimedia) { this.linkMultimedia = linkMultimedia; }

    public void setRefParent(String refParent) { this.refParent = refParent; }

    public void setNumberLikes(String id) {
        boolean found = false;

        if(!likedPost.isEmpty()) {
            for(String s : likedPost) {
                if(id.equals(s)) {
                    found = true;
                }
            }
        } // correct this TODO

        if(!found || likedPost.isEmpty()) {
            likedPost.add(id);
            this.numberLikes++;
        }

    } // Doesn't we have to have the reference to the user who liked?

    /*public void setNumberLikes() {
        this.numberLikes++;
    }*/

    public void unsetNumberLikes(String id) {
        this.numberLikes--;
        likedPost.remove(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) { this.title = title; }

    public void setTextMessage(String textMessage) { this.textMessage = textMessage; }
}
