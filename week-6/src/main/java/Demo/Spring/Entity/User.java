/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tran Anh Tuan
 */
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private Set<String> role;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
