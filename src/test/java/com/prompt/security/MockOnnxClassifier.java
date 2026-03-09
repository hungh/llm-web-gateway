package com.prompt.security;

/**
 * Mock implementation of OnnxClassifier for testing
 * This bypasses the actual ONNX dependencies and provides predictable behavior
 */
public class MockOnnxClassifier {
    
    private final double threshold;
    
    public MockOnnxClassifier() {
        this.threshold = 0.5;
    }
    
    public boolean isPromptInjection(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return false;
        }
        // Simple heuristic for testing - expanded to cover more patterns
        String lowerPrompt = prompt.toLowerCase();
        return lowerPrompt.contains("ignore") || 
               lowerPrompt.contains("bypass") || 
               lowerPrompt.contains("hack") ||
               lowerPrompt.contains("system") ||
               lowerPrompt.contains("admin") ||
               lowerPrompt.contains("dan") ||
               lowerPrompt.contains("override") ||
               lowerPrompt.contains("escalate") ||
               lowerPrompt.contains("privilege") ||
               lowerPrompt.contains("disregard") ||
               lowerPrompt.contains("decode") ||
               lowerPrompt.contains("encode");
    }
    
    public float classifyPrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalStateException("Empty prompt");
        }
        
        String lowerPrompt = prompt.toLowerCase();
        float baseScore = 0.1f;
        
        // Expanded scoring for more comprehensive detection
        if (lowerPrompt.contains("ignore")) baseScore += 0.3f;
        if (lowerPrompt.contains("bypass")) baseScore += 0.3f;
        if (lowerPrompt.contains("hack")) baseScore += 0.3f;
        if (lowerPrompt.contains("system")) baseScore += 0.2f;
        if (lowerPrompt.contains("admin")) baseScore += 0.2f;
        if (lowerPrompt.contains("dan")) baseScore += 0.25f;
        if (lowerPrompt.contains("override")) baseScore += 0.25f;
        if (lowerPrompt.contains("escalate")) baseScore += 0.2f;
        if (lowerPrompt.contains("privilege")) baseScore += 0.2f;
        if (lowerPrompt.contains("disregard")) baseScore += 0.2f;
        if (lowerPrompt.contains("decode")) baseScore += 0.15f;
        if (lowerPrompt.contains("encode")) baseScore += 0.15f;
        
        return Math.min(baseScore, 1.0f);
    }
    
    public void cleanup() {
        // Mock cleanup - no resources to clean
    }
}
