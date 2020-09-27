package guru.sfg.brewery.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class RestUrlAuthFilter extends  AbstractRestAuthFilter {


    public RestUrlAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    protected String getPassword(HttpServletRequest request) {
        String value = request.getParameter("password");
        if(Objects.isNull(value)){
            value = "";
        }
        return value;
    }

    @Override
    protected String getUsername(HttpServletRequest request) {
        String value = request.getParameter("username") ;
        if(Objects.isNull(value)){
            value = "";
        }
        return value;
    }
}
