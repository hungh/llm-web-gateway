package com.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.config.YamlPropertySourceFactory;


@Configuration
@ConfigurationProperties(prefix = "onnx.model")
@PropertySource(value = "file:config.yaml", factory = YamlPropertySourceFactory.class)
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    
    private String path;
    private String vocab;
    private String tokenizer;
    private double threshold;
    
    // Getters
    public String getModelPath() { return path; }
    public String getVocabPath() { return vocab; }
    public String getTokenizerPath() { return tokenizer; }
    public double getThreshold() { return threshold; }
    
    // Setters (required by @ConfigurationProperties)
    public void setPath(String path) { this.path = path; }
    public void setVocab(String vocab) { this.vocab = vocab; }
    public void setTokenizer(String tokenizer) { this.tokenizer = tokenizer; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    
    @PostConstruct
    public void init() {
        logger.info("ONNX Model Configuration loaded:");
        logger.info("  Model: {}", path);
        logger.info("  Vocab: {}", vocab);
        logger.info("  Tokenizer: {}", tokenizer);
        logger.info("  Threshold: {}", threshold);
    }
}
