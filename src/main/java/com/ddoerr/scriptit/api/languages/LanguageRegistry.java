package com.ddoerr.scriptit.api.languages;

public interface LanguageRegistry {
    void registerLanguage(String name, LanguageImplementation languageImplementation);
}