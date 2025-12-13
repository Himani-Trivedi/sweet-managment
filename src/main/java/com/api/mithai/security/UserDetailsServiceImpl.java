package com.api.mithai.security;

import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.auth.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + username));
        return getUserDetails(user);
    }

    private UserDetails getUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmailId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRoleName()
                ))
        );
    }
}
