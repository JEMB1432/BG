package jemb.bistrogurmand.views.Admin;

public class User {

    private String idUser;
    private String nameUser;
    private String lastNameUser;
    private String celPhoneUser;
    private String emailUser;
    private String rolUser;
    private String imageUser;

    public User(String idUser, String nameUser, String lastNameUser, String celPhoneUser, String emailUser, String rolUser, String imageUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.lastNameUser = lastNameUser;
        this.celPhoneUser = celPhoneUser;
        this.emailUser = emailUser;
        this.rolUser = rolUser;
        this.imageUser = imageUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getLastNameUser() {
        return lastNameUser;
    }

    public void setLastNameUser(String lastNameUser) {
        this.lastNameUser = lastNameUser;
    }

    public String getCelPhoneUser() {
        return celPhoneUser;
    }

    public void setCelPhoneUser(String celPhoneUser) {
        this.celPhoneUser = celPhoneUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getRolUser() {
        return rolUser;
    }

    public void setRolUser(String rolUser) {
        this.rolUser = rolUser;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }
}
