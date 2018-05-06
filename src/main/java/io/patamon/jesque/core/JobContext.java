package io.patamon.jesque.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.patamon.jesque.annotation.JobConsumer;
import io.patamon.jesque.annotation.JobType;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.client.Client;
import net.greghaines.jesque.client.ClientPoolImpl;
import net.greghaines.jesque.utils.JesqueUtils;
import net.greghaines.jesque.worker.MapBasedJobFactory;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerPoolImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Desc: [延时]队列任务Context类, 主要进行初始化相关的动作
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/7
 */
public class JobContext {
    private static Logger log = LoggerFactory.getLogger(JobContext.class);

    private final ApplicationContext applicationContext;
    // 提交任务客户端
    private Client client;
    // 任务消费者
    private Worker worker;

    /**
     * 任务执行类的名称
     * @see JobAction
     */
    private static String ACTION_MAME = "JobAction";

    /**
     * 存储需要执行的方法
     * key: jobType的类型 {@link JobType}
     * value: 存储需要执行的类和方法
     */
    static Map<String, List<ConsumeMethod>> METHODS = new ConcurrentHashMap<>();
    static ExecutorService executorService;

    public JobContext(ApplicationContext applicationContext, RedisProperties redisProperties, Pool<Jedis> jedisPool) {
        this.applicationContext = applicationContext;
        executorService = new ThreadPoolExecutor(2, 4, 60L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10000),
                new ThreadFactoryBuilder().setNameFormat("Jesque-Job-%d").build(),
                (r, executor) -> log.error("Jesque job {} is rejected", r)
        );
        Config config = getConfig(redisProperties);
        // 初始化 job provider
        this.client = new ClientPoolImpl(config, jedisPool);
        // 初始化 job consumer
        initJobConsumer(config, jedisPool);
    }

    /**
     * 初始化Job consumer
     */
    private void initJobConsumer(Config config, Pool<Jedis> jedisPool) {
        // 获取所有的job consumer类
        Map<String, Object> consumers = applicationContext.getBeansWithAnnotation(JobConsumer.class);
        consumers.forEach((key, consumer) -> {
            JobConsumer jobConsumer = applicationContext.findAnnotationOnBean(key, JobConsumer.class);
            if (jobConsumer == null) {
                return;
            }
            // 获取所有的public方法
            for(Method method : consumer.getClass().getMethods()) {
                JobType jobType = method.getAnnotation(JobType.class);
                if (jobType != null) {
                    List<ConsumeMethod> methods = METHODS.computeIfAbsent(jobType.value(), k -> Lists.newArrayList());
                    methods.add(new ConsumeMethod(consumer, method, jobType.value()));
                }
            }
        });
        // 注册给jesque, 并启动worker
        this.worker = new WorkerPoolImpl(config,
                METHODS.keySet(),
                new MapBasedJobFactory(JesqueUtils.map(JesqueUtils.entry(ACTION_MAME, JobAction.class))),
                jedisPool
        );
        new Thread(worker).start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * 获取redis相关配置信息
     */
    private Config getConfig(RedisProperties redisProperties) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        if (!StringUtils.isEmpty(redisProperties.getDatabase())) {
            configBuilder.withDatabase(redisProperties.getDatabase());
        }
        if (!StringUtils.isEmpty(redisProperties.getHost())) {
            configBuilder.withHost(redisProperties.getHost());
        }
        if (redisProperties.getPort() > 0) {
            configBuilder.withPort(redisProperties.getPort());
        }
        if (!StringUtils.isEmpty(redisProperties.getPassword())) {
            configBuilder.withPassword(redisProperties.getPassword());
        }
        // Sentinel模式
        if (redisProperties.getSentinel() != null) {
            if (!StringUtils.isEmpty(redisProperties.getSentinel().getNodes())) {
                Iterable<String> sentinelHosts = Splitter.on(',').trimResults().omitEmptyStrings().split(redisProperties.getSentinel().getNodes());
                configBuilder.withSentinels(Sets.newHashSet(sentinelHosts));
            }
            if (!StringUtils.isEmpty(redisProperties.getSentinel().getMaster())) {
                configBuilder.withMasterName(redisProperties.getSentinel().getMaster());
            }
        }
        return configBuilder.build();
    }

    /**
     * 提交任务, 且任务延时执行
     *
     * @param type      任务的类型
     * @param args      任务相关参数
     * @param future    未来的时间, 非delay, 而是 current + delay
     */
    void submit(String type, List<?> args, long future) {
        Job job = new Job(ACTION_MAME, args);
        client.delayedEnqueue(type, job, future);
    }

    /**
     * 提交任务, 任务立即执行
     *
     * @param type  任务的类型
     * @param args  任务相关参数
     */
    void submit(String type, List<?> args) {
        Job job = new Job(ACTION_MAME, args);
        // client.enqueue(type, job);
        // 在redis中, 这里会存在`立即执行的Type`和`延时执行的Type`类型不同报错的问题, 这里改成延迟100毫秒执行
        client.delayedEnqueue(type, job, System.currentTimeMillis() + 100L);
    }

    /**
     * 移除延时队列中的任务
     *
     * @param type  任务类型
     * @param args  任务相关参数
     */
    void removeDelayedEnqueue(String type, List<?> args) {
        Job job = new Job(ACTION_MAME, args);
        client.removeDelayedEnqueue(type, job);
    }

    @PreDestroy
    public void shutdown(){
        executorService.shutdown();
        if (client != null) {
            client.end();
        }
        if (worker != null) {
            worker.end(true);
        }
    }

}
