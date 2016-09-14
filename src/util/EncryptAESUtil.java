package util;

import org.apache.commons.codec.binary.Base64OutputStream;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

/**
 * Created by labreu13 on 14-9-16.
 */
public final class EncryptAESUtil {
    public static final String AES = "AES";
    public static final String KEY = "aes.encryptionKey";
    private static final String ENCRYPTION_PROPERTIES = "encryption.properties";

    private static byte[] encryptionKey;

    private EncryptAESUtil() {
        // Utility...
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static boolean encryptFile(String fileName) {
        try {
            SecretKeySpec sks = new SecretKeySpec(EncryptAESUtil.getEncryptionKey(), EncryptAESUtil.AES);
            Cipher cipher = Cipher.getInstance(EncryptAESUtil.AES);
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());

            FileOutputStream ostream = new FileOutputStream(fileName);
            Base64OutputStream bstream = new Base64OutputStream(ostream);
            CipherOutputStream cos = new CipherOutputStream(bstream, cipher);

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cos));
            pw.close();
            return true;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException| InvalidKeyException | InvalidParameterException | InvalidAlgorithmParameterException | FileNotFoundException e) {
            System.out.println("Error when encrypting file: "+e.getMessage());
            return false;
        }
    }

    public static void decryptFile(String filename) throws Exception {
        FileInputStream fis = null;
        BufferedReader br = new BufferedReader(new FileReader(filename));

        File file = new File(filename + "-decrypted");
        file.createNewFile();
        Writer out = new OutputStreamWriter(new FileOutputStream(filename + "-decrypted"), "UTF-8");

        String line = null;
        try {
            while (( line = br.readLine()) != null){
                line = decrypt(Base64.getDecoder().decode(line));
                out.write(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (fis != null) {
                fis.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static String decrypt(byte[] line) {
        try {
            SecretKeySpec sks = new SecretKeySpec(EncryptAESUtil.getEncryptionKey(), EncryptAESUtil.AES);
            Cipher cipher = Cipher.getInstance(EncryptAESUtil.AES);
            cipher.init(Cipher.DECRYPT_MODE, sks);

            final byte[] plainText = cipher.doFinal(line);
            return new String(plainText, "UTF-8").trim();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException| InvalidKeyException | InvalidParameterException | IOException| BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error when decrypting file: "+e.getMessage());
            return null;
        }
    }

    private static byte[] getEncryptionKey() {
        synchronized (EncryptAESUtil.class) {
            if (encryptionKey == null) {
                try {
                    InputStream resource = EncryptAESUtil.class.getClassLoader().getResourceAsStream(ENCRYPTION_PROPERTIES);
                    Properties props = new Properties();
                    props.load(resource);
                    encryptionKey = hexStringToByteArray(props.getProperty(KEY));
                } catch (IOException e) {
                    System.out.println("Failed to read from encryption properties: "+e.getMessage());
                }
            }
        }
        return encryptionKey;
    }
}
