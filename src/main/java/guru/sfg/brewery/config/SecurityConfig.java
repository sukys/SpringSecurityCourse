package guru.sfg.brewery.config;

import guru.sfg.brewery.security.JpaUserDetailService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestUrlAuthFilter restUrlAuthFilter(AuthenticationManager authenticationManager) {
        RestUrlAuthFilter filter = new RestUrlAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http.addFilterBefore(restUrlAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/h2-console/**").permitAll() //  do not use in production
                    .antMatchers("/", "/webjars/**", "/resources/**", "/login").permitAll()
                    .antMatchers("/beers*", "/beers/*", "/beers/find").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    .antMatchers(HttpMethod.GET, "/api/v1/beer/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    .mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
                    .mvcMatchers("/brewery/breweries").hasAnyRole("ADMIN", "CUSTOMER")
                    .mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries/**").hasAnyRole("ADMIN","CUSTOMER")

                    //.mvcMatchers(HttpMethod.GET, "/api/v1/beer/**", "/api/v1/beerUpc").hasAnyRole("ADMIN", "CUSTOMER", "USER")

                    .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").hasAnyRole("ADMIN", "CUSTOMER", "USER");
        })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();

        // h2 console config
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

    // @Autowired
    // JpaUserDetailService jpaUserDetailService;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // Using jpaUserDetailService
//        auth.userDetailsService(this.jpaUserDetailService).passwordEncoder(passwordEncoder());
//
//        // In Memory Authentication:
//        auth.inMemoryAuthentication()
//                .withUser("spring").password("{bcrypt}$2a$10$.1hpoAGNM8lJH/twz.M67O6pUKgZ8CR6RVhNx2LPiLHIry14Fxuui").roles("ADMIN")
//                .and()
//                .withUser("user").password("{sha256}0b6881efec12c737dc7c48737399274d06c34e08137f21958713d972dfcf483d1184633014a6694d").roles("USER")
//                .and()
//                .withUser("scott").password("{ldap}{SSHA}onAYT82u+1Zn09I9OlOCfujQOmEPj8v7iTN3QQ==").roles("CUSTOMER")
//                .and()
//                .withUser("scott_15").password("{bcrypt15}$2a$15$Q1ScfKnd5KldAA4KKQRpNOn/CVGj2DvEhoEPVdXbgP9uLFGLVoR3a").roles("CUSTOMER");
//    }

//    @Bean
//    @Override
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }


}
