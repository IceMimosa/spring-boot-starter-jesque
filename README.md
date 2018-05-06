# 任务延时队列使用

基于[Jesque](https://github.com/gresrun/jesque)封装的springboot starter，使用注解的方式方便使用。

# 使用方法

* pom

```xml
<dependencies>
    <dependency>
        <groupId>io.patamon.jesque</groupId>
        <artifactId>spring-boot-starter-jesque</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>patamon.release.repository</id>
        <name>github release repository</name>
        <url>https://raw.github.com/icemimosa/maven/release/</url>
    </repository>
</repositories>
```

## 定义消息生产者

* 新建一个任务(队列)类型

```java
/**
 * Desc: 队列任务类型常量
 *
 * 也可在其他地方声明, 这里作为测试. 随意...
 */
public class JesqueType {

    /**
     * 测试JOB队列类型
     */
    public static final String JOB_TEST_TYPE = "JOB_TEST";
}
```

* 生产者代码如下

```java
/**
 * Desc: 队列任务生产者例子
 * 
 * [使用方式]: 使用 {@link JobClient} 接口的方法进行任务的提交
 */
@Slf4j
@RestController
@RequestMapping("/api/common/test/job")
public class DemoProvider {
    @Autowired
    private JobClient jobClient;

    @RequestMapping(value = "/{itemId}", method = RequestMethod.GET)
    public void test(@PathVariable Long itemId) {
        log.info("[Jesque Start]");
        for (int i = 1; i <= 10; i++) {
            jobClient.submit(itemId + "" + i, JesqueType.JOB_TEST_TYPE, i * 1000);
        }
        log.info("[Jesque End]");
    }

    @RequestMapping(value = "/now/{itemId}", method = RequestMethod.GET)
    public void testNow(@PathVariable Long itemId) {
        log.info("[Jesque Start]");
        jobClient.submit(itemId + "", JesqueType.JOB_TEST_TYPE);
        log.info("[Jesque End]");
    }

}
```

## 定义消息消费者

```java
/**
 * Desc: 队列任务消费者例子
 *
 * [使用方式]:
 *  1. 声明一个类, 用 {@link JobConsumer} 修饰
 *  2. 定义一个public方法, 用 {@link JobType} 修饰, 并传入任务类型常量, 如 {@link JesqueType}
 *  3. 方法需要给定一个String类型参数, 这个参数就是Provider提供的业务ID
 *
 * [注意]:
 *  需要注意的是, 同一个 {@link JobType} 的多个会被多次执行. 这里的消费方法与队列的消费者有本质的区别.
 *  队列消费者只会有一个抢到任务并消费, 这里针对部署了多份服务(多个JVM)
 *  方法消费是指在一个服务中声明了多个相同的 {@link JobType}, 所以每个方法都会被执行, 而不是原则上的多次消费.
 *
 */
@Slf4j
@JobConsumer
public class DemoConsumer {

    @JobType(JesqueType.JOB_TEST_TYPE)
    public void consume(String itemId) {
        log.info("JOB_TEST consume {}", itemId);
    }

    @JobType(JesqueType.JOB_TEST_TYPE)
    public void consume2(String itemId) {
        log.info("JOB_TEST consume2 {}", itemId);
    }
}
```