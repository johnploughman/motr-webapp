package uk.gov.dvsa.motr.web.encryption;

/**
 * Generic interface for decryption.
 */
public interface Decryptor {

    /**
     * Decrypts ciphertext
     *
     * @param value base64-encoded cipherblob
     * @return decryped plain text
     */
    String decrypt(String value);
}
