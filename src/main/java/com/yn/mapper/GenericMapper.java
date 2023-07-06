package com.yn.mapper;

import java.util.List;
import java.util.Map;

/**
 * 通用mapper
 * 用于业务mapper继承
 */
public interface GenericMapper<T, PK> {

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    T selectByPrimaryKey(PK id);

    /**
     * 插入记录
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 插入记录，不插入空值
     * @param record
     * @return
     */
    int insertSelective(T record);

    /**
     * 更新记录
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);

    /**
     * 更新记录，不更新空值
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 删除记录
     * @param id
     * @return
     */
    int deleteByPrimaryKey(PK id);

    /**
     * 根据条件查询
     * @param param 条件
     * @return List<T>
     */
    List<T> listByCondition(Map<String, Object> param);

    /**
     * 插入记录并赋值记录ID
     * @param t bean
     * @return Integer
     */
    int insertAndGetId(T t);

    /**
     * 批量插入
     * @param dataList 数据List
     * @return Integer
     */
    int insertInBatch(List<T> dataList);

    /**
     * 根据主键批量更新
     * @param dataList 数据List
     * @return Integer
     */
    int updateByPrimaryKeySelectiveInBatch(List<T> dataList);

    /**
     * 根据主键批量更新，不判断空值
     * @param dataList 数据List
     * @return Integer
     */
    int updateByPrimaryKeyInBatch(List<T> dataList);
}
