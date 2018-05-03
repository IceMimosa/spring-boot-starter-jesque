package io.patamon.jesque.core;

import io.patamon.jesque.annotation.JobConsumer;
import io.patamon.jesque.annotation.JobType;

import java.lang.reflect.Method;

/**
 * Desc: 需要执行的消费者
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/8
 */
public class ConsumeMethod {
    /**
     * 消费者实例, 被 {@link JobConsumer} 修饰的类
     */
    private Object consumer;

    /**
     * 需要执行的方法
     */
    private Method method;

    /**
     * 方法的 {@link JobType} 的值
     */
    private String type;

    public ConsumeMethod(Object consumer, Method method, String type) {
        this.consumer = consumer;
        this.method = method;
        this.type = type;
    }

    public ConsumeMethod() {

    }

    public Object getConsumer() {
        return consumer;
    }

    public void setConsumer(Object consumer) {
        this.consumer = consumer;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
