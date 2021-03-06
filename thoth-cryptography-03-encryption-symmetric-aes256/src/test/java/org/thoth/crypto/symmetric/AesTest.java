package org.thoth.crypto.symmetric;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.thoth.crypto.io.ByteArrayReader;
import org.thoth.crypto.io.ByteArrayWriter;

/**
 *
 * @author Michael Remijan mjremijan@yahoo.com @mjremijan
 */
public class AesTest {

    static Path secretKeyFile;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // Store the SecretKey bytes in the ./target diretory. Do
        // this so it will be ignore by source control.  We don't
        // want this file committed.
        secretKeyFile
            = Paths.get("./target/Aes256.key").toAbsolutePath();

        // Generate a SecretKey for the test
        SecretKey secretKey
            = new AesSecretKeyProducer().produce();

        // Store the byte[] of the SecretKey.  This is the
        // "private key file" you want to keep safe.
        ByteArrayWriter writer = new ByteArrayWriter(secretKeyFile);
        writer.write(secretKey.getEncoded());
    }


    @Test
    public void encrypt_and_decrypt_using_same_Aes256_instance() {
        // setup
        SecretKey secretKey
            = new AesSecretKeyProducer().produce(
                new ByteArrayReader(secretKeyFile).read()
            );

        Aes aes
            = new Aes(secretKey);

        String toEncrypt
            = "encrypt me";

        // run
        byte[] encryptedBytes
            = aes.encrypt(toEncrypt, Optional.empty());

        String decrypted
            = aes.decrypt(encryptedBytes, Optional.empty());

        // assert
        Assert.assertEquals(toEncrypt, decrypted);
    }


    public void encrypt_and_decrypt_with_aad_using_same_Aes256_instance() {
        // setup
        SecretKey secretKey
            = new AesSecretKeyProducer().produce(
                new ByteArrayReader(secretKeyFile).read()
            );

        Aes aes
            = new Aes(secretKey);

        String toEncrypt
            = "encrypt me aad";

        // run
        byte[] encryptedBytes
            = aes.encrypt(toEncrypt, Optional.of("JUnit AAD"));

        String decrypted
            = aes.decrypt(encryptedBytes, Optional.of("JUnit AAD"));

        // assert
        Assert.assertEquals(toEncrypt, decrypted);
    }


    @Test
    public void encrypt_and_decrypt_using_different_Aes256_instance()
    throws Exception {
        // setup
        SecretKey secretKey
            = new AesSecretKeyProducer().produce(
                new ByteArrayReader(secretKeyFile).read()
            );

        Aes aesForEncrypt
            = new Aes(secretKey);

        Aes aesForDecrypt
            = new Aes(secretKey);

        String toEncrypt
            = "encrypt me";

        // run
        byte[] encryptedBytes
            = aesForEncrypt.encrypt(toEncrypt, Optional.empty());

        ByteArrayOutputStream baos
            = new ByteArrayOutputStream();
        baos.write(encryptedBytes);

        String decrypted
            = aesForDecrypt.decrypt(baos.toByteArray(), Optional.empty());

        // assert
        Assert.assertEquals(toEncrypt, decrypted);
    }
}
