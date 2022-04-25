package org.example.rest.utils;

import org.example.rest.entities.storage.Actor;
import org.example.rest.persistence.ActorHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Date;

public class JToken {

    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final int SECINDAY = 86400 ;

    public static String geneateToken (String username, String role){

        Long expDate = new Date(System.currentTimeMillis()).getTime() / 1000 + SECINDAY;

        JSONObject payload = new JSONObject();
        payload.put("usr", username);
        payload.put("rol",role);
        payload.put("exp", expDate);

        String encodedHeader = encode(JWT_HEADER.getBytes());
        String encodedPayload = encode(payload.toJSONString().getBytes());
        String signature = hmacSha256(encodedHeader + "." + encodedPayload);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public static String getUsrByToken (String token) throws ParseException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return null;
        JSONParser parser = new JSONParser();
        String decPayload = decode(parts[1]);
        JSONObject payload = (JSONObject) parser.parse(decPayload);
        return payload.get("usr").toString();
    }

    public static String getRoleByToken (String token) throws ParseException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return null;
        JSONParser parser = new JSONParser();
        JSONObject payload = (JSONObject) parser.parse(decode(parts[1]));
        return payload.get("rol").toString();
    }


    public static boolean verifyToken (String token) throws ParseException {

        String[] parts = token.split("\\.");

        if (parts.length != 3) return false;

        String signature = parts[2];

        JSONParser parser = new JSONParser();
        JSONObject payload = (JSONObject) parser.parse(decode(parts[1]));

        String username = payload.get("usr").toString();
        Long expDate = (Long) payload.get("exp");

        if (ActorHelper.getById(Actor.class,username) != null &&
                signature.equals(hmacSha256(parts[0] + "." + parts[1])) &&
                expDate > (System.currentTimeMillis() / 1000) )
            return true;
        else return false;

    }

    private static String hmacSha256(String data) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("./WEB-INF/keys.json"));
            String secret = ((JSONObject) obj).get("prik").toString();

            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return encode(signedBytes);
        } catch (Exception e) {

            return null;
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

}
