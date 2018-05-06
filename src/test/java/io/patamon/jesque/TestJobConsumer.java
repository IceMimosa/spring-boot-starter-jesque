package io.patamon.jesque;

import io.patamon.jesque.annotation.JobConsumer;
import io.patamon.jesque.annotation.JobType;

/**
 * Desc: 测试consumer
 * <p>
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/5/6
 */
@JobConsumer
public class TestJobConsumer {

    /**
     * 立即执行的类型
     */
    @JobType(TestJobClient.TEST_TYPE)
    public void consume(String id) {
        System.out.println("consumer => " + id);
    }

    /**
     * 立即执行的类型
     */
    @JobType(TestJobClient.TEST_DELAY_TYPE)
    public void consume_delay(String id) {
        System.out.println("consumer delay => " + id);
    }

}
