/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Service;

import Demo.Spring.DTO.Request.RequestFirstName;
import Demo.Spring.DTO.Request.RequestUser;
import Demo.Spring.DTO.Request.RequestUserRabbitMQ;
import Demo.Spring.DTO.Response.ResponseUser;
import Demo.Spring.DTO.Response.SuccessResponse;
import Demo.Spring.Entity.User;
import Demo.Spring.Exceptions.AppException;

import static Demo.Spring.Exceptions.ErrorCode.*;
import static java.rmi.server.LogStream.log;

import Demo.Spring.Mapper.MapperUser;
import Demo.Spring.Repository.RepositoryUser;
import Demo.Spring.Roles.Roles;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/**
 *
 * @author Tran Anh Tuan
 */
@Service
public class UserService {
    private static final Log log = LogFactory.getLog(UserService.class);
    @Autowired
    private RepositoryUser repositoryuser;
    protected static final String SIGNER_KEY = "Ma6LbMpM7HC65LAyRS38h64oPmDyEnSU98gU5Rkq07Yl30vdV9gW7irBMI9VEQ6hgpUKo7eq0RgSs5kj";
    // Tạo JWT
    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtclaimsset = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", getString(user.getRole()))
                .build();
        Payload payload = new Payload(jwtclaimsset.toJSONObject());
        JWSObject jwtobject = new JWSObject(header, payload);
        
        try{
           jwtobject.sign(new MACSigner(SIGNER_KEY)); 
           return jwtobject.serialize();
        }
        catch(JOSEException e){
             throw new RuntimeException("Không thể tạo JWT", e);
        }
    }
    private String getString(Set<String> se){
        if(se.size() == 0) return "";
        else{
            String cur = "";
            for(String x: se){
                cur += x + " ";
            }
            return cur.trim();
        }
    }
    // Check JWT
    private boolean check(String token) throws JOSEException, ParseException{
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return signedJWT.verify(verifier) && expityTime.after(new Date());
    }

    public ResponseEntity<Object> update(String username, RequestFirstName request) throws JOSEException, ParseException{
        if(check(request.getToken()) != true){
            throw new AppException(TOKEN_ERROR);
        }
        else{
        List<User> listuser = repositoryuser.findByusername(username);
        if(listuser.size() == 0) throw new AppException(USER_NOT_EXIST);
        
        listuser.get(0).setFirstname(request.getFirstname());
        repositoryuser.save(listuser.get(0));
        return ResponseEntity.ok().body(new SuccessResponse(200, "Success", "firstName đã cập nhật: " + request.getFirstname()));
        }
    }


    public ResponseEntity<Object> create(RequestUser request){
        List<User> listuser= repositoryuser.findByusername(request.getUsername());
        List<User> listuser2 = repositoryuser.findByemail(request.getEmail());
        if(listuser.size() != 0 || listuser2.size() != 0) throw new AppException(USER_EXIST);
        
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User newuser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        newuser.setRole(roles);
        repositoryuser.save(newuser);
        String token = generateToken(newuser);
        return ResponseEntity.ok().body(new SuccessResponse(200, "Success", token));
    }
    
    public ResponseEntity<Object> login(RequestUser request){
        List<User> list = repositoryuser.findByusername(request.getUsername());
        if(list.size() == 0) throw new AppException(USER_NOT_EXIST);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if(passwordEncoder.matches(request.getPassword(), list.get(0).getPassword())){
            String token = generateToken(list.get(0));
            return ResponseEntity.ok().body(new SuccessResponse(200, "Success", token));
        }
        else throw new AppException(USER_NOT_EXIST);
    }
    @Autowired
    private MapperUser mapperuser;
    public ResponseEntity<Object> getInformation(){
        List<User> list = repositoryuser.findAll();
        List<ResponseUser> listResponse = new ArrayList<>();
        for(int i = 0; i< list.size(); i++){
            ResponseUser responseuser= mapperuser.toResponseUser(list.get(i));
            listResponse.add(responseuser);
        }
        return ResponseEntity.ok().body(new SuccessResponse<>(200, "Success", listResponse));
    }


    public ResponseEntity<Object> createMQ(RequestUserRabbitMQ request){
        List<User> listuser= repositoryuser.findByusername(request.getUsername());

        if(listuser.size() != 0) throw new AppException(USER_NOT_EXIST);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User newuser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        newuser.setRole(roles);
        repositoryuser.save(newuser);
        String token = generateToken(newuser);
        return ResponseEntity.ok().body(new SuccessResponse(200, "Success", "thanh cong"));
    }


}
