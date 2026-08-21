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

        System.out.println("PRIVATE KEY: " + privateKey);
        System.out.println("PUBLIC KEY: " + publicKey);

        var pemPrivateKey = Base64.getEncoder().encodeToString(privateKey);
        var pemPublicKey = Base64.getEncoder().encodeToString(publicKey);

        System.out.println("Encoded PRIVATE KEY: " + pemPrivateKey);
        System.out.println("Encoded PUBLIC KEY: " + pemPublicKey);

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
}