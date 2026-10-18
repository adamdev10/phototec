package com.projects.phototec.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGenerator {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair keyPair = generator.generateKeyPair();

        var privateKey = keyPair.getPrivate().getEncoded();
        var publicKey = keyPair.getPublic().getEncoded();

        var pemPrivateKey = toPem("PRIVATE KEY", privateKey);
        var pemPublicKey = toPem("PUBLIC KEY", publicKey);

        Path keysDirectory = Path.of("keys");
        Files.createDirectories(keysDirectory);

        Files.writeString(
                keysDirectory.resolve("private.pem"),
                pemPrivateKey
        );

        Files.writeString(
                keysDirectory.resolve("public.pem"),
                pemPublicKey
        );

        System.out.println("RSA keys generated.");
    }

    private static String toPem(String type, byte[] key) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(key);

        return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----\n";
    }
}