package com.yn.service;

import com.yn.mapper.GenericMapper;
import com.yn.query.PageQuery;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 通用服务方法接口
 */
public interface GenericService<T, PK> {

    /**
     * 获取mapper
     * @return
     */
    GenericMapper<T, PK> getMapper();

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    T getById(PK id);

    /**
     * 保存
     * @param record
     * @return
     */
    int save(T record);

    /**
     * 保存，不保存空值
     * @param record
     * @return
     */
    int saveSelective(T record);

    /**
     * 更新
     * @param record
     * @return
     */
    int update(T record);

    /**
     * 更新，不更新空值
     * @param record
     * @return
     */
    int updateSelective(T record);

    /**
     * 删除
     * @param id
     * @return
     */
    int delete(PK id);

    /**
     * 根据条件查询
     * @param param 条件
     * @return List<T>
     */
    List<T> listByCondition(Map<String, Object> param);

    /**
     * 使用listByCondition分页查询
     * @param query
     * @return
     */
    PageInfo<T> listByPage(PageQuery query);

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
