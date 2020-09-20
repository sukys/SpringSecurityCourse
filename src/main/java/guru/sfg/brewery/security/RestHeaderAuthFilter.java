package guru.sfg.brewery.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {


    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }





    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (logger.isDebugEnabled()) {
            logger.debug("Request is to process authentication");
        }
        Authentication authResult = attemptAuthentication(request, response);

        if(Objects.nonNull(authResult)){
            successfulAuthentication(request, response, chain, authResult);
        } else {
          chain.doFilter(request, response);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String username = getUsername(request);
        String password = getPassword(request);

        log.debug("Authenticating user: [{}].", username);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        if(!StringUtils.isEmpty(username)){
            return this.getAuthenticationManager().authenticate(token);
        } else {
            return null;
        }

    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }


    private String getPassword(HttpServletRequest request) {
        String value = request.getHeader("Api-Secret");
        if(Objects.isNull(value)){
            value = "";
        }
        return value;
    }

    private String getUsername(HttpServletRequest request) {
        String value =  request.getHeader("Api-Key");
        if(Objects.isNull(value)){
            value = "";
        }
        return value;
    }
}
