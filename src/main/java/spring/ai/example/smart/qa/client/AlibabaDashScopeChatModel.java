package spring.ai.example.smart.qa.client;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AlibabaDashScopeChatModel implements ChatModel {

    @Value("${spring.ai.alibaba.dashscope.api-key}")
    private String apiKey;

    @Value("${spring.ai.alibaba.dashscope.chat.model:qwen3-235b-a22b}")
    private String modelName;


    @Override
    public ChatResponse call(Prompt prompt) {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(prompt.getContents())
                .build();

        // 构建参数
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model(modelName)
                .messages(Arrays.asList(systemMsg,userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .enableThinking(false)
                .build();

        // 调用API
        try {
            GenerationResult call = gen.call(param);
            String resContent = call.getOutput().getChoices().get(0).getMessage().getContent();
            org.springframework.ai.chat.model.Generation aiGeneration =
                    new org.springframework.ai.chat.model.Generation(new AssistantMessage(resContent));

            List<org.springframework.ai.chat.model.Generation> generations = new ArrayList<>();
            generations.add(aiGeneration);

            return new ChatResponse(generations);
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return ChatModel.super.stream(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return ChatModel.super.getDefaultOptions();
    }


    @Override
    public String call(String message) {
        return ChatModel.super.call(message);
    }
}
