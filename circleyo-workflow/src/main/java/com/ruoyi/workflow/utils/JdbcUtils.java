package com.ruoyi.workflow.utils;

import com.ruoyi.workflow.domain.TableColumn;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
    public static List<Map> jdbc(String classForName, String url, String username, String password, String sql)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //加载数据库驱动包
//            Class clas= Class.forName("com.mysql.cj.jdbc.Driver");
            Class clas= Class.forName(classForName);
            Driver driver= (Driver) clas.newInstance();
            //注册驱动
            DriverManager.registerDriver(driver);
            System.out.println("数据库驱动加载成功==select");
            System.out.println("执行SQL:"+sql);
            //建立数据库链接
//            connection= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo1?user=data_platform&password=68063bdc0cbcd52b&useUnicode=true&characterEncoding=UTF-8","root","123456");
            connection= DriverManager.getConnection(url,username,password);
            //创建操作命令
            statement=connection.createStatement();
            //执行SQL语句
//            resultSet=statement.executeQuery("select * from person ");
            resultSet=statement.executeQuery(sql);

            List<Map> list=new ArrayList<Map>();
            ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            //处理结果集
            while(resultSet.next())
            {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }
                list.add(rowData);
            }
            System.out.println("list============="+list);
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            //关闭结果集
            if (resultSet !=null)
            {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭命令
            if (statement!=null)
            {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接命令
            if (connection!=null)
            {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<TableColumn> jdbcTableColumn(String classForName, String url, String username, String password, String sql)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //加载数据库驱动包
            Class clas= Class.forName(classForName);
            Driver driver= (Driver) clas.newInstance();
            //注册驱动
            DriverManager.registerDriver(driver);
            System.out.println("数据库驱动加载成功==select");
            System.out.println("执行SQL:"+sql);
            //建立数据库链接
            connection= DriverManager.getConnection(url,username,password);
            //创建操作命令
            statement=connection.createStatement();
            //执行SQL语句
            resultSet=statement.executeQuery(sql);

            List<TableColumn> list=new ArrayList<TableColumn>();
            //处理结果集
            while(resultSet.next())
            {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setColumnName(resultSet.getString("column_name"));
                tableColumn.setIsRequired(resultSet.getString("is_required"));
                tableColumn.setIsPk(resultSet.getString("is_pk"));
                tableColumn.setSort(resultSet.getInt("sort"));
                tableColumn.setColumnComment(resultSet.getString("column_comment"));
                tableColumn.setIsIncrement(resultSet.getString("is_increment"));
                tableColumn.setColumnType(resultSet.getString("column_type"));
                list.add(tableColumn);
            }
            System.out.println("list============="+list);
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            //关闭结果集
            if (resultSet !=null)
            {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭命令
            if (statement!=null)
            {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接命令
            if (connection!=null)
            {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int jdbcSave(String classForName, String url, String username, String password, String sql)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //加载数据库驱动包
//            Class clas= Class.forName("com.mysql.cj.jdbc.Driver");
            Class clas= Class.forName(classForName);
            Driver driver= (Driver) clas.newInstance();
            //注册驱动
            DriverManager.registerDriver(driver);
            System.out.println("数据库驱动加载成功==save");
            System.out.println("执行SQL:"+sql);
            //建立数据库链接
            connection= DriverManager.getConnection(url,username,password);
            //创建操作命令
            statement=connection.createStatement();
            //执行SQL语句
            statement.execute(sql);

            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }finally {
            //关闭结果集
            if (resultSet !=null)
            {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭命令
            if (statement!=null)
            {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接命令
            if (connection!=null)
            {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
