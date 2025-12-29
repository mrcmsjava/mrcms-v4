package com.wuweibi.cms;

import org.junit.jupiter.api.Test;
import org.marker.mushroom.utils.FileUtils;

import java.util.Arrays;
import java.util.stream.Stream;

public class SqlTest {
    @Test
    public void test2(){
        String sql = FileUtils.getResourceFile("/data/sql/db_app.sql");
        sql = sql.replace("\r\n","\n"); // 统一转换unix换行格式
        sql = sql.replace("\r","\n"); // 统一转换unix换行格式
//        sql = sql.replaceAll("`mr_", "`"+prefix);//替换前缀
//                System.out.println(sql);

        String[] sqla = sql.split(";\n");
        Arrays.stream(sqla).forEach(item->{
            System.out.println("sql1:"+item);
            System.out.println("");
        });
    }
}
