package spring.ai.example.smart.qa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentUtil {
    private static Logger logger = LoggerFactory.getLogger(DocumentUtil.class);

    public static List<Document> getDocuments() {
        Path knowledgePaths = Paths.get("src/main/resources/knowledge");
        List<DocumentReader> documentReaders = List.of(
                new TextReader(new FileSystemResource(knowledgePaths.resolve("product_info.txt"))),
                new TextReader(new FileSystemResource(knowledgePaths.resolve("warranty_policy.txt")))
        );

        List<Document> documents = documentReaders.stream()
                .flatMap(reader -> reader.read().stream())
                .toList();
        documents.forEach(doc -> logger.debug("Loaded Document:{}",doc.getMetadata()));

        //将文件进行分段
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.split(documents);
        logger.info("split documents into {} chunks", splitDocuments.size());
        splitDocuments.forEach(chunk -> logger.debug("Chunk ({} chars):{}",chunk.getText().length(),chunk.getMetadata()));
        return splitDocuments;
    }
}
