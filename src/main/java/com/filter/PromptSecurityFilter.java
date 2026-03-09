package com.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.service.PromptInjectionClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class PromptSecurityFilter extends OncePerRequestFilter {
    
    @Autowired
    private PromptInjectionClassifier classifier;

    private static final Logger logger = LoggerFactory.getLogger(PromptSecurityFilter.class);
    
    private static final String BLOCK_MESSAGE = "{\"error\":\"Prompt injection detected\",\"status\":403}";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {            
            if (containsPromptInjection(request)) {
                logger.warn("Prompt injection detected in request: {}", request.getRequestURI());
                sendBlockResponse(response);
                return;
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Error in prompt security filter: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
    
    private boolean containsPromptInjection(HttpServletRequest request) {        
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            
            for (String paramValue : paramValues) {
                if (paramValue != null && classifier.isPromptInjection(paramValue)) {
                    logger.debug("Injection found in parameter '{}': {}", paramName, paramValue);
                    return true;
                }
            }
        }
        
        // TODO: Add request body checking for POST/PUT requests
        // TODO: Add header checking if needed
        
        return false;
    }
    
    private void sendBlockResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(BLOCK_MESSAGE);
        response.getWriter().flush();
    }
}
