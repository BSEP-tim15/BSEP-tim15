package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IValidationService;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidationService implements IValidationService {

    @Override
    public boolean isValid(String regex, String data) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    @Override
    public boolean containsDangerousCharacters(String data) {
        return data.contains("'") || data.contains("\"") || data.contains(";") || data.contains("--") || data.contains("=") ||
                data.contains("xp_") || data.contains("/*") || data.contains("*/") || data.contains("\\");
    }
}
