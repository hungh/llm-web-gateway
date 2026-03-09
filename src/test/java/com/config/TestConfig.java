package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.prompt.security.MockOnnxClassifier;

@Configuration
public class TestConfig {
    
    @Bean
    public AppConfig appConfig() {
        AppConfig config = new AppConfig();
        // Set values manually for testing
        config.setPath("models/fmops-distilbert-prompt-injection-onnx/model.onnx");
        config.setVocab("models/fmops-distilbert-prompt-injection-onnx/vocab.txt");
        config.setTokenizer("models/fmops-distilbert-prompt-injection-onnx/tokenizer.json");
        config.setThreshold(0.5);
        return config;
    }
    
    @Bean
    public OnnxModelConfig onnxModelConfig(AppConfig appConfig) {
        return new OnnxModelConfig(appConfig);
    }
    
    @Bean
    public MockOnnxClassifier mockOnnxClassifier() {
        return new MockOnnxClassifier();
    }
}
