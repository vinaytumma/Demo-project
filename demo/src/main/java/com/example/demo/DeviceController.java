package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@RestController
public class DeviceController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KeyPair keyPair;

    public DeviceController() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    @PostMapping("/submit")
    public String submitDeviceData(@RequestBody DeviceData deviceData) throws Exception {
        // Convert device data to JSON
        String json = objectMapper.writeValueAsString(deviceData);

        // Return JSON content for verification
        return json;
    }

    @GetMapping("/download-signature")
    public String downloadSignature(@RequestParam String json) throws Exception {
        // Generate SHA-512/RSA signature
        Signature signature = Signature.getInstance("SHA512withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(json.getBytes(StandardCharsets.UTF_8));
        byte[] signedData = signature.sign();

        // Encode in Base64
        return Base64.getEncoder().encodeToString(signedData);
    }
}
