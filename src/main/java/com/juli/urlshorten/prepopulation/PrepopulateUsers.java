package com.juli.urlshorten.prepopulation;

import com.juli.urlshorten.model.entity.RoleEntity;
import com.juli.urlshorten.model.entity.UserEntity;
import com.juli.urlshorten.repository.RoleRepository;
import com.juli.urlshorten.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PrepopulateUsers {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PrepopulateUsers(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void prepopulate() throws Exception {

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ADMIN");

        RoleEntity userRole = new RoleEntity();
        userRole.setName("USER");

        if (roleRepository.count() == 0) {
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            System.out.println("Roles added to the database");
        }

        if (userRepository.count() == 0) {
            Set<RoleEntity> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            UserEntity user1 = new UserEntity();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password1"));
            user1.setRoles(adminRoles);

            Set<RoleEntity> userRoles = new HashSet<>();
            userRoles.add(userRole);

            UserEntity user2 = new UserEntity();
            user2.setUsername("user2");
            user2.setPassword(passwordEncoder.encode("password2"));
            user2.setRoles(userRoles);

            userRepository.save(user1);
            userRepository.save(user2);
            System.out.println("Users added to the database");
        }
    }
}
