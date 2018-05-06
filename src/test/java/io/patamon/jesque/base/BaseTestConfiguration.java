package io.patamon.jesque.base;

import io.patamon.jesque.JesqueConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2018/5/6
 */
@Configuration
@EnableAutoConfiguration
@Import(JesqueConfiguration.class)
public class BaseTestConfiguration {

}
