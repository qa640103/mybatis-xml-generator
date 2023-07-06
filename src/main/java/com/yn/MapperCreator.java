package com.yn;

import com.yn.bean.ColumnProp;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class MapperCreator {
    public static void main(String[] args) {
        try {
            // TODO 1.从mapper.xml文件复制resultMap
            String xmlMap =
                    "<resultMap id=\"BaseResultMap\" type=\"com.amap.gemini.bean.label.Label\">\n" +
                    "        <id column=\"id\" jdbcType=\"BIGINT\" property=\"id\" />\n" +
                    "        <result column=\"source\" jdbcType=\"VARCHAR\" property=\"source\" />\n" +
                    "        <result column=\"label\" jdbcType=\"VARCHAR\" property=\"label\" />\n" +
                    "    </resultMap>";

            // TODO 2.改表名
            String tableName = "label";

            // TODO 3.执行
            execute(xmlMap, tableName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute(String xmlMap, String tableName) throws DocumentException {
        List<ColumnProp> primaryKeyList = new ArrayList<ColumnProp>();
        List<ColumnProp> columnPropList = new ArrayList<ColumnProp>();

        Element rootElement = DocumentHelper.parseText(xmlMap).getRootElement();
        String beanType = rootElement.attributeValue("type");
        List<Element> item = rootElement.elements();
        for (Element element : item) {
            ColumnProp po = new ColumnProp();
            if ("id".equals(element.getName())) {
                po.setColumn(element.attributeValue("column"));
                po.setProperty(element.attributeValue("property"));
                po.setJdbcType(element.attributeValue("jdbcType"));
                primaryKeyList.add(po);
            } else {
                po.setColumn(element.attributeValue("column"));
                po.setProperty(element.attributeValue("property"));
                po.setJdbcType(element.attributeValue("jdbcType"));
                columnPropList.add(po);
            }
        }

        // 根据条件查询
        List<ColumnProp> allColumns = new ArrayList<ColumnProp>();
        allColumns.addAll(primaryKeyList);
        allColumns.addAll(columnPropList);
        StringBuilder selectResult = new StringBuilder();
        selectResult.append("<!--根据条件查询-->\n");
        selectResult.append("<select id=\"listByCondition\" resultMap=\"BaseResultMap\" parameterType=\"java.util.Map\">\n");
        selectResult.append("\tselect \n");
        selectResult.append("\t<include refid=\"Base_Column_List\" /> \n");
        selectResult.append("\tfrom " + tableName + " \n");
        selectResult.append("\t<where>\n");
        for (ColumnProp ColumnProp : allColumns) {
            if ("VARCHAR".equalsIgnoreCase(ColumnProp.getJdbcType())){
                selectResult.append("\t\t<if test=\"" + ColumnProp.getProperty() + " != null and " + ColumnProp.getProperty() + " != ''\">\n");
            }else {
                selectResult.append("\t\t<if test=\"" + ColumnProp.getProperty() + " != null\">\n");
            }
            selectResult.append("\t\t\tand " + ColumnProp.getColumn() + " = #{" + ColumnProp.getProperty() + ",jdbcType=" + ColumnProp.getJdbcType() + "}\n");
            selectResult.append("\t\t</if>\n");
        }
        selectResult.append("\t</where>\n");
        selectResult.append("</select>");
        System.out.println(selectResult + "\n");


        // 批量更新
        StringBuilder updateSelectiveResult = new StringBuilder();
        updateSelectiveResult.append("<!--批量更新-->\n");
        updateSelectiveResult.append("<update id=\"updateByPrimaryKeySelectiveInBatch\" parameterType=\"java.util.List\">\n");
        updateSelectiveResult.append("\tupdate " + tableName + "\n");
        updateSelectiveResult.append("\t<trim prefix=\"set\" suffixOverrides=\",\">\n");
        for (ColumnProp ColumnProp : columnPropList) {
            updateSelectiveResult.append("\t\t<trim prefix=\"" + ColumnProp.getColumn() + " =case\" suffix=\"end,\">\n");
            updateSelectiveResult.append("\t\t\t<foreach collection=\"list\" item=\"item\" index=\"index\">\n");
            updateSelectiveResult.append("\t\t\t\t<if test=\"item." + ColumnProp.getProperty() + " != null\">\n");
            if (primaryKeyList.size() == 1) {
                ColumnProp key = primaryKeyList.get(0);
                updateSelectiveResult.append("\t\t\t\t\twhen " + key.getColumn() + " = #{item." + key.getProperty() + "} then #{item." + ColumnProp.getProperty() + ", jdbcType=" + ColumnProp.getJdbcType() + "}\n");
            } else {
                updateSelectiveResult.append("\t\t\t\t\twhen (");
                for (ColumnProp key : primaryKeyList) {
                    updateSelectiveResult.append(key.getColumn()).append(" = #{item.").append(key.getProperty()).append("} and ");
                }
                updateSelectiveResult.delete(updateSelectiveResult.length() - 5, updateSelectiveResult.length() - 1).append(") ");
                updateSelectiveResult.append("then #{item." + ColumnProp.getProperty() + ", jdbcType=" + ColumnProp.getJdbcType() + "}\n");
            }
            updateSelectiveResult.append("\t\t\t\t</if>\n");
            updateSelectiveResult.append("\t\t\t</foreach>\n");
            updateSelectiveResult.append("\t\t</trim>\n");
        }
        updateSelectiveResult.append("\t</trim>\n");
        updateSelectiveResult.append("\twhere\n");
        for (int i = 0; i < primaryKeyList.size(); i++) {
            updateSelectiveResult.append("\t");
            if (i != 0) {
                updateSelectiveResult.append("and ");
            }
            updateSelectiveResult.append(primaryKeyList.get(i).getColumn() + " in\n");
            updateSelectiveResult.append("\t<foreach collection=\"list\" index=\"index\" item=\"item\" separator=\",\" open=\"(\" close=\")\">\n");
            updateSelectiveResult.append("\t\t#{item." + primaryKeyList.get(i).getProperty() + ",jdbcType=" + primaryKeyList.get(i).getJdbcType() + "}\n");
            updateSelectiveResult.append("\t</foreach>\n");
        }
        updateSelectiveResult.append("</update>");
        System.out.println(updateSelectiveResult + "\n");


        // 批量更新，不判断空值
        StringBuilder updateResult = new StringBuilder();
        updateResult.append("<!--批量更新，不判断空值-->\n");
        updateResult.append("<update id=\"updateByPrimaryKeyInBatch\" parameterType=\"java.util.List\">\n");
        updateResult.append("\tupdate " + tableName + "\n");
        updateResult.append("\t<trim prefix=\"set\" suffixOverrides=\",\">\n");
        for (ColumnProp ColumnProp : columnPropList) {
            updateResult.append("\t\t<trim prefix=\"" + ColumnProp.getColumn() + " =case\" suffix=\"end,\">\n");
            updateResult.append("\t\t\t<foreach collection=\"list\" item=\"item\" index=\"index\">\n");
            if (primaryKeyList.size() == 1) {
                ColumnProp key = primaryKeyList.get(0);
                updateResult.append("\t\t\t\twhen " + key.getColumn() + " = #{item." + key.getProperty() + "} then #{item." + ColumnProp.getProperty() + ", jdbcType=" + ColumnProp.getJdbcType() + "}\n");
            } else {
                updateResult.append("\t\t\t\twhen (");
                for (ColumnProp key : primaryKeyList) {
                    updateResult.append(key.getColumn()).append(" = #{item.").append(key.getProperty()).append("} and ");
                }
                updateResult.delete(updateResult.length() - 5, updateResult.length() - 1).append(") ");
                updateResult.append("then #{item." + ColumnProp.getProperty() + ", jdbcType=" + ColumnProp.getJdbcType() + "}\n");
            }
            updateResult.append("\t\t\t</foreach>\n");
            updateResult.append("\t\t</trim>\n");
        }
        updateResult.append("\t</trim>\n");
        updateResult.append("\twhere\n");
        for (int i = 0; i < primaryKeyList.size(); i++) {
            updateResult.append("\t");
            if (i != 0) {
                updateResult.append("and ");
            }
            updateResult.append(primaryKeyList.get(i).getColumn() + " in\n");
            updateResult.append("\t<foreach collection=\"list\" index=\"index\" item=\"item\" separator=\",\" open=\"(\" close=\")\">\n");
            updateResult.append("\t\t#{item." + primaryKeyList.get(i).getProperty() + ",jdbcType=" + primaryKeyList.get(i).getJdbcType() + "}\n");
            updateResult.append("\t</foreach>\n");
        }
        updateResult.append("</update>");
        System.out.println(updateResult + "\n");


        // 批量插入
        StringBuilder insertResult = new StringBuilder();
        insertResult.append("<!--批量插入-->\n");
        insertResult.append("<insert id=\"insertInBatch\" parameterType=\"java.util.List\" useGeneratedKeys=\"true\" keyProperty=\"" + primaryKeyList.get(0).getProperty() + "\">\n");
        insertResult.append("\tinsert into " + tableName + "(");
        for (ColumnProp key : primaryKeyList) {
            insertResult.append(key.getColumn()).append(", ");
        }
        insertResult.append("\n\t\t");
        for (int i = 0; i < columnPropList.size(); i++) {
            ColumnProp ColumnProp = columnPropList.get(i);
            insertResult.append(ColumnProp.getColumn() + ", ");
            if (i != 0 && i % 5 == 0) {
                insertResult.append("\n\t\t");
            }
        }
        insertResult.deleteCharAt(insertResult.lastIndexOf(",")).append(")\n");
        insertResult.append("\tvalues\n");
        insertResult.append("\t<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">\n");
        insertResult.append("\t\t(");
        for (ColumnProp key : primaryKeyList) {
            insertResult.append("#{item." + key.getProperty() + ",jdbcType=" + key.getJdbcType() + "},");
        }
        insertResult.append("\n\t\t");
        for (int i = 0; i < columnPropList.size(); i++) {
            ColumnProp ColumnProp = columnPropList.get(i);
            insertResult.append("#{item." + ColumnProp.getProperty() + ",jdbcType=" + ColumnProp.getJdbcType() + "}, ");
            if (i != 0 && i % 3 == 0) {
                insertResult.append("\n\t\t");
            }
        }
        insertResult.deleteCharAt(insertResult.lastIndexOf(",")).append(")\n");
        insertResult.append("\t</foreach>\n");
        insertResult.append("</insert>");
        System.out.println(insertResult + "\n");


        // 插入并返回id
        StringBuilder insertAndGetId = new StringBuilder();
        insertAndGetId.append("<!--插入并返回id-->\n");
        insertAndGetId.append("<insert id=\"insertAndGetId\" parameterType=\"" + beanType + "\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n");
        insertAndGetId.append("\tinsert into " + tableName + "(");
        for (ColumnProp key : primaryKeyList) {
            insertAndGetId.append(key.getColumn()).append(", ");
        }
        insertAndGetId.append("\n\t\t");
        for (int i = 0; i < columnPropList.size(); i++) {
            ColumnProp ColumnProp = columnPropList.get(i);
            insertAndGetId.append(ColumnProp.getColumn() + ", ");
            if (i != 0 && i % 5 == 0) {
                insertAndGetId.append("\n\t\t");
            }
        }
        insertAndGetId.deleteCharAt(insertAndGetId.lastIndexOf(",")).append(")\n");
        insertAndGetId.append("\tvalues \n");
        insertAndGetId.append("\t\t(");
        for (ColumnProp key : primaryKeyList) {
            insertAndGetId.append("#{" + key.getProperty() + ",jdbcType=" + key.getJdbcType() + "},");
        }
        insertAndGetId.append("\n\t\t");
        for (int i = 0; i < columnPropList.size(); i++) {
            ColumnProp ColumnProp = columnPropList.get(i);
            insertAndGetId.append("#{" + ColumnProp.getProperty() + ",jdbcType=" + ColumnProp.getJdbcType() + "}, ");
            if (i != 0 && i % 3 == 0) {
                insertAndGetId.append("\n\t\t");
            }
        }
        insertAndGetId.deleteCharAt(insertAndGetId.lastIndexOf(",")).append(")\n");
        insertAndGetId.append("</insert>");
        System.out.println(insertAndGetId);
    }
}
