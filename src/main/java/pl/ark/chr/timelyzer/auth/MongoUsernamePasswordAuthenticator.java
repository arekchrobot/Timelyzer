package pl.ark.chr.timelyzer.auth;

import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;
import pl.ark.chr.timelyzer.persistence.User;
import pl.ark.chr.timelyzer.repository.UserRepository;

public class MongoUsernamePasswordAuthenticator implements UsernamePasswordAuthenticator {

    private final UserRepository userRepository;

    public MongoUsernamePasswordAuthenticator(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.findByUsernameAndPassword(username, password).subscribe(authSubscriber);

        try {
            authSubscriber.await();
        } catch (InterruptedException e) {
            this.throwsException("Error when susbcribing");
            e.printStackTrace();
        }

        if (userFromDb[0] == null) {
            throwsException("Username or password is wrong");
        } else {
            HttpProfile profile = new HttpProfile();
            profile.setId(username);
            profile.addAttribute("username", username);
            credentials.setUserProfile(profile);
        }
    }

    protected void throwsException(String message) {
        throw new CredentialsException(message);
    }
}
