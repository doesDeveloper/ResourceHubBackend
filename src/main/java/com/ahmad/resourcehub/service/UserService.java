package com.ahmad.resourcehub.service;

import com.ahmad.resourcehub.dto.UserRegisterDTO;
import com.ahmad.resourcehub.exception.ConflictException;
import com.ahmad.resourcehub.model.User;
import com.ahmad.resourcehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.getPassword());
        builder.roles(user.getRole().toString());
        return builder.build();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User registerUser(UserRegisterDTO userRegisterDTO) {
        if (findByUsername(userRegisterDTO.getUsername()) != null)
            throw new ConflictException("User", "username", userRegisterDTO.getUsername());
        userRegisterDTO.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(userRegisterDTO.getPassword())
                .email(userRegisterDTO.getEmail())
                .fullName(userRegisterDTO.getFullName())
                .role(User.Role.USER)
                .build();
        return saveUser(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
