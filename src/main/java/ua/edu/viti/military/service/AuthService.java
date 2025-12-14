package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.LoginRequestDTO;
import ua.edu.viti.military.dto.request.RegisterRequestDTO;
import ua.edu.viti.military.dto.response.JwtResponseDTO;
import ua.edu.viti.military.entity.Role;
import ua.edu.viti.military.entity.RoleName;
import ua.edu.viti.military.entity.User;
import ua.edu.viti.military.repository.RoleRepository;
import ua.edu.viti.military.repository.UserRepository;
import ua.edu.viti.military.security.JwtService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // === ЛОГІН (Вхід) ===
    public JwtResponseDTO login(LoginRequestDTO dto) {
        // 1. Spring Security перевіряє логін/пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Якщо все ок - генеруємо токен
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponseDTO(jwt, userDetails.getUsername(), roles);
    }

    // === РЕЄСТРАЦІЯ ===
    @Transactional
    public String register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Помилка: Такий користувач вже існує!");
        }

        // Створюємо нового користувача
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword())) // Шифруємо пароль
                .fullName(dto.getFullName())
                .rank(dto.getRank())
                .enabled(true)
                .build();

        // Призначаємо ролі
        Set<Role> roles = new HashSet<>();
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            // Якщо роль не вказана, даємо роль СОЛДАТ (для перегляду)
            roles.add(roleRepository.findByName(RoleName.ROLE_SOLDIER)
                    .orElseThrow(() -> new RuntimeException("Роль ROLE_SOLDIER не знайдена в БД")));
        } else {
            dto.getRoles().forEach(roleStr -> {
                try {
                    RoleName roleName = RoleName.valueOf(roleStr);
                    Role role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Роль не знайдена: " + roleName));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Невідома роль: " + roleStr);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return "Користувача успішно зареєстровано!";
    }
}