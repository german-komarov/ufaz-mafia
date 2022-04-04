package com.mafia.mafiademo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mafia.mafiademo.services.UserDetailsServiceImpl;
import com.mafia.mafiademo.util.constants.UserRole;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configurable
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;


    public SecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            BCryptPasswordEncoder passwordEncoder,
            ObjectMapper mapper) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/actuator/**").hasRole(UserRole.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPointImpl())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .formLogin()
                .usernameParameter("email")
                .successHandler(new CustomAuthenticationSuccessHandlerImpl())
                .failureHandler(new CustomAuthenticationFailureHandlerImpl())
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.POST.name()))
                .and()
                .rememberMe()
                .tokenValiditySeconds(365 * 24 * 60 * 60)
                .and()
                .csrf().disable();
    }


    private class CustomAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            String sessionId = request.getSession().getId();
            String rememberToken = Arrays.stream(response.getHeader("Set-Cookie").split(";"))
                    .filter(s -> s.startsWith("remember"))
                    .findFirst()
                    .map(s -> s.split("=")[1])
                    .orElse(null);
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("JSESSIONID", sessionId);
            if (rememberToken != null) {
                responseParams.put("remember-me", rememberToken);
            }
            String json = mapper.writeValueAsString(responseParams);
            response.setContentLength(json.length());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(json);
        }
    }

    private class CustomAuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            processException(request, response, exception, HttpStatus.UNAUTHORIZED);
        }
    }

    private class CustomAuthenticationEntryPointImpl implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            processException(request, response, authException, HttpStatus.UNAUTHORIZED);
        }
    }

    private class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            processException(request, response, accessDeniedException, HttpStatus.FORBIDDEN);
        }
    }

    private void processException(HttpServletRequest request, HttpServletResponse response, Exception exception, HttpStatus status) throws IOException, ServletException {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("message", exception.getMessage());

        String json = mapper.writeValueAsString(responseParams);

        response.setContentLength(json.length());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(json);
    }
}
