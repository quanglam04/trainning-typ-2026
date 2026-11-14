package Demo.Spring.Config;

import Demo.Spring.Entity.User;
import Demo.Spring.Repository.RepositoryUser;
import Demo.Spring.Roles.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(RepositoryUser userRepository){
        return args -> {
            if(userRepository.findByusername("admin").isEmpty()){
                HashSet<String> roles = new HashSet<>();
                roles.add(Roles.ADMIN.name());
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(roles)
                        .build();

                userRepository.save(user);
            }
        };
    }
}
