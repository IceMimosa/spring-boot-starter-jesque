package io.patamon.jesque.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Desc: 消费者实例方法执行
 * <p>
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/7
 */
public class MethodTask implements Runnable, Serializable {
    private static final long serialVersionUID = -2697267268471440803L;

    private static Logger log = LoggerFactory.getLogger(MethodTask.class);

    private final String businessId;
    private final ConsumeMethod consumeMethod;

    MethodTask(String businessId, ConsumeMethod consumeMethod) {
        this.businessId = businessId;
        this.consumeMethod = consumeMethod;
    }

    @Override
    public void run() {
        Object consumer = consumeMethod.getConsumer();
        Method method = consumeMethod.getMethod();
        String type = consumeMethod.getType();
        try {
            if (method == null || consumer == null) {
                return;
            }
            // 执行方法
            method.invoke(consumer, businessId);
        } catch (Exception e) {
            log.error("Jesque job invoke error, type is {}, businessId is {}, comsumer is {}, method is {}, cause by {}",
                    type, businessId, consumer.getClass().getName(), method.getName(), e);
            throw new RuntimeException(e);
        }
    }
}
