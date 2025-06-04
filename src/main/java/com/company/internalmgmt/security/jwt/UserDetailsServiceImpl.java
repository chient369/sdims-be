package com.company.internalmgmt.security.jwt;

import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Tìm kiếm user bằng username hoặc email
        Optional<User> userByUsername = userRepository.findByUsername(login);
        if (userByUsername.isPresent()) {
            return UserDetailsImpl.build(userByUsername.get());
        }
        
        Optional<User> userByEmail = userRepository.findByEmail(login);
        System.out.println("userByEmail: " + userByEmail.toString());
        if (userByEmail.isPresent()) {
            return UserDetailsImpl.build(userByEmail.get());
        }
        
        throw new UsernameNotFoundException("User Not Found with username/email: " + login);
    }
} 
