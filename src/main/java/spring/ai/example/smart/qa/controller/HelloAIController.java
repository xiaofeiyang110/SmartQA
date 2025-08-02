package spring.ai.example.smart.qa.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.smart.qa.client.AlibabaDashScopeChatModel;

@RestController
public class HelloAIController {

    private final ZhiPuAiChatModel chatModel;

    public HelloAIController(ZhiPuAiChatModel chatModel) {
        this.chatModel = chatModel;
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
}
