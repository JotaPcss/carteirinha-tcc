package com.unilasalle.carteirinha.digital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF (essencial para API REST stateless)
            .csrf(csrf -> csrf.disable())

            // Configurar CORS (permite requisições do frontend na mesma origem ou de outras)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Política de sessão stateless (sem sessão no servidor)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
            	    // 1. Endpoints da API públicos (permitidos sem token)
            	    .requestMatchers("/api/estudantes/cadastro", "/api/auth/login", "/api/validacao/**", "/api/cursos").permitAll()
            	    // 2. Qualquer outra chamada para /api/** exige autenticação
            	    .requestMatchers("/api/**").authenticated()
            	    // 3. Todo o resto (frontend, assets, etc.) é público
            	    .anyRequest().permitAll()
            	)
            
            // Adicionar o filtro JWT antes do filtro padrão de autenticação
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração de CORS – permite requisições do frontend (mesma origem ou de outra porta)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Como o frontend agora está servido na mesma porta (8080), pode-se usar "*" ou especificar a origem.
        List<String> origins = new ArrayList<>(Arrays.asList("http://localhost:8080", "http://localhost:3000"));
        String extraOrigin = System.getenv("APP_BASE_URL");
        if (extraOrigin != null && !extraOrigin.isBlank()) origins.add(extraOrigin);
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


/* // Regras de autorização (ORDEM IMPORTANTE: específicas primeiro)
.authorizeHttpRequests(auth -> auth
    // 1. Recursos estáticos do frontend (totalmente públicos)
    .requestMatchers("/", "/index.html", "/assets/**", "/favicon.svg", "/icons.svg").permitAll()
    
    // 2. Endpoints da API públicos (cadastro, login, validação)
    .requestMatchers("/api/estudantes/cadastro", "/api/auth/login", "/api/validacao/**").permitAll()
    
    // 3. Endpoints exclusivos para ADMIN
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    
    // 4. Endpoints exclusivos para ESTUDANTE autenticado
    .requestMatchers("/api/estudantes/me", "/api/estudantes/upload-foto", "/api/estudantes/carteirinha/**").hasRole("ESTUDANTE")
    
    // 5. Qualquer outra requisição não especificada exige autenticação
    .anyRequest().authenticated()
) */