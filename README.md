# 分布式锁🔒

> **TIP** 
> 单体应用锁`synchronized`、`ReentrantLock`； 
> 基于数据库实现分布式锁； 
> 基于缓存（Redis等）实现分布式锁； 
> 基于Zookeeper实现分布式锁；

## 场景

## 单体锁

> **TIP** 
> 项目：`single-jvm-lock`

- 基于`Synchronized`锁(原始锁)
- 基于`ReentrantLock`锁(并发包中的锁)

## 基于数据库自己实现

> **TIP** 
> 项目：`distribute-lock-impl`

### 原理

- 多个进程、多个线程访问共同组件数据库
- 通过 `select … for update` 访问同一条数据
- `for update` 锁定数据,其他线程只能等待

打开数据库查询两个窗口（Navicat）

窗口一：

```sql
-- 取消自动commit
SET @@autocommit=0;

SELECT * FROM distribute_lock FOR UPDATE；
```

查询结果：

```sql
结果正常返回
```

窗口二：

```sql
-- 取消自动commit
SET @@autocommit=0;

SELECT * FROM distribute_lock FOR UPDATE；
```

查询结果：

```sql
等待窗口一查询
```

### 使用

**阻塞型：在获取锁处等待。如果数据库连接断开则获取锁失败，程序结束。** 

- 优点:简单方便、易于理解、易于操作
- 缺点:并发量大时,对数据库压力较大
- 建议:作为锁的数据库与业务数据库分开

## 基于Redis自己实现

> **TIP** 
> 项目：`distribute-lock-impl`

**阻塞型：程序未获取锁，向下继续执行。** 

### 原理

```shell
SET resource_name random_value NX PX 30000
```

- resource_name: 资源名称，根据不同业务区分不同的锁
- random_value: 随机值，每个线程的随机值不同，释放锁时进行校验，只可以释放自己的锁
- NX: key不存在时设置成功，存在设置不成功
- PX: 自动失效时间，出现异常情况，锁可以过期失效

#### 创建锁

- 利用NX的原子性,多个线程并发时,只有一个线程可以设置成功
- 设置成功即获得锁,可以执行后续的业务处理
- 如果出现异常,过了锁的有效期,锁自动释放

#### 释放锁

- 释放锁采用 Redis的 delete命令
- 释放锁时校验之前设置的随机数,相同才能释放
- 释放锁的L∪A脚本
  
  ```lua
  if redis.call("get",KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1])
  else
    return 0
  end
  ```

原理图示：

<img title="" src="C:\Users\lch\AppData\Roaming\marktext\images\2020-12-23-18-41-02-image.png" alt="" data-align="center">

## 基于Zookeeper自己实现

> **TIP** 
> 项目：`distribute-lock-impl`

**利用Zookeeper的瞬时节点实现**

### 原理

#### 1、zookeeper 数据结构

<img src="C:\Users\lch\AppData\Roaming\Typora\typora-user-images\image-20201224110409463.png" alt="image-20201224110409463" style="zoom: 33%;" />

- 红色：持久节点

- 黄色：瞬时节点，有序。瞬时节点不可再有子节点，会话结束后，瞬时节点自动消失。

> **TIP**
>
> `Zookeeper`安装：
>
> 1、下载
>
> ```
> https://zookeeper.apache.org/
> ```
>
> 2、配置
>
> ```shell
> vim zoo.cfg
> 
> # 修改如下：
> # The number of milliseconds of each tick
> tickTime=2000
> # The number of ticks that the initial 
> # synchronization phase can take
> initLimit=10
> # The number of ticks that can pass between 
> # sending a request and getting an acknowledgement
> syncLimit=5
> # the directory where the snapshot is stored.
> # do not use /tmp for storage, /tmp here is just 
> # example sakes.
> dataDir=/usr/local/zookeeper-3.6.2/data
> # the port at which the clients will connect
> clientPort=2181
> ```
>
> 3、启动
>
> ```shell
> ./zkServer.sh start
> ```
>
> 4、使用
>
> ```shell
> # 连接
> ./zkCli.sh
> 
> # 查看节点
> [zk: localhost:2181(CONNECTED) 0] ls /
> [zookeeper]
> ```

#### 2、zookeeper 观察器

