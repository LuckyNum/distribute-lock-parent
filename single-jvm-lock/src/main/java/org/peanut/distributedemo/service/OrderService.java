package org.peanut.distributedemo.service;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributedemo.dao.OrderItemMapper;
import org.peanut.distributedemo.dao.OrderMapper;
import org.peanut.distributedemo.dao.ProductMapper;
import org.peanut.distributedemo.model.Order;
import org.peanut.distributedemo.model.OrderItem;
import org.peanut.distributedemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lch
 * @date 2020-12-22 22:14:45
 */
@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductMapper productMapper;

    //购买商品id
    private int purchaseProductId = 100100;
    //购买商品数量
    private int purchaseProductNum = 1;

    /**
     * 存在超卖问题
     * 超卖现象一： 商品数为0，订单数为5
     * 超卖现象二： 商品数为-4，订单数为5
     *
     * @return 订单主键
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder1() {
        Product product = productMapper.selectByPrimaryKey(purchaseProductId);
        Optional.ofNullable(product).orElseThrow(() -> new RuntimeException("购买的商品不存在：" + purchaseProductId));

        // 商品当前库存
        Integer currentCount = product.getPnum();
        // 校验库存
        if (purchaseProductNum > currentCount) {
            throw new RuntimeException("商品: " + purchaseProductId + " 仅剩余: " + currentCount + "件，无法购买。");
        }

        //-------- 1、程序计算库存并更新
        // 计算剩余库存
        // int leftCount = currentCount - purchaseProductNum;
        // 更新库存
        // product.setPnum(leftCount);
        // productMapper.updateByPrimaryKeySelective(product);

        //-------- 2、数据库计算库存并更新
        // 计算并更新库存
        productMapper.updateProductCount(purchaseProductNum, product.getId());

        Order order = new Order();
        order.setMoney(product.getPrice());
        order.setOrdertime(new Date());
        order.setPaystate(1);
        order.setReceiveraddress("xxx省xxx市xxx县");
        order.setReceivername("张三");
        order.setReceiverphone("13322222222");
        order.setUserId(1000);
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setBuynum(purchaseProductNum);
        orderItemMapper.insertSelective(orderItem);

        return order.getId();
    }

    /**
     * 超卖现象二： 商品数为-1，订单数为2
     * 分析：因为第一次请求的事务未提交，第二次获取到的商品数依然为原来的值
     *
     * @return 订单主键
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized Integer createOrder2() {
        Product product = productMapper.selectByPrimaryKey(purchaseProductId);
        Optional.ofNullable(product).orElseThrow(() -> new RuntimeException("购买的商品不存在：" + purchaseProductId));

        // 商品当前库存
        Integer currentCount = product.getPnum();
        // 校验库存
        if (purchaseProductNum > currentCount) {
            throw new RuntimeException("商品: " + purchaseProductId + " 仅剩余: " + currentCount + "件，无法购买。");
        }
        // 计算并更新库存
        productMapper.updateProductCount(purchaseProductNum, product.getId());

        Order order = new Order();
        order.setMoney(product.getPrice());
        order.setOrdertime(new Date());
        order.setPaystate(1);
        order.setReceiveraddress("xxx省xxx市xxx县");
        order.setReceivername("张三");
        order.setReceiverphone("13322222222");
        order.setUserId(1000);
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setBuynum(purchaseProductNum);
        orderItemMapper.insertSelective(orderItem);

        return order.getId();
    }

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * synchronized 锁
     *
     * @return 订单主键
     */
    public Integer createOrderBySynchronized() {

        //-------- 块锁，控制商品数量变更事物
        Product product;
        synchronized (this) {
            TransactionStatus productTrans = platformTransactionManager.getTransaction(transactionDefinition);
            product = productMapper.selectByPrimaryKey(purchaseProductId);
            Optional.ofNullable(product).orElseThrow(() -> {
                platformTransactionManager.rollback(productTrans);
                return new RuntimeException("购买的商品不存在：" + purchaseProductId);
            });

            Integer currentCount = product.getPnum();
            if (purchaseProductNum > currentCount) {
                platformTransactionManager.rollback(productTrans);
                throw new RuntimeException("商品: " + purchaseProductId + " 仅剩余: " + currentCount + "件，无法购买。");
            }
            productMapper.updateProductCount(purchaseProductNum, product.getId());
            platformTransactionManager.commit(productTrans);
        }

        TransactionStatus orderTrans = platformTransactionManager.getTransaction(transactionDefinition);
        Order order = new Order();
        order.setMoney(product.getPrice());
        order.setOrdertime(new Date());
        order.setPaystate(1);
        order.setReceiveraddress("xxx省xxx市xxx县");
        order.setReceivername("张三");
        order.setReceiverphone("13322222222");
        order.setUserId(1000);
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setBuynum(purchaseProductNum);
        orderItemMapper.insertSelective(orderItem);
        platformTransactionManager.commit(orderTrans);

        return order.getId();
    }


    private Lock lock = new ReentrantLock();

    /**
     * ReentrantLock 锁
     *
     * @return 订单主键
     */
    public Integer createOrderByReentrantLock() {

        //-------- 块锁，控制商品数量变更事物
        Product product;
        lock.lock();
        try {
            TransactionStatus productTrans = platformTransactionManager.getTransaction(transactionDefinition);
            product = productMapper.selectByPrimaryKey(purchaseProductId);
            Optional.ofNullable(product).orElseThrow(() -> {
                platformTransactionManager.rollback(productTrans);
                return new RuntimeException("购买的商品不存在：" + purchaseProductId);
            });

            Integer currentCount = product.getPnum();
            if (purchaseProductNum > currentCount) {
                platformTransactionManager.rollback(productTrans);
                throw new RuntimeException("商品: " + purchaseProductId + " 仅剩余: " + currentCount + "件，无法购买。");
            }
            productMapper.updateProductCount(purchaseProductNum, product.getId());
            platformTransactionManager.commit(productTrans);
        } finally {
            lock.unlock();
        }

        TransactionStatus orderTrans = platformTransactionManager.getTransaction(transactionDefinition);
        Order order = new Order();
        order.setMoney(product.getPrice());
        order.setOrdertime(new Date());
        order.setPaystate(1);
        order.setReceiveraddress("xxx省xxx市xxx县");
        order.setReceivername("张三");
        order.setReceiverphone("13322222222");
        order.setUserId(1000);
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setBuynum(purchaseProductNum);
        orderItemMapper.insertSelective(orderItem);
        platformTransactionManager.commit(orderTrans);

        return order.getId();
    }
}
