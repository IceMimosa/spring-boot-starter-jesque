package io.patamon.jesque.base;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/5/6
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BaseTestConfiguration.class)
@ActiveProfiles("test")
public class BaseTest {

}
