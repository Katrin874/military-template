package ua.edu.viti.military.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.entity.User;
import ua.edu.viti.military.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional // Важливо для завантаження зв'язків (Roles)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Шукаємо користувача в базі
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Користувача не знайдено: " + username));

        // 2. Перетворюємо наші Roles на Spring Security Authorities
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // 3. Повертаємо стандартний об'єкт Spring Security User
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.getEnabled()) // Якщо enabled = false, то акаунт заблоковано
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }
}