package org.corella.springboot.servicesImplementation;

import org.corella.springboot.services.EmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {
    private final EmbeddingModel embeddingModel;
    private final SimpleVectorStore vectorStore;

    @Value("vectorstore.json")
    private String vectorStoreFileName;

    @Autowired
    public EmbeddingServiceImpl(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    @Override
    public SimpleVectorStore getVectorStore() {
        return vectorStore;
    }

    public void embedAndStore(Resource resource) {
        File vectorStoreFile = getVectroStoreFile();
        if (vectorStoreFile.exists())
            if(!vectorStoreFile.delete())
                throw new RuntimeException("Error borrando vector store file");
        try {
            if(!vectorStoreFile.createNewFile())
                throw new RuntimeException("Error creando vector store file");
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.split(tikaDocumentReader.get());
            vectorStore.add(splitDocuments);
            vectorStore.save(vectorStoreFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating vector store file");
        }
    }

    private File getVectroStoreFile() {
        Path path = Paths.get("src", "main", "resources", "data");
        String absolutePath = path.toFile().getAbsoluteFile() + "\\" + vectorStoreFileName;
        return new File(absolutePath);
    }

}
