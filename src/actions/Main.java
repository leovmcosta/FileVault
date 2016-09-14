package actions;

import util.EncryptAESUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by labreu13 on 14-9-16.
 */
public class Main {
    public enum Actions {
        encrypt,
        decrypt
    }

    private static void usage() {
        System.out.println("Usage: FileVault");
        System.out.println("action=[encrypt/decrypt] path=<file path used for retrieving or storing file >");
    }
    public static void main(String[] args) {
        Actions action;

        try {
            action = Actions.valueOf(args[0]);
        } catch (NullPointerException e) {
            usage();
            return;
        } catch (IllegalArgumentException e) {
            usage();
            return;
        }

        if ((action == Actions.encrypt || action == Actions.decrypt) && args[1] == null) {
            usage();
            return;
        }

        if (action == Actions.encrypt) {
            EncryptAESUtil.encryptFile(args[1]);
        }

        if (action == Actions.decrypt) {
            try {
                EncryptAESUtil.decryptFile(args[1]);
            } catch (Exception e) {
                e.getMessage();
            }

        }
        return;
    }
}
