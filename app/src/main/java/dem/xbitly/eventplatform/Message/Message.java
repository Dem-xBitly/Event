package dem.xbitly.eventplatform.Message;

public class Message {
    String text;
    String from;

    public Message (String text, String from){
        this.text = text;
        this.from = from;
    }

    public String getText(){return text;}

    public void setText(String name){this.text=text;}

    public String getFrom(){return from;}

    public void setName(String from){this.from=from;}
}
