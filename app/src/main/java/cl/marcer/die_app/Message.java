package cl.marcer.die_app;

/**
 * Created by Alberto Galleguillos on 4/3/17.
 */

public class Message {
    private String photoUrl;
    private String author;
    private String title;
    private String body;
    private Long date;

    public Message() {
        //Needed by Firebase init.
    }

    public Message(String photoUrl, String author, String title, String body, Long date) {
        this.photoUrl = photoUrl;
        this.author = author;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDate() { return date; }

    public void setDate(Long date) { this.date = date; }
}

