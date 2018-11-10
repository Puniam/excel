package org.excelProject.dao;

import org.apache.ibatis.annotations.Param;
import org.excelProject.pojo.Excel;

import java.util.List;

public interface ExcelMapper {
    int insert(Excel record);

    int insertSelective(Excel record);

    int insertList(List<Excel> list);

    List<Object> query(@Param("beginTime") int beginTime,
                       @Param("endTime") int endTime,
                       @Param("stockId") int stockid,
                       @Param("pageNum") int pageNum
    );

    int queryCount(@Param("beginTime") int beginTime,
                   @Param("endTime") int endTime,
                   @Param("stockId") int stockid);

    int delete(@Param("beginTime") int beginTime,
               @Param("endTime") int endTime,
               @Param("stockId") int stockid);

    int update(@Param("beginTime") int beginTime,
               @Param("endTime") int endTime,
               @Param("stockId") int stockid);

    List<Object> queryList(@Param("beginTime") int beginTime,
                       @Param("endTime") int endTime,
                       @Param("stockId") int stockid
    );

}