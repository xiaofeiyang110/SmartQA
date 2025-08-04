package spring.ai.example.smart.qa.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import spring.ai.example.smart.qa.util.DocumentUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeBaseService {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseService.class);

    private final VectorStore vectorStore;
    private final ObservationRegistry observationRegistry;
    public KnowledgeBaseService(VectorStore vectorStore, ObservationRegistry observationRegistry){
        this.vectorStore = vectorStore;
        this.observationRegistry = observationRegistry;
    }


    @PostConstruct
    public void init() {
        Observation.createNotStarted("knowledge-base-init", observationRegistry)
                .observe(this::performInit);
    }

    private void performInit(){
        logger.info("KnowledgeBaseService init");
        //加载knowledge文件下面的所有文件
        try{
            List<Document> splitDocuments = DocumentUtil.getDocuments();

            Observation.createNotStarted("vectorStore-add", observationRegistry)
                    .lowCardinalityKeyValue("region", "us-east")
                    .highCardinalityKeyValue("requestId", UUID.randomUUID().toString())
                    .observe(() -> vectorStore.add(splitDocuments));
        }catch (Exception e){
            logger.error("KnowledgeBaseService init error", e);
        }
    }

}
