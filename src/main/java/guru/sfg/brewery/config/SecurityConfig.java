package guru.sfg.brewery.config;

import guru.sfg.brewery.security.JpaUserDetailService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;


    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(authorize -> {
                authorize
                    .antMatchers("/h2-console/**").permitAll() //do not use in production!
                    .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll();
            } )
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin(loginConfigurer -> {
                loginConfigurer
                    .loginProcessingUrl("/login")
                    .loginPage("/").permitAll()
                    .successForwardUrl("/")
                    .defaultSuccessUrl("/")
                    .failureUrl("/?error");
            })
            .logout(logoutConfigurer -> {
                logoutConfigurer
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/?logout")
                    .permitAll();
            })
            .httpBasic()
            .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
            .and()
                .rememberMe()
                .tokenRepository(persistentTokenRepository)
                // .key("sfg-key")
                .userDetailsService(userDetailsService);

        //h2 console config
        http.headers().frameOptions().sameOrigin();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        // return new LdapShaPasswordEncoder();
        // return new BCryptPasswordEncoder(12); 
        // return NoOpPasswordEncoder.getInstance();
        // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
