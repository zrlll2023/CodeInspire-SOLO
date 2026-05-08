package com.codeinspire.privacy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PrivacyServiceImpl implements PrivacyService {

    private static final Map<Pattern, String> PII_PATTERNS = new LinkedHashMap<>();

    static {
        PII_PATTERNS.put(Pattern.compile("\\b(1[3-9]\\d{9})\\b"), "[手机号]");
        PII_PATTERNS.put(Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"), "[邮箱]");
        PII_PATTERNS.put(Pattern.compile("\\d{17}[\\dXx]"), "[身份证号]");
        PII_PATTERNS.put(Pattern.compile("(?:北京市|上海市|天津市|重庆市|河北省|山西省|辽宁省|吉林省|黑龙江省|江苏省|浙江省|安徽省|福建省|江西省|山东省|河南省|湖北省|湖南省|广东省|海南省|四川省|贵州省|云南省|陕西省|甘肃省|青海省|台湾省|内蒙古自治区|广西壮族自治区|西藏自治区|宁夏回族自治区|新疆维吾尔自治区|香港特别行政区|澳门特别行政区)[^，。！？]*?(?:市|区|县|镇|乡|街道|路|号|村)"), "[地址]");
    }

    @Override
    public MaskedResult mask(String content) {
        if (content == null || content.isEmpty()) {
            return new MaskedResult();
        }

        String maskedContent = content;
        Map<String, String> piiMapping = new LinkedHashMap<>();
        int counter = 0;

        for (Map.Entry<Pattern, String> entry : PII_PATTERNS.entrySet()) {
            Pattern pattern = entry.getKey();
            String placeholder = entry.getValue();
            Matcher matcher = pattern.matcher(maskedContent);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String originalValue = matcher.group(1);
                if (originalValue == null) {
                    originalValue = matcher.group(0);
                }
                
                String replacementKey = placeholder + "_" + counter++;
                piiMapping.put(replacementKey, originalValue);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacementKey));
            }
            matcher.appendTail(sb);
            maskedContent = sb.toString();
        }

        log.debug("Privacy masking completed. Found {} PII items", piiMapping.size());

        MaskedResult result = new MaskedResult();
        result.setMaskedContent(maskedContent);
        result.setPiiMapping(piiMapping);
        return result;
    }

    @Override
    public String unmask(String maskedContent, Map<String, String> mapping) {
        if (maskedContent == null || mapping == null || mapping.isEmpty()) {
            return maskedContent;
        }

        String result = maskedContent;
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
