package com.juli.urlshorten.prepopulation;

import com.juli.urlshorten.model.entity.RoleEntity;
import com.juli.urlshorten.model.entity.UrlMappingEntity;
import com.juli.urlshorten.model.entity.UserEntity;
import com.juli.urlshorten.model.enums.ExpiryOptions;
import com.juli.urlshorten.repository.RoleRepository;
import com.juli.urlshorten.repository.UrlShortenRepository;
import com.juli.urlshorten.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class PrepopulateUsers {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UrlShortenRepository urlShortenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PrepopulateUsers(UserRepository userRepository, RoleRepository roleRepository, UrlShortenRepository urlShortenRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.urlShortenRepository = urlShortenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
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


            UrlMappingEntity urlMappingEntity = new UrlMappingEntity();
            urlMappingEntity.setOriginalUrl("https://www.google.com");
            urlMappingEntity.setShortUrl("ABCDEF");
            urlMappingEntity.setHitCount(10);
            urlMappingEntity.setExpiryDate(LocalDateTime.now().plus(ExpiryOptions.ONE_MINUTE.getDuration()));
            urlMappingEntity.setCreatedBy("user1");
            urlMappingEntity.setCreatedDate(LocalDateTime.now());

            urlShortenRepository.save(urlMappingEntity);

            UrlMappingEntity urlMappingEntity2 = new UrlMappingEntity();
            urlMappingEntity2.setOriginalUrl("https://www.facebook.com");
            urlMappingEntity2.setShortUrl("GHIJKL");
            urlMappingEntity2.setHitCount(5);
            urlMappingEntity2.setExpiryDate(LocalDateTime.now().plus(ExpiryOptions.FIVE_MINUTES.getDuration()));
            urlMappingEntity2.setCreatedBy("user2");
            urlMappingEntity2.setCreatedDate(LocalDateTime.now());

            urlShortenRepository.save(urlMappingEntity2);

            System.out.println("URLs added to the database");
        }
    }
}
