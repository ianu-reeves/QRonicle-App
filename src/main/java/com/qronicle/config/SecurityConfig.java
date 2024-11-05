package com.qronicle.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.qronicle.filter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


// Configuration for the security side of the web application, including how to handle failed/ successful
// logins/ logouts, password encryption, and resource protection
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final Environment env;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    private static final List<String> oAuthClients = Arrays.asList("google", "github");
    private static final String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    @Value("${jwt.rsa.key.public}")
    private RSAPublicKey publicKey;

    @Value("${jwt.rsa.key.private}")
    private RSAPrivateKey privateKey;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public SecurityConfig(Environment env) {
        this.env = env;
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter () {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter( grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Access-Control-Allow-Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Access-Control-Allow-Headers"));
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("Access-Control-Allow-Methods",
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // SecurityFilterChain applied to requests made to resource server
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/api/v1/**")
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET, "/auth/secure/**").hasAnyAuthority("ROLE_USER")
                .antMatchers("/api/v1/test/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/tags/**", "/api/v1/items/**", "/files/**", "/users/**").hasAnyAuthority("ROLE_USER")
                .antMatchers(HttpMethod.POST, "/items/**", "/files/**", "/users/**").hasRole("USER")
            .anyRequest().authenticated()
            )
            .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(authenticationEntryPoint))
            .csrf().disable()
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
//            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    // SecurityFilterChain applied for authentication/ registration requests
    @Bean
    @Order(2)
    public SecurityFilterChain oauth2AuthenticationSecurityFilterChain(HttpSecurity http) throws Exception{
        http
            .authorizeRequests(authorizeRequests -> authorizeRequests   // configure request authorization handling
                    .antMatchers("/auth/signout").hasRole("USER")
                    .antMatchers("/auth/**", "/*.css", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(authenticationEntryPoint))
            .csrf().disable()
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin()
            .loginPage("/auth/login")
            .loginProcessingUrl("/process").and()
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .oauth2Login()
                .authorizationEndpoint().authorizationRequestResolver(
                    new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository(),
                        "/auth/oauth2/authorization"
                )).and()
            .loginPage("/auth/login")
                .defaultSuccessUrl("http://localhost:3000/")
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .clientRegistrationRepository(clientRegistrationRepository())
            .authorizedClientService(authorizedClientService());

        return http.build();
    }

    // Returns a repository of all valid oAuth sign-in options with client credentials
    // for the application configured for each.
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = oAuthClients.stream()
            .map(this::getRegistration) // get ClientRegistration for each valid oAuth client
            .filter(Objects::nonNull)   // remove any ClientRegistrations that returned a null value
            .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    // Return a ClientRegistration object with application properties based on the passed client.
    // Valid clients are "google" and "github".
    private ClientRegistration getRegistration(String clientName) {
        if (clientName == null) {
            return null;
        }

        // retrieve client ID & secret for passed client from properties file
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + clientName + ".client-id");
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + clientName + ".client-secret");

        ClientRegistration clientRegistration = null;
        if (clientName.equals("google")) {
            clientRegistration = CommonOAuth2Provider.GOOGLE
                .getBuilder(clientName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        }
        if (clientName.equals("github")) {
            clientRegistration = CommonOAuth2Provider.GITHUB
                .getBuilder(clientName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        }

        return clientRegistration;
    }

    //TODO: get this to create list of strings with security prefix prepended to use in authorizing requests
    private String[] getApiWhitelist() {
        String[] allowedEndpoints = {"/items", "/tags", "/files", "/users"};
        String prefix = env.getProperty("app.api.v1.prefix");
        for (String endpoint : allowedEndpoints) {
            endpoint = prefix + endpoint;
        }
        System.out.println(allowedEndpoints);
        return allowedEndpoints;
    }

}
