package io.patamon.jesque;

import java.util.Date;

/**
 * Desc: 任务操作客户端
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/7
 */
public interface JobClient {

    /**
     * 提交一个任务, 任务立即执行
     *
     * @param businessId 业务ID
     * @param type       任务类型
     */
    void submit(String businessId, String type);

    /**
     * 提交一个任务, 任务延时执行.
     * 如果 `delay < 1000` (1秒), 则任务立即执行
     *
     * @param businessId  业务ID
     * @param type        任务类型
     * @param delay       延时的时间, 毫秒值
     */
    void submit(String businessId, String type, long delay);

    /**
     * 提交一个任务, 任务在固定的日期执行
     * 如果date小于当前时间, 则任务立即执行
     *
     * @param businessId  业务ID
     * @param type        任务类型
     * @param date        任务执行的日期
     */
    void submit(String businessId, String type, Date date);


    /**
     * 移除一个任务
     *
     * @param businessId 业务ID
     * @param type       任务类型
     */
    void remove(String businessId, String type);
}
