package io.patamon.jesque.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.JobFactory;
import net.greghaines.jesque.worker.WorkerPoolImpl;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.Collection;

/**
 * Desc: 增强 WorkerPoolImpl
 * <p>
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/9/25
 */
public class SpringWorkerPoolImpl extends WorkerPoolImpl {

    public SpringWorkerPoolImpl(Config config, Collection<String> queues, JobFactory jobFactory, Pool<Jedis> jedisPool) {
        super(config, queues, jobFactory, jedisPool);
    }

    /**
     * 获取 jedisPool
     */
    public Pool<Jedis> getJedisPool() {
        return super.jedisPool;
    }

    /**
     * 获取 config
     */
    public Config getConfig() {
        return super.config;
    }

    /**
     * 启动当前的 Runnable
     */
    public void start() {
        new ThreadFactoryBuilder().build()
                .newThread(this)
                .start();
    }

}
