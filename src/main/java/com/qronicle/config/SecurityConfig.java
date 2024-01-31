package com.qronicle.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
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
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

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
    private UserDetailsService userDetailsService;

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


    // SecurityFilterChain applied for authentication/ registration requests
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2AuthenticationSecurityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(authorizeRequests ->
            authorizeRequests   // configure request authorization handling
                    .antMatchers("/authenticate", "/login", "/register/**", "/*.css", "/js/**")
                    .permitAll()
                )
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/process").and()
                .csrf().disable()
                .cors().and()
                .authenticationProvider(authenticationProvider())
                .oauth2Login()
                .loginPage("/login")
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService());

        return http.build();
    }

    // SecurityFilterChain applied to requests made to resource server
    @Bean
    @Order(2)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
            authorizeRequests
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET, "/secure/**").hasAnyAuthority("ROLE_USER")
                .antMatchers(HttpMethod.GET,  "/test", "/tags/**", "/items/**", "/files/**", "/users/**").permitAll()
                .antMatchers(HttpMethod.POST, "/items/**", "/files/**", "/users/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .csrf().disable()
            .cors().and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

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

}
