package org.example;

public class Category {

    private int    categoryId;
    private String categoryName;
    private String description;

    public Category() {}

    public Category(int categoryId, String categoryName, String description) {
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
        this.description  = description;
    }

    public void display() {
        System.out.println("Category ID   : " + categoryId);
        System.out.println("Category Name : " + categoryName);
        System.out.println("Description   : " + description);
        System.out.println("---------------------------");
    }

    public int    getCategoryId()                         { return categoryId; }
    public void   setCategoryId(int categoryId)           { this.categoryId = categoryId; }

    public String getCategoryName()                       { return categoryName; }
    public void   setCategoryName(String categoryName)    { this.categoryName = categoryName; }

    public String getDescription()                        { return description; }
    public void   setDescription(String description)      { this.description = description; }
}
