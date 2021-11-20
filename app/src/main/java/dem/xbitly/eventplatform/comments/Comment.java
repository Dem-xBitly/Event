package dem.xbitly.eventplatform.comments;

public class Comment {

    private String username, text, date, image;


    public Comment(String username, String text, String date, String image){
        this.username = username;
        this.text = text;
        this.date = date;
        this.image = image;
    }


    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getImageURL(){return image;}
}
