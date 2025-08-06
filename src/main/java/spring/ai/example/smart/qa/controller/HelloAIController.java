package spring.ai.example.smart.qa.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.smart.qa.client.AlibabaDashScopeChatModel;
import spring.ai.example.smart.qa.service.AlibabaAIService;

import java.util.List;

@RestController
public class HelloAIController {

    private final ZhiPuAiChatModel chatModel;

    private final VectorStore vectorStore;

    private final ZhiPuAiEmbeddingModel embeddingModel;

    @Resource
    private AlibabaAIService alibabaAIService;

    public HelloAIController(ZhiPuAiChatModel chatModel,VectorStore vectorStore,ZhiPuAiEmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @Resource
    private AlibabaDashScopeChatModel alibabaChatModel;

    //helloAI
    @GetMapping("/api/chat")
    public String hello(@Parameter String str) {
        String call = chatModel.call(str);
        return call;
    }


    //helloAI
    @GetMapping("/alibaba/api/chat")
    public String alibabaApiChat(@Parameter String str) {
        String call = alibabaChatModel.call(str);
        return call;
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String query) {
//        float[] embed = embeddingModel.embed(query);
        return vectorStore.similaritySearch(query);
    }

    @GetMapping("/alibaba/query")
    public String alibabaRag(@RequestParam String query){
        return alibabaAIService.generateResponse(query);
    }
}
