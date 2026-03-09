# LLM Web Gateway

SpringBoot Web Server for Agentic Flows

## Overview

This web server acts as a gateway that receives user prompts and routes them to Python LLM servers for inference.

## Prerequisites

- **Java 17** (JDK)
- **Gradle** for building
- **Python LLM servers** running on configured endpoints

## Quick Start

1. **Clone and build**:
   ```bash
   git clone <repository-url>
   cd llm-web-gateway
   ./gradlew build
   ./gradlew bootRun
   ```

2. **Download ONNX Model (for prompt injection detection)**:
   ```bash
   mkdir -p models/fmops-distilbert-prompt-injection-onnx
   curl -L -o models/fmops-distilbert-prompt-injection-onnx/model.onnx https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx/resolve/main/model.onnx
   curl -L -o models/fmops-distilbert-prompt-injection-onnx/vocab.txt https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx/raw/main/vocab.txt
   curl -L -o models/fmops-distilbert-prompt-injection-onnx/tokenizer.json https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx/resolve/main/tokenizer.json
   curl -L -o models/fmops-distilbert-prompt-injection-onnx/tokenizer_config.json https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx/raw/main/tokenizer_config.json
   curl -L -o models/fmops-distilbert-prompt-injection-onnx/special_tokens_map.json https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx/raw/main/special_tokens_map.json
   ```

3. **Server runs on**: `http://localhost:8080`

## Python LLM Server Interface

Python LLM server should implement this FastAPI interface:

```python
from fastapi import FastAPI
from pydantic import BaseModel
from my_agentic_llm import run_agent

app = FastAPI()

class Prompt(BaseModel):
    text: str

@app.post("/infer")
async def infer(prompt: Prompt):
    result = await run_agent(prompt.text)
    return {"response": result}
```

## Communication Protocol

- **Method**: HTTPS POST
- **Content-Type**: application/json
- **Body**: `{"text": "your prompt here"}`

## Security Features

The gateway includes **prompt injection detection** using:

- **ONNX Runtime** with DistilBERT model
- **Model**: [protectai/fmops-distilbert-prompt-injection-onnx](https://huggingface.co/protectai/fmops-distilbert-prompt-injection-onnx)
- **Fallback**: Heuristic-based classification when model unavailable
- **Threshold**: 0.5 (configurable)

### Usage Example
```java
@Autowired
private OnnxClassifier classifier;

public boolean checkPrompt(String prompt) {
    return classifier.isPromptInjection(prompt);
}
```


## API Endpoints

- `POST /api/prompt` - Submit prompt for inference
- `GET /actuator/health` - Health check

## Development

- **Build**: `./gradlew build`
- **Run**: `./gradlew bootRun`
- **Test**: `./gradlew test`
