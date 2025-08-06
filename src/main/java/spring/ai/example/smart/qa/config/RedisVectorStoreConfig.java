package spring.ai.example.smart.qa.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisVectorStoreConfig {


    @Bean
    public JedisPooled jedisPooled(RedisConnectionFactory redisConnectionFactory) {
        if (redisConnectionFactory instanceof JedisConnectionFactory jedisConnectionFactory) {
            return new JedisPooled(
                    jedisConnectionFactory.getHostName(),
                    jedisConnectionFactory.getPort()
            );
        }
        throw new IllegalStateException("RedisConnectionFactory must be instance of JedisConnectionFactory");
    }
    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(@Qualifier("zhiPuAiEmbeddingModel") EmbeddingModel zhiPuAiEmbeddingModel) {
        return zhiPuAiEmbeddingModel;
    }
    

    @Bean(name = "redis-vector")
    public VectorStore vectorStore(JedisPooled jedisPooled,  @Qualifier("zhiPuAiEmbeddingModel") ZhiPuAiEmbeddingModel embeddingModel) {
        // 创建 RedisVectorStore 实例
        // 第一个参数是 RedisTemplate，Spring Boot 会自动配置
        // 第二个参数是 EmbeddingModel，用于生成向量
        // 第三个参数是索引名称，可以自定义
        return RedisVectorStore.builder(jedisPooled,embeddingModel).prefix("spring-ai-index").initializeSchema(true).build();
    }
}
