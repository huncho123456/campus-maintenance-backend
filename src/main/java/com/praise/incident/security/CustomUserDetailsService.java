package com.praise.incident.security;


import com.praise.incident.entity.UserEntity;
import com.praise.incident.exception.NotFoundException;
import com.praise.incident.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        return AuthUser.builder()
                .user(user)
                .build();
    }
}
