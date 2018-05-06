package io.patamon.jesque.core;

import com.google.common.collect.ImmutableList;
import io.patamon.jesque.JobClient;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Desc: 任务操作客户端
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/7
 */
public class JobClientImpl implements JobClient {

    private final JobContext jobContext;

    public JobClientImpl(JobContext jobContext) {
        this.jobContext = jobContext;
    }

    /**
     * 提交一个任务, 任务立即执行
     *
     * @param businessId 业务ID
     * @param type       任务类型
     */
    @Override
    public void submit(String businessId, String type) {
        jobContext.submit(type, ImmutableList.of(type, businessId));
    }

    /**
     * 提交一个任务, 任务延时执行.
     * 如果 `delay < 1000` (1秒), 则任务立即执行
     *
     * @param businessId  业务ID
     * @param type        任务类型
     * @param delay       延时的时间, 毫秒值
     */
    @Override
    public void submit(String businessId, String type, long delay) {
        if (delay < 1000L) {
            submit(businessId, type);
        } else {
            jobContext.submit(type, ImmutableList.of(type, businessId), System.currentTimeMillis() + delay);
        }
    }

    /**
     * 提交一个任务, 任务在固定的日期执行
     * 如果date小于当前时间, 则任务立即执行
     *
     * @param businessId  业务ID
     * @param type        任务类型
     * @param date        任务执行的日期
     */
    @Override
    public void submit(String businessId, String type, Date date) {
        if (date != null && DateTime.now().isBefore(new DateTime(date))) {
            long delay = date.getTime() - System.currentTimeMillis();
            submit(businessId, type, delay);
        } else {
            submit(businessId, type);
        }
    }

    /**
     * 移除一个任务
     *
     * @param businessId 业务ID
     * @param type       任务类型
     */
    @Override
    public void remove(String businessId, String type) {
        jobContext.removeDelayedEnqueue(type, ImmutableList.of(type, businessId));
    }
}
