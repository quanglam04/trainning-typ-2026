/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Demo.Spring.Repository;

import Demo.Spring.Entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Tran Anh Tuan
 */
@Repository
public interface RepositoryUser extends JpaRepository<User, String> {
    List<User> findByusername(String userName);
    Optional<User> findByUsername(String userName);
    List<User> findByemail(String email);
    List<User> findByid(String id);
}