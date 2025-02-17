package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(18)
@Slf4j
public class Challenge18 extends Challenge {

    private final String hashPassword;
    private final String md5Hash = "MD5";
    private final String sha1Hash = "SHA1";

    public Challenge18(ScoreCard scoreCard, @Value("aHVudGVyMg==") String hashPassword) {
        super(scoreCard);
        this.hashPassword = hashPassword;
    }

    private String base64Decode(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes);
    }


    private String calculateHash(String hash, String input) {
        try {
            if (md5Hash.equals(hash) || sha1Hash.equals(hash)) {
                var md = MessageDigest.getInstance(hash);
                return new String(Hex.encode(md.digest(input.getBytes(StandardCharsets.UTF_8))));
            }
        } catch (NoSuchAlgorithmException e) {
            log.warn("Exception thrown when calculating hash", e);
        }
        return "No Hash Selected";
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(base64Decode(hashPassword));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return calculateHash(md5Hash, base64Decode(hashPassword)).equals(calculateHash(md5Hash, answer))
            || calculateHash(sha1Hash, base64Decode(hashPassword)).equals(calculateHash(sha1Hash, answer));
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 5;
    }

    @Override
    public String getTech() {
        return "Hashing";
    }
}
