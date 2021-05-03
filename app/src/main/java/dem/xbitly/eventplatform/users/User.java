package dem.xbitly.eventplatform.users;

public class User
{
  String username,email;
    User()
    {

    }
    public User(String username) {
        this.username = username;
        this.email = email;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}
