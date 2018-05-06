package io.patamon.jesque;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import io.patamon.jesque.core.JobClientImpl;
import io.patamon.jesque.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

import java.util.Set;

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

    @Autowired
    private RedisProperties properties;

    @Bean
    @ConditionalOnMissingBean(JobContext.class)
    public JobContext jobContext(ApplicationContext applicationContext, Pool<Jedis> jedisPool) {
        return new JobContext(applicationContext, properties, jedisPool);
    }

    @Bean
    @ConditionalOnMissingBean(JobClient.class)
    public JobClient jobClient(JobContext jobContext) {
        return new JobClientImpl(jobContext);
    }

    @Bean
    @ConditionalOnMissingBean(JedisPoolConfig.class)
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(5);
        config.setMaxIdle(0);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(true);
        return config;
    }

    @Bean
    @ConditionalOnMissingBean(value = Pool.class, name = "jedisPool")
    public Pool<Jedis> jedisPool(JedisPoolConfig poolConfig) {
        // Sentinel model
        if (properties.getSentinel() != null) {
            RedisProperties.Sentinel sentinel = properties.getSentinel();
            String sentinelProps = sentinel.getNodes();
            Iterable<String> parts = Splitter.on(',').trimResults().omitEmptyStrings().split(sentinelProps);

            final Set<String> sentinelHosts = Sets.newHashSet(parts);
            String masterName = sentinel.getMaster();
            if (StringUtils.isEmpty(properties.getPassword())) {
                return new JedisSentinelPool(masterName, sentinelHosts, poolConfig);
            } else {
                return new JedisSentinelPool(masterName, sentinelHosts, poolConfig,
                        Protocol.DEFAULT_TIMEOUT, properties.getPassword());
            }
        }
        if (StringUtils.isEmpty(properties.getPassword())) {
            return new JedisPool(poolConfig, properties.getHost(), properties.getPort());
        }
        return new JedisPool(poolConfig, properties.getHost(), properties.getPort(),
                Protocol.DEFAULT_TIMEOUT, properties.getPassword());
    }
}
