package pl.ark.chr.timelyzer.auth;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordService {

    private int strength;

    public BCryptPasswordService(int strength) {
        this.strength = strength;
    }

    public String encryptPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(strength));
    }

    public boolean passwordsMatch(String plainTextPassword, String encrypted) {
        return BCrypt.checkpw(plainTextPassword, encrypted);
    }
}
