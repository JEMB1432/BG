package jemb.bistrogurmand.utils;

public class Category {
    private int ID_Category;
    private String name;
    private String state;

    public Category() {}

    public Category(int ID_Category, String name, String state) {
        this.ID_Category = ID_Category;
        this.name = name;
        this.state = state;
    }

    public int getID_Category() {
        return ID_Category;
    }

    public void setID_Category(int ID_Category) {
        this.ID_Category = ID_Category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
