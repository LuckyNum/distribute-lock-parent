package org.peanut.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * 基于 Zookeeper 的分布式锁
 *
 * @author lch
 * @date 2020-12-24 11:33:10
 */
@Slf4j
public class ZkLock implements AutoCloseable, Watcher {

    private final ZooKeeper zooKeeper;

    private final String businessName;

    private String businessNode;

    private final Consumer<String> closeHandler;

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZkLock(String connectString, String businessName, Consumer<String> closeHandler) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, 30000, this);
        this.businessName = businessName;
        this.closeHandler = closeHandler;
    }

    public Boolean getLock() {
        try {
            // 创建业务 根节点 /order
            Stat exists = zooKeeper.exists("/" + businessName, false);
            if (exists == null) {
                zooKeeper.create("/" + businessName,
                        businessName.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
            // 创建锁 瞬时有序节点 /order/order_00000001
            businessNode = zooKeeper.create("/" + businessName + "/" + businessName + "_", businessName.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            // 获取业务 根节点的所有子节点 并 排序，获取最小的序号的节点
            List<String> childrenNodes = zooKeeper.getChildren("/" + businessName, false);
            Collections.sort(childrenNodes);
            String firstNode = childrenNodes.get(0);
            // 如果创建的节点是第一个节点（最小的节点），则获得锁
            if (businessNode.endsWith(firstNode)) {
                return true;
            }

            // 如果未获得锁，则等待上一节点（监听节点）
            String previousNode = firstNode;
            for (String node : childrenNodes) {
                if (businessNode.endsWith(node)) {
                    // 创建上一节点的监听器
                    zooKeeper.exists("/" + businessName + "/" + previousNode, true);
                    break;
                } else {
                    previousNode = node;
                }
            }

            // 等待上一个节点释放
            // 方式一
            synchronized (this) {
                wait();
            }
            // 方式二
            // countDownLatch.await();

            // 结束等待，则获取锁，返回成功
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        zooKeeper.delete(businessNode, -1);
        zooKeeper.close();
        closeHandler.accept(businessNode);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.EventType.NodeDeleted.equals(watchedEvent.getType())) {
            // 方式一
            synchronized (this) {
                notify();
            }
            // 方式二
            // countDownLatch.countDown();
        }
    }
}
