package io.patamon.jesque.core;

import java.io.Serializable;
import java.util.List;

/**
 * Desc: Jesque 内部执行的Action
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2017/8/7
 */
public class JobAction implements Runnable, Serializable {
    private static final long serialVersionUID = -9154294237110925524L;

    private final String type;
    private final String businessId;

    public JobAction(String type, String businessId) {
        this.type = type;
        this.businessId = businessId;
    }

    @Override
    public void run() {
        List<ConsumeMethod> methods = JobContext.METHODS.get(type);
        if (methods == null || methods.isEmpty()) {
            return;
        }
        methods.forEach(method -> JobContext.executorService.submit(new MethodTask(businessId, method)));
    }
}
