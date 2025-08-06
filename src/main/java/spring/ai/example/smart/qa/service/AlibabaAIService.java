package spring.ai.example.smart.qa.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import spring.ai.example.smart.qa.util.DocumentUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlibabaAIService {
    Logger logger = LoggerFactory.getLogger(AlibabaAIService.class);
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    public AlibabaAIService(ChatClient.Builder chatClientBuilder, @Qualifier("redis-vector") VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void initializeVectorStore(){
        vectorStore.add(DocumentUtil.getDocuments());
        logger.info("Documents indexed into Vector Store.");
    }

    public String generateResponse(String userQuery) {
        // 1. 检索阶段：从向量存储中检索相关文档
        logger.info("Searching for relevant documents for query: " + userQuery);
        List<Document> relevantDocuments = vectorStore.similaritySearch(SearchRequest.builder().query(userQuery).topK(2).build()); // 获取最相关的2个文档
        for (int i = 0; i< relevantDocuments.size(); i++) {
            logger.info("Found  document ,index" +i+",doc:"+ relevantDocuments.get(i).getText());
        }


        // 2. 增强阶段：将检索到的文档作为上下文添加到提示中
        String documentContent = relevantDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 创建PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = Map.of(
                "input", userQuery,
                "documents", documentContent
        );
        Prompt prompt = promptTemplate.create(promptParameters);

        System.out.println("Sending prompt to LLM...");
        // 3. 生成阶段：使用 DashScope 模型生成回答
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        if (chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
            return chatResponse.getResult().getOutput().getText();
        } else {
            logger.warn("Received empty or invalid response from AI model.");
            return "抱歉，暂时无法生成回答。";
        }
    }
}
