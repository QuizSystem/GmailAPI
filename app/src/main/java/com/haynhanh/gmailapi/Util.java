package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 1/29/17.
 */
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

public class Util {
    public static String base64UrlDecode(String input) {
        if(input != null && !input.isEmpty()) {
            String result = null;
            Base64 decoder = new Base64(true);
            byte[] decodedBytes = decoder.decode(input);
            result = new String(decodedBytes);
            return result;
        }
        return null;
    }
}
