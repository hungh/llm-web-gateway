package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OnnxModelConfig {
    
    private final AppConfig config;
    
    @Autowired
    public OnnxModelConfig(AppConfig config) {
        this.config = config;
    }
    
    public String getModel() {
        return config.getModelPath();
    }
    
    public String getVocab() {
        return config.getVocabPath();
    }
    
    public String getTokenizer() {
        return config.getTokenizerPath();
    }

    public double getThreshold() {
        return config.getThreshold();
    }
}
