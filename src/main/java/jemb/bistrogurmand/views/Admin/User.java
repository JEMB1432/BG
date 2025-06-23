package jemb.bistrogurmand.views.Admin;

public class User {
    private String userID;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String rolUser;
    private String userImage;

    public User(String userID, String firstName, String lastName, String phone,
                String email, String rolUser, String userImage) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.rolUser = rolUser;
        this.userImage = userImage;
    }

    // Getters deben seguir exactamente el patrón "get" + nombre de propiedad (case sensitive)
    public String getUserID() { return userID; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getPhone() { return phone; }

    public String getEmail() { return email; }

    // Para "rolUser" el getter debe ser getRolUser()
    public String getRolUser() { return rolUser; }

    public String getUserImage() { return userImage; }

    // Setters (opcionales para TableView a menos que necesites edición)
    public void setUserID(String userID) { this.userID = userID; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setEmail(String email) { this.email = email; }

    public void setRolUser(String rolUser) { this.rolUser = rolUser; }

    public void setUserImage(String userImage) { this.userImage = userImage; }
}