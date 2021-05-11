package dem.xbitly.eventplatform.users;

public class User
{
  String username;

    public User(String username) {
        this.username = username;
    }

    public User(){

    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }
}
