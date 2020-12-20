package guru.sfg.brewery.security.google;


import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private final Google2FaFailureHandler google2FaFailureHandler = new Google2FaFailureHandler();
    private final RequestMatcher ulrIs2fa = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher urlResource = new AntPathRequestMatcher("/resources/**");


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        StaticResourceRequest.StaticResourceRequestMatcher staticResourceRequestMatcher= PathRequest.toStaticResources().atCommonLocations();

        if(ulrIs2fa.matches(request) || urlResource.matches(request) || staticResourceRequestMatcher.matcher(request).isMatch()){
            filterChain.doFilter(request, response);
            return;
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.nonNull(authentication) && !authenticationTrustResolver.isAnonymous(authentication)){
            log.debug("Processing 2FA Filter.");
            if(Objects.nonNull(authentication.getPrincipal()) && authentication.getPrincipal() instanceof User){
                User user = (User) authentication.getPrincipal();
                if(user.getUseGoogle2Fa() && user.getGoogle2FaRequired()){
                    log.debug("2FA required.");
                    google2FaFailureHandler.onAuthenticationFailure(request, response, null);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
