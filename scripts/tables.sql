-- 商品表
CREATE TABLE `product`
(
    `id`          INT(11) auto_increment,
    `name`        VARCHAR(40),
    `price`       DOUBLE,
    `category`    VARCHAR(40),
    `pnum`        INT(11),
    `imgurl`      VARCHAR(100),
    `description` VARCHAR(255),
    PRIMARY KEY (`id`)
);
-- 订单表
CREATE TABLE `order`
(
    `id`              INT(11) auto_increment,
    `money`           DOUBLE,
    `receiverAddress` VARCHAR(255),
    `receiverName`    VARCHAR(20),
    `receiverPhone`   VARCHAR(20),
    `paystate`        INT(11),
    `ordertime`       TIMESTAMP,
    `user_id`         INT(11),
    PRIMARY KEY (`id`)
);
-- 订单项表
CREATE TABLE `order_item`
(
    `order_id`   INT(11),
    `product_id` INT(11),
    `buynum`     INT(11),
    PRIMARY KEY (`order_id`, `product_id`),
    FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);