- 可设置观察器的3个方法：getData()，getChildren()，exists()
- 节点数据发生变化，发送给客户端
- 观察器只能监控一次，再次监控需重新设置

> **TIP**
>
> Curator不存在此问题：观察器只能监控一次，再次监控需重新设置

#### 3、实现原理

- 利用 Zookeeper的瞬时有序节点的特性
- 多线程并发创建瞬时节点时,得到有序的序列
- 序号最小的线程获得锁
- 其他的线程则监听自己序号的前一个序号
- 前一个线程执行完成,删除自己序号的节点
- 下一个序号的线程得到通知,继续执行

```shell
[zk: localhost:2181(CONNECTED) 78] ls /demoKey 
[demoKey_0000000014, demoKey_0000000015]
[zk: localhost:2181(CONNECTED) 79] ls /demoKey 
[demoKey_0000000015]
[zk: localhost:2181(CONNECTED) 80] ls /demoKey 
[]
```



<img src="C:\Users\lch\AppData\Roaming\Typora\typora-user-images\image-20201224112904804.png" alt="image-20201224112904804" style="zoom:50%;" />

## 基于Zookeeper的Curator客户端

> **TIP** 
> 项目：`distribute-lock-client`

### 集成

- 依赖

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.2.0</version>
</dependency>
```

- 配置

CuratorConfig:

```java
@Configuration
public class CuratorConfig {

    @Value("${zookeeper.connect.url}")
    private String connectString;

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework getCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }
}
```

application.properties

```properties
# zookeeper 连接设置
zookeeper.connect.url=192.168.58.10:2181
```

### 使用

```java
@RequestMapping("/curatorLock")
public String curatorLock() {
    log.info("进入了方法！");
    StopWatch watch = new StopWatch("server.port: " + port);
    watch.start();
    InterProcessMutex lock = new InterProcessMutex(client, "/order");
    try {
        if (lock.acquire(30, TimeUnit.SECONDS)) {
            log.info("获得了锁！");
            Thread.sleep(10_000L);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            log.info("释放了锁！");
            lock.release();
        } catch (Exception e) {
            log.info("释放锁失败！");
            e.printStackTrace();
        }
    }
    watch.stop();
    log.info("方法执行完成: {}", watch.prettyPrint());
    return "方法执行完成！";
}
```

## 基于Redis的Redisson客户端

> **TIP** 
> 项目：`distribute-lock-client`

### 集成

- 依赖

``` xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.11.2</version>
</dependency>
```

- 配置

配置文件：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://redisson.org/schema/redisson
       http://redisson.org/schema/redisson/redisson.xsd">

    <redisson:client>
        <redisson:single-server address="redis://127.0.0.1:6379"/>
    </redisson:client>
</beans>
```

导入配置文件：

```java
@SpringBootApplication
@ImportResource("classpath*:redisson.xml")
public class DistributeLockApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DistributeLockApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
```

### 使用

```java
@RequestMapping("/redissonLock")
public String redissonLock() {
    log.info("进入了方法！");
    StopWatch watch = new StopWatch("server.port: " + port);
    watch.start();
    RLock lock = redisson.getLock("order");
    try {
        lock.lock(30, TimeUnit.SECONDS);
        log.info("获得了锁！");
        Thread.sleep(10_000L);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        log.info("释放了锁！");
        lock.unlock();
    }
    watch.stop();
    log.info("方法执行完成： {}", watch.prettyPrint());
    return "方法执行完成！";
}
```

### SpringBoot 集成 Redisson Start

#### 集成

- 依赖

``` xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.12.0</version>
</dependency>
```

- 配置

```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=1
```

#### 使用

``` java
@RestController
@Slf4j
public class RedissonLockController {

    @Autowired
    private RedissonClient client;

    @Value("${server.port}")
    private String port;

    @RequestMapping("/redissonLock")
    public String redissonLock() {
        log.info("进入了方法！");
        StopWatch watch = new StopWatch("server.port: " + port);
        watch.start();
        RLock lock = client.getLock("order");
        try {
            lock.lock(30, TimeUnit.SECONDS);
            log.info("获得了锁！");
            Thread.sleep(10_000L);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("释放了锁！");
            lock.unlock();
        }
        watch.stop();
        log.info("方法执行完成： {}", watch.prettyPrint());
        return "方法执行完成！";
    }
}
```

