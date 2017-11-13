package pl.ark.chr.timelyzer.auth;

import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;
import pl.ark.chr.timelyzer.persistence.User;
import pl.ark.chr.timelyzer.repository.UserRepository;
import pl.ark.chr.timelyzer.util.ApplicationProperties;

public class MongoUsernamePasswordAuthenticator implements UsernamePasswordAuthenticator {

    private final UserRepository userRepository;
    private final BCryptPasswordService bCryptPasswordService;

    public MongoUsernamePasswordAuthenticator(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordService = new BCryptPasswordService(ApplicationProperties.getBCryptStrength());
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials) {
        if (credentials == null) {
            this.throwsException("No credential");
        }

        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            this.throwsException("Username cannot be blank");
        }

        if (CommonHelper.isBlank(password)) {
            this.throwsException("Password cannot be blank");
        }

        final User[] userFromDb = new User[1];
        AuthSubscriber authSubscriber = new AuthSubscriber((user) -> userFromDb[0] = user);
        userRepository.findByUsername(username).subscribe(authSubscriber);

        authSubscriber.await();

        if (userFromDb[0] == null) {
            throwsException("Username or password is wrong");
        } else {
            validateUserPassword(credentials, username, userFromDb[0]);
        }
    }

    private void validateUserPassword(UsernamePasswordCredentials credentials, String username, User user) {
        if(bCryptPasswordService.passwordsMatch(credentials.getPassword(), user.getPassword())) {
            HttpProfile profile = new HttpProfile();
            profile.setId(username);
            profile.addAttribute("username", username);
            credentials.setUserProfile(profile);
        } else {
            this.throwsException("Passwords not match");
        }
    }

    protected void throwsException(String message) {
        throw new CredentialsException(message);
    }
}
