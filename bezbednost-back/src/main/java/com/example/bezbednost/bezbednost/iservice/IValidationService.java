package com.example.bezbednost.bezbednost.iservice;

public interface IValidationService {

    boolean isValid(String regex, String data);

    boolean containsDangerousCharacters(String data);
}
