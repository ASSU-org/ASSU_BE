package com.assu.server.domain.auth.crypto;

public interface SchoolCredentialEncryptor {
    String encrypt(String plain);   // -> Base64(iv+ciphertext)
    String decrypt(String cipher);  // Base64(iv+ciphertext) -> plain
}
