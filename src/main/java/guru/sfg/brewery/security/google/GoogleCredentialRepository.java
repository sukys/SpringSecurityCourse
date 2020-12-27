package guru.sfg.brewery.security.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;


    /**
     * This method retrieves the Base32-encoded private key of the given user.
     *
     * @param userName the user whose private key shall be retrieved.
     * @return the private key of the specified user.
     */
    @Override
    public String getSecretKey(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow();
        return user.getGoogle2FaSecret();
    }

    /**
     * This method saves the user credentials.
     *
     * @param userName       the user whose data shall be saved.
     * @param secretKey      the generated key.
     * @param validationCode the validation code.
     * @param scratchCodes   the list of scratch codes.
     */
    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        User user = userRepository.findByUsername(userName).orElseThrow();
        user.setGoogle2FaSecret(secretKey);
        user.setUseGoogle2Fa(true);
        log.debug("User Found: {}",user.getUsername());
        log.debug("Saving: \n\tGoogle2faSecret: {}\n\tUse 2FA? {} ", secretKey, true);
        userRepository.save(user);
    }
}
