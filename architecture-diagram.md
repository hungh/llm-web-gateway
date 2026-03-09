## Architecture

```mermaid
flowchart TD
    %% User Layer
    User[👤 User Request] --> Gateway[🔐 LLM Web Gateway]

    %% Security & Normalization
    Gateway --> Security[🛡️ Security Checks<br/>- Prompt Injection Detection<br/>- Harmful Content Filter]
    Security --> Normalize[📝 Prompt Normalization<br/>- Cleanup & Standardization]

    %% Exact Cache Layer
    Normalize --> CacheCheck{🗄️ Redis Cache Check}
    CacheCheck -->|Hit| CachedResponse[📋 Cached Response]

    %% Miss → Orchestration
    CacheCheck -->|Miss| Orchestrator[🧩 Orchestrator / Router<br/>- RAG? Tools? Direct LLM?]

    %% RAG Branch
    Orchestrator -->|Needs RAG| EmbedQuery[🧠 Embed Query]
    EmbedQuery --> Qdrant[🔍 Qdrant Vector Search]
    Qdrant --> RetrievedDocs[📚 Retrieved Context]
    RetrievedDocs --> LLMRequest

    %% Direct LLM Branch
    Orchestrator -->|Direct LLM| LLMRequest[🚀 LLM Request]

    %% LLM Processing
    LLMRequest --> LLMServer[🤖 LLM Inference Servers]
    LLMServer --> RawResponse[💬 Raw LLM Response]

    %% Safety Before Caching
    RawResponse --> SafetyFinal[🛡️ Final Safety Check]

    %% Cache Storage
    SafetyFinal --> StoreCache[💾 Store in Redis Cache]

    %% Final Response
    CachedResponse --> FinalResponse[✅ Response to User]
    SafetyFinal --> FinalResponse

    %% Styling
    classDef user fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef gateway fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef security fill:#ffebee,stroke:#b71c1c,stroke-width:2px
    classDef cache fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef vector fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef llm fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    classDef response fill:#f1f8e9,stroke:#33691e,stroke-width:2px
    classDef orchestrator fill:#ede7f6,stroke:#4527a0,stroke-width:2px

    class User user
    class Gateway gateway
    class Security,SecurityFinal security
    class CacheCheck,StoreCache,CachedResponse cache
    class EmbedQuery,Qdrant,RetrievedDocs vector
    class LLMServer llm
    class RawResponse,FinalResponse response
    class Orchestrator orchestrator
```
### Architecture Overview
<img src="images/main_diagram.png" alt="LLM Web Gateway Architecture" width="800">

### Data Flow

1. **User Request** → Security validation → Prompt normalization
2. **Cache Check** → Redis for exact matches of normalized prompt (as key)
3. **Cache Miss** → Orchestrator decides: RAG or Direct LLM
4. **RAG Path** → Embedding generation → Qdrant semantic search → LLM with context
5. **Direct LLM Path** → LLM servers
6. **New Prompts** → LLM servers → Response caching
7. **Final Security Check** → Response delivery

### Key Components

- **🔐 Security**: Multi-layer security validation (prompt injection, harmful content)
- **🗄️ Redis**: Fast key-value cache for exact prompt matches
- **🔍 Qdrant**: Vector database for semantic similarity search
- **🧠 Embeddings**: Text-to-vector conversion for semantic matching
- **🤖 LLM Servers**: Actual inference endpoints
