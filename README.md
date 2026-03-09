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

2. **Server runs on**: `http://localhost:8080`

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


## API Endpoints

- `POST /api/prompt` - Submit prompt for inference
- `GET /actuator/health` - Health check

## Development

- **Build**: `./gradlew build`
- **Run**: `./gradlew bootRun`
- **Test**: `./gradlew test`
