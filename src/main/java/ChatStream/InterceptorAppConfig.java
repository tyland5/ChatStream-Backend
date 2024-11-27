package ChatStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Component
class AuthInterceptor implements HandlerInterceptor {
    private final Set<String> exemptPaths = new HashSet<>(Arrays.asList("/check-credentials",
            "/logout", "/change-password", "/create-user", "/check-email-used", "/check-logged-in",
            "/check-username-available", "/send-verif-email-register", "/send-verif-email-forgot-pw"));

    // look at authentication before passing it to the controller. Kind of like middleware in nodejs
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // need to for post preflight??? idk
        if(exemptPaths.contains(request.getRequestURI()) || request.getMethod().equals("OPTIONS")){
            return true;
        }

        HttpSession session = request.getSession(false);

        if(session == null){
            response.setStatus(401);
            return false;
        }

        if(request.getMethod().equals("DELETE") || request.getMethod().equals("POST") || request.getMethod().equals("PUT")){
            if(request.getHeader("csrf") == null || !request.getHeader("csrf").equals(session.getAttribute("csrf"))){
                response.setStatus(401);
                return false;
            }
        }

        return true;
    }
}


@Component
public class InterceptorAppConfig implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }
}



