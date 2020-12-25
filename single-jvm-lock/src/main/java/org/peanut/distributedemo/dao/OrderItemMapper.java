package org.peanut.distributedemo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.peanut.distributedemo.model.OrderItem;
import org.peanut.distributedemo.model.OrderItemExample;
import org.peanut.distributedemo.model.OrderItemKey;

public interface OrderItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    long countByExample(OrderItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int deleteByExample(OrderItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int deleteByPrimaryKey(OrderItemKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int insert(OrderItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int insertSelective(OrderItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    List<OrderItem> selectByExample(OrderItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    OrderItem selectByPrimaryKey(OrderItemKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByExampleSelective(@Param("record") OrderItem record, @Param("example") OrderItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByExample(@Param("record") OrderItem record, @Param("example") OrderItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByPrimaryKeySelective(OrderItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_item
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByPrimaryKey(OrderItem record);
}