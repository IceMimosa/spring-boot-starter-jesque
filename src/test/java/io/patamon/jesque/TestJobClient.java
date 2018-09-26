package io.patamon.jesque;

import io.patamon.jesque.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/5/6
 */
public class TestJobClient extends BaseTest {

    @Autowired
    private JobClient jobClient;
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 类型
     */
    public static final String TEST_TYPE = "TEST_TYPE";
    public static final String TEST_DELAY_TYPE = "TEST_DELAY_TYPE";

    /**
     * 测试立即执行
     */
    @Test
    public void test_submit_now() throws InterruptedException {
        jobClient.submit("1", TEST_TYPE);
        latch.await();
    }

    /**
     * 测试延迟执行
     */
    @Test
    public void test_submit_delay() throws InterruptedException {
        // 延迟5秒执行
        jobClient.submit("1", TEST_DELAY_TYPE, 5000);
        latch.await();
    }

    /**
     * 测试取消
     */
    @Test
    public void test_submit_cancel() throws InterruptedException {
        // 延迟5秒执行
        jobClient.submit("1", TEST_DELAY_TYPE, 5000);
        jobClient.submit("2", TEST_DELAY_TYPE, 5000);
        // 取消
        jobClient.remove("2", TEST_DELAY_TYPE);
        latch.await();
    }

    @Test
    public void test_redis_shutdown() throws InterruptedException {
        jobClient.submit("1", TEST_TYPE);
        // shutdown redis and restart
        Thread.sleep(20000L);
        jobClient.submit("2", TEST_TYPE);
        latch.await();
    }
}
