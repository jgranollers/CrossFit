package com.example.crudthymeilif.Config;

import org.springframework.stereotype.Component;

@Component("flagHelper")
public class FlagHelper {

    /**
     * Converts an ISO 3166-1 alpha-2 country code to an emoji flag.
     * Example: "ES" -> "🇪🇸", "FR" -> "🇫🇷"
     */
    public String getFlag(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "";
        }
        String upper = countryCode.toUpperCase();
        // Regional Indicator Symbol Letters start at U+1F1E6 (for 'A')
        int base = 0x1F1E6;
        int cp1 = base + (upper.charAt(0) - 'A');
        int cp2 = base + (upper.charAt(1) - 'A');
        return new String(Character.toChars(cp1)) + new String(Character.toChars(cp2));
    }
}
