package com.assu.server.domain.auth.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AesGcmSchoolCredentialEncryptor implements SchoolCredentialEncryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;   // 16 bytes tag
    private static final int IV_BYTES = 12;        // 96-bit IV (권장)
    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public AesGcmSchoolCredentialEncryptor(byte[] keyBytes) {
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public String encrypt(String plain) {
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt school credential", e);
        }
    }

    @Override
    public String decrypt(String cipherB64) {
        try {
            byte[] all = Base64.getDecoder().decode(cipherB64);
            byte[] iv = Arrays.copyOfRange(all, 0, IV_BYTES);
            byte[] ct = Arrays.copyOfRange(all, IV_BYTES, all.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt school credential", e);
        }
    }
}
