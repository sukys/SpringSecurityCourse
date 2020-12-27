package guru.sfg.brewery.web.controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String refister2fa(Model model){

        User user = getUser();
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("=====> Google QR Url: {}", url);


        model.addAttribute("googleurl", url);

        return "/user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode){
        User user = getUser();
        log.debug("Entered Code is: {}", verifyCode);
        log.debug("User found is: {}", user.getUsername());

        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode, new Date().getTime())){
            User savedUser = userRepository.findById(user.getId()).orElseThrow();
            savedUser.setUseGoogle2Fa(true);
            userRepository.save(savedUser);
            log.debug("Authenticated!!");
            return "index";
        } else {
            // bad code
            log.debug("Authentication Failed.");
            return "/user/register2fa";
        }

    }

    @GetMapping("/verify2fa")
    public String verify2fa(){
        return "/user/verify2fa";
    }

    @PostMapping("/verify2fa")
    public String verify2Fa(@RequestParam Integer verifyCode){
        User user = getUser();
        log.debug("Verifying 2FA for user {};", user.getUsername());
        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)){
            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2FaRequired(false);
            log.debug("Verification success.");
            return "/index";
        } else {
            log.debug("Verification failure.");
            return "/user/verify2fa";
        }
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
