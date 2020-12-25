package org.peanut.distributedemo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.peanut.distributedemo.model.Order;
import org.peanut.distributedemo.model.OrderExample;

public interface OrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    long countByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int deleteByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int insertSelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    List<Order> selectByExample(OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order
     *
     * @mbg.generated Tue Dec 22 23:14:42 CST 2020
     */
    int updateByPrimaryKey(Order record);
}