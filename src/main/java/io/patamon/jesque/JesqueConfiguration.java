package io.patamon.jesque;

import io.patamon.jesque.core.JobClientImpl;
import io.patamon.jesque.core.JobContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * Desc: Jesque Configuration
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/8
 */
@Configuration
@ConditionalOnProperty(value = "jesque.enable", havingValue = "true", matchIfMissing = true)
public class JesqueConfiguration {

    @Bean
    @ConditionalOnMissingBean(JobContext.class)
    public JobContext jobContext(ApplicationContext applicationContext, RedisProperties redisProperties, Pool<Jedis> jedisPool) {
        return new JobContext(applicationContext, redisProperties, jedisPool);
    }

    @Bean
    @ConditionalOnMissingBean(JobClient.class)
    public JobClient jobClient(JobContext jobContext) {
        return new JobClientImpl(jobContext);
    }

}
