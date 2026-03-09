package com.prompt.processing;

import java.text.Normalizer;

/**
 * Normalization class for prompt normalization
 */
public class Normalization {
    public String normalize(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        // Normalize Unicode characters and remove diacritics if needed
        prompt = Normalizer.normalize(prompt, Normalizer.Form.NFC);

        prompt = prompt.trim().toLowerCase();

        // remove new line  
        prompt = prompt.replace("\n", " ");
        // remove multiple spaces
        prompt = prompt.replaceAll("\\s+", " ");
        
        // normalize punctuation
        prompt = prompt.replaceAll("[!]+", "!");
        prompt = prompt.replaceAll("[?]+", "?");
        
        // normalize punctation spacing
        prompt = prompt.replaceAll("\\s+([.!?])", "$1");
        
        // remove trailing punctuation
        prompt = prompt.replaceAll("[.!?]+$", "");
        
        
        return prompt;
    }
}
