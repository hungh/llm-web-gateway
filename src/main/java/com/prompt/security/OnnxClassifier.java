package com.prompt.security;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.config.OnnxModelConfig;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.djl.huggingface.tokenizers.Encoding;
/**
 * Working ONNX Runtime classifier for prompt injection detection
 * Uses: https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx
 */
@Component
public class OnnxClassifier {
    
    private static final Logger logger = LoggerFactory.getLogger(OnnxClassifier.class);
    
    private final OrtEnvironment environment;
    private final OrtSession session;
    private final HuggingFaceTokenizer tokenizer;

    private final OnnxModelConfig config;   
    
    private boolean onnxAvailable = false;
    private boolean modelLoaded = false;
    
    
    @Autowired
    public OnnxClassifier(OnnxModelConfig config) throws Exception {
        this.config = config;
         logger.info("Initializing PromptInjectionClassifier with config from YAML");
        this.environment = OrtEnvironment.getEnvironment();
        this.session = environment.createSession(config.getModel(), new OrtSession.SessionOptions());
        
        // Load tokenizer from local file
        try {
            this.tokenizer = HuggingFaceTokenizer.newInstance(Paths.get(config.getTokenizer()));
            logger.info("PromptInjectionClassifier initialized with ONNX model: {}", config.getModel());
        } catch (Exception e) {
            logger.error("Failed to load tokenizer from local file: {}", config.getTokenizer());
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }
    
    @PostConstruct
    public void initialize() {
        try {           
            Class.forName("ai.onnxruntime.OrtEnvironment");
            onnxAvailable = true;
            logger.info("ONNX Runtime is available!");
            
            if (Files.exists(Paths.get(config.getModel())) && 
                Files.exists(Paths.get(config.getVocab())) && 
                Files.exists(Paths.get(config.getTokenizer()))) {
                
                modelLoaded = true;
                logger.info("ONNX model ready for inference!");
            } else {
                logger.warn("Model files not found. Using fallback classifier.");
                logger.info("Download files to: {}", config.getModel());
            }
            
        } catch (ClassNotFoundException e) {
            logger.warn("ONNX Runtime not available - using fallback classifier");
            onnxAvailable = false;
        } catch (Exception e) {
            logger.error("Error initializing ONNX classifier: {}", e.getMessage());
        }
    }
    
  
    public boolean isPromptInjection(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return false;
        }
        try {
            float score = classifyPrompt(prompt);
            return score > config.getThreshold();
        } catch (Exception e) {
            logger.error("Error classifying prompt: {}", e.getMessage());
            return false;
        }
    }
    
    public float classifyPrompt(String prompt) {
        if (onnxAvailable) {
            return classifyWithOnnx(prompt);
        }
        throw new IllegalStateException("ONNX classifier not available");
    }

    private float appleSoftmax(float[][] logits) {
       // 2 categories : benign and malicious
       float benignProb = logits[0][0];
       float maliciousProb = logits[0][1];
       float score = (float) Math.exp(maliciousProb) / (float) (Math.exp(benignProb) + Math.exp(maliciousProb));
       return score;
    }
    
    private float[][] onnxInference(String prompt) throws Exception {
        Encoding encoding = tokenizer.encode(prompt);

        // Create tensors with proper cleanup - convert 1D arrays to 2D (batch_size=1)
        OnnxTensor inputIdsTensor = null;
        OnnxTensor attentionMaskTensor = null;
        
        try {
            // Convert 1D arrays to 2D arrays with batch_size=1
            long[] inputIds1D = encoding.getIds();
            long[] attentionMask1D = encoding.getAttentionMask();
            
            long[][] inputIds2D = new long[1][inputIds1D.length];
            long[][] attentionMask2D = new long[1][attentionMask1D.length];
            
            System.arraycopy(inputIds1D, 0, inputIds2D[0], 0, inputIds1D.length);
            System.arraycopy(attentionMask1D, 0, attentionMask2D[0], 0, attentionMask1D.length);
            
            inputIdsTensor = OnnxTensor.createTensor(environment, inputIds2D);
            attentionMaskTensor = OnnxTensor.createTensor(environment, attentionMask2D);
            
            Map<String, OnnxTensor> inputs = Map.of(
                "input_ids", inputIdsTensor,
                "attention_mask", attentionMaskTensor
            );
            
            try (OrtSession.Result result = session.run(inputs)) {
                return (float[][])result.get(0).getValue();
            }
        } finally {
            // Close tensors to prevent memory leaks
            if (inputIdsTensor != null) {
                inputIdsTensor.close();
            }
            if (attentionMaskTensor != null) {
                attentionMaskTensor.close();
            }
        }
    }
    
    private float classifyWithOnnx(String prompt) {
        if (!modelLoaded) {
            logger.error("Model not loaded, using enhanced fallback");
            throw new IllegalStateException("Model not loaded. Not safe to proceed injection detection");
        }
        
        logger.info("Using ONNX model for inference");
        try {
            float[][] logits = onnxInference(prompt);
            float score = appleSoftmax(logits);
            return score;
        } catch (Exception e) {
            logger.error("ONNX inference failed: {}", e.getMessage());
            throw new IllegalStateException("ONNX inference failed", e);
        }
    }

    
    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            logger.error("Error closing ONNX session: {}", e.getMessage());
        }
        
        try {
            if (tokenizer != null) {
                tokenizer.close();
            }
        } catch (Exception e) {
            logger.error("Error closing tokenizer: {}", e.getMessage());
        }
        
        try {
            if (environment != null) {
                environment.close();
            }
        } catch (Exception e) {
            logger.error("Error closing ONNX environment: {}", e.getMessage());
        }
        
        logger.info("OnnxClassifier resources closed");
    }
}
