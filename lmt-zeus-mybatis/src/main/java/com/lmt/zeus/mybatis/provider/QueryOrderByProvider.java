package com.lmt.zeus.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * @description 扩展mybatis提供根据条件查寻并根据指定字段排序功能
 * @author bazhandao
 * @date 2018/12/10 22:44
 * @since JDK1.8
 */
public class QueryOrderByProvider extends MapperTemplate {

    public QueryOrderByProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }


    /**
     * 实现扩展mybatis提供根据条件查寻并根据指定字段排序方法provider
     * @author bazhandao
     * @date 2018-12-10
     * @param ms
     * @return
     */
    public String queryOrderBy(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        sql.append("<where>");
        for (EntityColumn column : columnList) {
            sql.append(SqlHelper.getIfNotNull("entity", column, " AND " + column.getColumnEqualsHolder("entity"), true));
        }
        sql.append("</where>");
        sql.append("<if test=\"sortField != null and sortField != ''\">");
        sql.append("order by");
        for (EntityColumn column : columnList) {
            sql.append("<if test=\"sortField == '").append(column.getColumn()).append("' || sortField == '").append(column.getEntityField().getName()).append("'\">");
            sql.append(column.getColumn());
            sql.append("</if>");
        }
        sql.append("<choose>");
        sql.append("<when test=\"sortOrder == 'desc' || sortOrder == 'DESC'\">desc</when>");
        sql.append("<otherwise>asc</otherwise>");
        sql.append("</choose>");
        sql.append("</if>");
        return sql.toString();
    }

}
