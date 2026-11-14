/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Controller;
import Demo.Spring.DTO.Request.RequestFirstName;
import Demo.Spring.DTO.Request.RequestUser;
//import Demo.Spring.Service.StudentService;
import Demo.Spring.Service.UserService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Tran Anh Tuan
 */

@CommonsLog
@RestController
@RequestMapping("/users")
public class Controller {
    @Autowired
    private UserService UserService;
    
    // SignUp
    @PostMapping("/sign-up")
    public Object signUp(@RequestBody RequestUser Request){
        return UserService.create(Request);
    }
    // Login
    @PostMapping("/login")
    public Object login(@RequestBody RequestUser Request){
        return UserService.login(Request);
    }
    // Update firstName
    @PatchMapping("/update-firstname/{username}")
    public Object replace(@PathVariable String username, @RequestBody RequestFirstName request) throws JOSEException, ParseException{
        return UserService.update(username, request);
    }
    @GetMapping("/information")
    public Object get(){
        return UserService.getInformation();
    }
}

