package org.example.vegnbioapi.security.auth;

import lombok.extern.slf4j.Slf4j;

import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * This service has the responsibility to loads user from database
 */
@Slf4j
@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(">> User Details Service ");
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        // Account checking
        /*if (!user.isVerified()) {
            throw new RuntimeException("Votre compte n'a pas encore été validé par un administrateur.");
        }*/
        log.info(">> loadUserByUsername {}",  user.toString());
        return UserDetailsImp.build(user);
    }
}
