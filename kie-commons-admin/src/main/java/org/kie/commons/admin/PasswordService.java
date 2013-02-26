package org.kie.commons.admin;

public interface PasswordService {

    String encrypt(String plainText);

    String decrypt(String encryptedText);
}
