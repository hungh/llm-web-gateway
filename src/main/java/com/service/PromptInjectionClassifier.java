package com.service;

import org.springframework.stereotype.Service;
import com.prompt.security.OnnxClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PromptInjectionClassifier {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptInjectionClassifier.class);
    private final OnnxClassifier classifier;
    
    @Autowired
    public PromptInjectionClassifier(OnnxClassifier classifier) {
        this.classifier = classifier;
        logger.info("PromptInjectionClassifier initialized with ONNX classifier");
    }

    public boolean isPromptInjection(String prompt) {
        return classifier.isPromptInjection(prompt);
    }
    
    public float classifyPrompt(String prompt) {
        return classifier.classifyPrompt(prompt);
    }
    
    @PostConstruct
    public void init() {
        logger.info("PromptInjectionClassifier loaded successfully!");
    }
}
