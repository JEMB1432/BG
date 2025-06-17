package jemb.bistrogurmand.views.Admin;

public class User {
    private String nameUser;
    private String rolUser;
    private String idUser;
    private String emailUser;

    public User(String nameUser, String rolUser, String idUser, String emailUser) {
        this.nameUser = nameUser;
        this.rolUser = rolUser;
        this.idUser = idUser;
        this.emailUser = emailUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public String getRolUser() {
        return rolUser;
    }

    public String getEmailUser() {
        return emailUser;
    }
}
