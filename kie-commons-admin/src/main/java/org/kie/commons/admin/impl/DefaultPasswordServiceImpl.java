package org.kie.commons.admin.impl;

import javax.enterprise.context.ApplicationScoped;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.kie.commons.admin.PasswordService;

@ApplicationScoped
public class DefaultPasswordServiceImpl implements PasswordService {

    private static final String SECURE_STRING = System.getProperty("org.kie.secure.key", "org.kie.admin");
    private static final String SECURE_ALGORITHM = System.getProperty("org.kie.secure.alg", "PBEWithMD5AndTripleDES");

    @Override
    public String encrypt(String plainText) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECURE_STRING);
        encryptor.setAlgorithm(SECURE_ALGORITHM);
        return encryptor.encrypt(plainText);
    }

    @Override
    public String decrypt(String encryptedText) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECURE_STRING);
        encryptor.setAlgorithm(SECURE_ALGORITHM);
        return encryptor.decrypt(encryptedText);
    }
}
