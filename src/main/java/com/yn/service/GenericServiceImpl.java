package com.yn.service;

import com.github.pagehelper.PageInfo;
import com.yn.query.PageQuery;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 通用服务方法实现
 */
public abstract class GenericServiceImpl<T, PK> implements GenericService<T, PK> {

    private static final int BATCH_SIZE = 1000;

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    @Override
    public T getById(PK id) {
        return getMapper().selectByPrimaryKey(id);
    }

    /**
     * 保存
     * @param record
     * @return
     */
    @Override
    public int save(T record) {
        return getMapper().insert(record);
    }

    /**
     * 保存，不保存空值
     * @param record
     * @return
     */
    @Override
    public int saveSelective(T record) {
        return getMapper().insertSelective(record);
    }

    /**
     * 更新
     * @param record
     * @return
     */
    @Override
    public int update(T record) {
        return getMapper().updateByPrimaryKey(record);
    }

    /**
     * 更新，不更新空值
     * @param record
     * @return
     */
    @Override
    public int updateSelective(T record) {
        return getMapper().updateByPrimaryKeySelective(record);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public int delete(PK id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询
     * @param param 条件
     * @return List<T>
     */
    @Override
    public List<T> listByCondition(Map<String, Object> param){
        return getMapper().listByCondition(param);
    }

    /**
     * 使用listByCondition分页查询
     * @param query
     * @return
     */
    @Override
    public PageInfo<T> listByPage(PageQuery query) {
        query.setPageInfo();
        query.handleSortMap();
        List<T> dataList = listByCondition(beanToMap(query));
        return new PageInfo<>(dataList);
    }

    /**
     * 插入记录并赋值记录ID
     * @param t bean
     * @return Integer
     */
    @Override
    public int insertAndGetId(T t){
        return getMapper().insertAndGetId(t);
    }

    /**
     * 批量插入
     * @param dataList 数据List
     * @return Integer
     */
    @Override
    public int insertInBatch(List<T> dataList){
        if (dataList == null || dataList.size() == 0){
            return -1;
        }
        List<List<T>> splitList = splitList(dataList, BATCH_SIZE);
        for (List<T> tList : splitList) {
            getMapper().insertInBatch(tList);
        }
        return 0;
    }

    /**
     * 根据主键批量更新
     * @param dataList 数据List
     * @return Integer
     */
    @Override
    public int updateByPrimaryKeySelectiveInBatch(List<T> dataList){
        if (dataList == null || dataList.size() == 0){
            return -1;
        }
        List<List<T>> splitList = splitList(dataList, BATCH_SIZE);
        for (List<T> tList : splitList) {
            getMapper().updateByPrimaryKeySelectiveInBatch(tList);
        }
        return 0;
    }

    /**
     * 根据主键批量更新，不判断空值
     * @param dataList 数据List
     * @return Integer
     */
    @Override
    public int updateByPrimaryKeyInBatch(List<T> dataList){
        if (dataList == null || dataList.size() == 0){
            return -1;
        }
        List<List<T>> splitList = splitList(dataList, BATCH_SIZE);
        for (List<T> tList : splitList) {
            getMapper().updateByPrimaryKeyInBatch(tList);
        }
        return 0;
    }

    /**
     * 对象转Map
     * @param object
     * @return
     */
    private Map<String, Object> beanToMap(Object object){
        try {
            // 获取父类字段
            List<Field> fieldList = new ArrayList<>();
            Class<?> clazz = object.getClass();
            while (clazz != null){
                fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }
            // 获取字段值
            Map<String, Object> map = new HashMap<>();
            for (Field field : fieldList) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将源List按照指定元素数量拆分为多个List
     * @param source 源List
     * @param splitItemNum 每个List中元素数量
     * @return
     * @param <T>
     */
    private <T> List<List<T>> splitList(List<T> source, int splitItemNum) {
        List<List<T>> result = new ArrayList<>();

        if (source != null && source.size() > 0 && splitItemNum > 0) {
            if (source.size() <= splitItemNum) {
                // 源List元素数量小于等于目标分组数量
                result.add(source);
            } else {
                // 计算拆分后list数量
                int splitNum = (source.size() % splitItemNum == 0) ? (source.size() / splitItemNum) : (source.size() / splitItemNum + 1);

                List<T> value;
                for (int i = 0; i < splitNum; i++) {
                    if (i < splitNum - 1) {
                        value = source.subList(i * splitItemNum, (i + 1) * splitItemNum);
                    } else {
                        // 最后一组
                        value = source.subList(i * splitItemNum, source.size());
                    }
                    result.add(value);
                }
            }
        }
        return result;
    }
}
