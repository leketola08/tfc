package org.corella.springboot.services;

import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.Resource;

public interface EmbeddingService {

    void embedAndStore(Resource resource);

    SimpleVectorStore getVectorStore();
}
