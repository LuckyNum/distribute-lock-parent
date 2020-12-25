-- 分布式锁表
CREATE TABLE `distribute_lock`
(
    `id`            INT(11) auto_increment,
    `business_code` VARCHAR(40),
    `business_name` VARCHAR(100),
    PRIMARY KEY (`id`)
);