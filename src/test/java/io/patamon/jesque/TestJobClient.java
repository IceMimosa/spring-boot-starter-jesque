package io.patamon.jesque;

import io.patamon.jesque.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/5/6
 */
public class TestJobClient extends BaseTest {

    @Autowired
    private JobClient jobClient;

    @Test
    public void t() {
        System.out.println(1);
    }

}
