package org.han.webtest.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false , length = 100)
    private String username;

    @Column(nullable = false, unique = true , length = 100)
    private String email;

    @Column(nullable = false , length = 100)
    private String password;

    @Column(nullable = false,length = 15)
    private String role = "CUSTOMER";

    public UserModel() {}

    public UserModel(String username, String email , String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
