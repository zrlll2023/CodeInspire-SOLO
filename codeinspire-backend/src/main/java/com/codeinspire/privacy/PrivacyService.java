package com.codeinspire.privacy;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PrivacyService {
    MaskedResult mask(String content);
    String unmask(String maskedContent, Map<String, String> mapping);
}

@Data
class MaskedResult {
    private String maskedContent;
    private Map<String, String> piiMapping;
}
