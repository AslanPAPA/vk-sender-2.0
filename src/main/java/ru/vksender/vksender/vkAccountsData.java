package ru.vksender.vksender;

public class vkAccountsData {

    private Integer id;
    private String token;
    private String accountName;
    private String description;
    private String image;
    private String firstName;
    private String lastName;
    private String adminUsername;


    public vkAccountsData(Integer id, String token, String accountName, String description, String image, String firstName, String lastName, String adminUsername) {
        this.id = id;
        this.token = token;
        this.accountName = accountName;
        this.description = description;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adminUsername = adminUsername;

    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAdminUsername() {
        return adminUsername;
    }



}
