package com.ruoyi.workflow.service.impl;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.ruoyi.common.datasource.annotation.Slave;
//import com.ruoyi.common.redis.service.RedisService;
//import com.ruoyi.common.security.utils.SecurityUtils;
//import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.config.DruidConfig;
import com.ruoyi.workflow.domain.*;
import com.ruoyi.workflow.domain.vo.ParamVo;
import com.ruoyi.workflow.entity.JdbcEntity;
import com.ruoyi.workflow.utils.DataSetDetailUtil;
import com.ruoyi.workflow.utils.HttpUtils;
import com.ruoyi.workflow.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.mapper.WfFormMapper;
import com.ruoyi.workflow.service.IWfFormService;

/**
 * 工作流表单Service业务层处理
 *
 * @author circleyo
 * @date 2022-06-12
 */
@Service
@Repository
public class WfFormServiceImpl implements IWfFormService {
    @Autowired
    private WfFormMapper wfFormMapper;

    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        return wfFormMapper.selectDbTableList(genTable);
    }

    /**
     * 查询工作流表单
     *
     * @param id 工作流表单主键
     * @return 工作流表单
     */
    @Override
    public WfForm selectWfFormById(Long id) {
        return wfFormMapper.selectWfFormById(id);
    }

    /**
     * 查询工作流表单列表
     *
     * @param wfForm 工作流表单
     * @return 工作流表单
     */
    @Override
    public List<WfForm> selectWfFormList(WfForm wfForm) {
        return wfFormMapper.selectWfFormList(wfForm);
    }

    /**
     * 新增工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int insertWfForm(WfForm wfForm) {
        return wfFormMapper.insertWfForm(wfForm);
    }

    /**
     * 修改工作流表单
     *
     * @param wfForm 工作流表单
     * @return 结果
     */
    @Override
    public int updateWfForm(WfForm wfForm) {
        return wfFormMapper.updateWfForm(wfForm);
    }

    /**
     * 批量删除工作流表单
     *
     * @param ids 需要删除的工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormByIds(Long[] ids) {
        return wfFormMapper.deleteWfFormByIds(ids);
    }

    /**
     * 删除工作流表单信息
     *
     * @param id 工作流表单主键
     * @return 结果
     */
    @Override
    public int deleteWfFormById(Long id) {
        return wfFormMapper.deleteWfFormById(id);
    }

    @Override
    public List<TableColumn> findFieldList(String table) {
        return wfFormMapper.selectDbTableColumnsByName(table);
    }

    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public List<TableColumn> findFieldListSlave(String table) {
        return wfFormMapper.selectDbTableColumnsByName(table);
    }

    @Override
    public int addItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.dynamicInsert(wfForm);
    }

    @Override
    public int deleteItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.deleteItemData(wfForm);
    }

    @Override
    public int updateItemData(WfForm wfForm) {
        WfForm currentForm = wfFormMapper.selectWfFormById(wfForm.getId());
        wfForm.setTableName(currentForm.getTableName());
        return wfFormMapper.updateItemData(wfForm);
    }

    @Override
    public List<ResourceVO> dataList() {
        List<ResourceVO> result = new ArrayList<>();
        RelativeData rel1 = new RelativeData("sql=========Table");
        RelativeData rel2 = new RelativeData("excel");
        RelativeData rel3 = new RelativeData("sqlTable");
        RelativeData rel4 = new RelativeData("sqlTable");
        ResourceVO r1 = new ResourceVO("2", "数据集", 1L, null,null);
        ResourceVO r2 = new ResourceVO("eff75208b8b547ea84bed4a54051192a", "工作台", 5L, null,null);
        ResourceVO r3 = new ResourceVO("089a61206f9a4fefb7a92a8444af9137", "sql查询", 5L, null,null);
        ResourceVO r4 = new ResourceVO("be6beb23790443c1b854a871b5ad6aa5", "DataMart查询", 5L, null,null);
        ResourceVO r5 = new ResourceVO("a96125427e854a3ebe7514c2b80d9e6c", "语言", 4L, null,"lang");
        ResourceVO r6 = new ResourceVO("a5a9c13e52e940e6bd1c7254ed73b998", "ddd", 5L, null,null);
        ResourceVO r7 = new ResourceVO("726465814a47490396e16bda49f9eb1b", "A", 5L, null,null);
        ResourceVO r8 = new ResourceVO("6cad66f2ee3b4414a51e5eea6b99a1ad", "eeeee", 5L, null,null);
        ResourceVO r9 = new ResourceVO("ee3d8d1a51f74e2c9ce8118f923e3079", "eeeee-复制", 4L, null,"coffee");
        ResourceVO r10 = new ResourceVO("36f7be4cc95742cdafcd13ada110c7c5", "茶叶销售", 4L, null,"tea_sale");
        ResourceVO r11 = new ResourceVO("f5325f13de7b4cc681b77aa1fc380136", "职员信息", 4L, null,"person");
        r5.setRelativeData(rel1);
        r7.setRelativeData(rel2);
        r8.setRelativeData(rel3);
        r9.setRelativeData(rel4);
        r1.getChildren().add(r2);
        r2.getChildren().add(r3);
        r2.getChildren().add(r4);
        r4.getChildren().add(r5);
        r6.getChildren().add(r7);
        r6.getChildren().add(r8);
        r6.getChildren().add(r9);
        r6.getChildren().add(r10);
        r6.getChildren().add(r11);
        r2.getChildren().add(r6);
        result.add(r1);
        return result;
    }

//    @Override
//    public Object dataListObject() {
//        List<ResourceVO> result = new ArrayList<>();
//        RelativeData rel1 = new RelativeData("sql=========Table");
//        RelativeData rel2 = new RelativeData("excel");
//        RelativeData rel3 = new RelativeData("sqlTable");
//        RelativeData rel4 = new RelativeData("sqlTable");
//        ResourceVO r1 = new ResourceVO("2", "数据集", 1L, null,null);
//        ResourceVO r2 = new ResourceVO("eff75208b8b547ea84bed4a54051192a", "工作台", 5L, null,null);
//        ResourceVO r3 = new ResourceVO("089a61206f9a4fefb7a92a8444af9137", "sql查询", 5L, null,null);
//        ResourceVO r4 = new ResourceVO("be6beb23790443c1b854a871b5ad6aa5", "DataMart查询", 5L, null,null);
//        ResourceVO r5 = new ResourceVO("a96125427e854a3ebe7514c2b80d9e6c", "语言", 4L, null,"lang");
//        ResourceVO r6 = new ResourceVO("a5a9c13e52e940e6bd1c7254ed73b998", "ddd", 5L, null,null);
//        ResourceVO r7 = new ResourceVO("726465814a47490396e16bda49f9eb1b", "A", 5L, null,null);
//        ResourceVO r8 = new ResourceVO("6cad66f2ee3b4414a51e5eea6b99a1ad", "eeeee", 5L, null,null);
//        ResourceVO r9 = new ResourceVO("ee3d8d1a51f74e2c9ce8118f923e3079", "eeeee-复制", 4L, null,"coffee");
//        ResourceVO r10 = new ResourceVO("36f7be4cc95742cdafcd13ada110c7c5", "茶叶销售", 4L, null,"tea_sale");
//        ResourceVO r11 = new ResourceVO("f5325f13de7b4cc681b77aa1fc380136", "职员信息", 4L, null,"person");
//        r5.setRelativeData(rel1);
//        r7.setRelativeData(rel2);
//        r8.setRelativeData(rel3);
//        r9.setRelativeData(rel4);
//        r1.getChildren().add(r2);
//        r2.getChildren().add(r3);
//        r2.getChildren().add(r4);
//        r4.getChildren().add(r5);
//        r6.getChildren().add(r7);
//        r6.getChildren().add(r8);
//        r6.getChildren().add(r9);
//        r6.getChildren().add(r10);
//        r6.getChildren().add(r11);
//        r2.getChildren().add(r6);
//
//        //3.1 api
//        JSONArray dataArray = DataSetDetailUtil.getDataSetTree();
//        if(dataArray != null){
//            dataArray.add(r1);
//            return dataArray;
//        }
//                result.add(r1);
//        return result;
//    }

    @Override
    public Object dataListObject() {
        List<ResourceVO> result = new ArrayList<>();
        RelativeData rel1 = new RelativeData("sql=========Table");
        RelativeData rel2 = new RelativeData("excel");
        RelativeData rel3 = new RelativeData("sqlTable");
        RelativeData rel4 = new RelativeData("sqlTable");
        ResourceVO r1 = new ResourceVO("2", "数据集", 1L, null,null);
        ResourceVO r2 = new ResourceVO("eff75208b8b547ea84bed4a54051192a", "工作台", 5L, null,null);
        ResourceVO r3 = new ResourceVO("089a61206f9a4fefb7a92a8444af9137", "sql查询", 5L, null,null);
        ResourceVO r4 = new ResourceVO("be6beb23790443c1b854a871b5ad6aa5", "DataMart查询", 5L, null,null);
        ResourceVO r5 = new ResourceVO("a96125427e854a3ebe7514c2b80d9e6c", "语言", 4L, null,"lang");
        ResourceVO r6 = new ResourceVO("a5a9c13e52e940e6bd1c7254ed73b998", "ddd", 5L, null,null);
        ResourceVO r7 = new ResourceVO("726465814a47490396e16bda49f9eb1b", "A", 5L, null,null);
        ResourceVO r8 = new ResourceVO("6cad66f2ee3b4414a51e5eea6b99a1ad", "eeeee", 5L, null,null);
        ResourceVO r9 = new ResourceVO("ee3d8d1a51f74e2c9ce8118f923e3079", "eeeee-复制", 4L, null,"coffee");
        ResourceVO r10 = new ResourceVO("36f7be4cc95742cdafcd13ada110c7c5", "茶叶销售", 4L, null,"tea_sale");
        ResourceVO r11 = new ResourceVO("f5325f13de7b4cc681b77aa1fc380136", "职员信息", 4L, null,"person");
        r5.setRelativeData(rel1);
        r7.setRelativeData(rel2);
        r8.setRelativeData(rel3);
        r9.setRelativeData(rel4);
        r1.getChildren().add(r2);
        r2.getChildren().add(r3);
        r2.getChildren().add(r4);
        r4.getChildren().add(r5);
        r6.getChildren().add(r7);
        r6.getChildren().add(r8);
        r6.getChildren().add(r9);
        r6.getChildren().add(r10);
        r6.getChildren().add(r11);
        r2.getChildren().add(r6);

        //3.1 api
        JSONArray dataArray = DataSetDetailUtil.getDataSetTree();
        if(dataArray != null){
//            dataArray.add(r1);
            return dataArray;
        }
        result.add(r1);
        return result;
    }

//    @Override
//    public Object dataListObject() {
//        List<ResourceVO> result = new ArrayList<>();
//        RelativeData rel1 = new RelativeData("sql=========Table");
//        RelativeData rel2 = new RelativeData("excel");
//        RelativeData rel3 = new RelativeData("sqlTable");
//        RelativeData rel4 = new RelativeData("sqlTable");
//        ResourceVO r1 = new ResourceVO("2", "数据集", 1L, null,null);
//        ResourceVO r2 = new ResourceVO("eff75208b8b547ea84bed4a54051192a", "工作台", 5L, null,null);
//        ResourceVO r3 = new ResourceVO("089a61206f9a4fefb7a92a8444af9137", "sql查询", 5L, null,null);
//        ResourceVO r4 = new ResourceVO("be6beb23790443c1b854a871b5ad6aa5", "DataMart查询", 5L, null,null);
//        ResourceVO r5 = new ResourceVO("a96125427e854a3ebe7514c2b80d9e6c", "语言", 4L, null,"lang");
//        ResourceVO r6 = new ResourceVO("a5a9c13e52e940e6bd1c7254ed73b998", "ddd", 5L, null,null);
//        ResourceVO r7 = new ResourceVO("726465814a47490396e16bda49f9eb1b", "A", 5L, null,null);
//        ResourceVO r8 = new ResourceVO("6cad66f2ee3b4414a51e5eea6b99a1ad", "eeeee", 5L, null,null);
//        ResourceVO r9 = new ResourceVO("ee3d8d1a51f74e2c9ce8118f923e3079", "eeeee-复制", 4L, null,"coffee");
//        ResourceVO r10 = new ResourceVO("36f7be4cc95742cdafcd13ada110c7c5", "茶叶销售", 4L, null,"tea_sale");
//        ResourceVO r11 = new ResourceVO("f5325f13de7b4cc681b77aa1fc380136", "职员信息", 4L, null,"person");
//        r5.setRelativeData(rel1);
//        r7.setRelativeData(rel2);
//        r8.setRelativeData(rel3);
//        r9.setRelativeData(rel4);
//        r1.getChildren().add(r2);
//        r2.getChildren().add(r3);
//        r2.getChildren().add(r4);
//        r4.getChildren().add(r5);
//        r6.getChildren().add(r7);
//        r6.getChildren().add(r8);
//        r6.getChildren().add(r9);
//        r6.getChildren().add(r10);
//        r6.getChildren().add(r11);
//        r2.getChildren().add(r6);
//
//        String res = HttpUtils.sendGet("http://admin.datains.cn/finance-admin/form/getDataSetTree",null);
//        JSONObject jsonObject = JSON.parseObject(res);
//        Integer code = (Integer) jsonObject.get("code");
//        if(code == 1000){
//            JSONArray dataArray = jsonObject.getJSONArray("data");
//            JSONObject childrenJson = dataArray.getJSONObject(0);
//            JSONArray childrenArray = childrenJson.getJSONArray("children");
//            childrenArray.add(r2);
////            for(int j = 0;j<childrenArray.size();j++){
////                JSONObject children = childrenArray.getJSONObject(j);
////                String id = children.getString("id");
////                String resourcename = children.getString("resourcename");
////                Long resourcetype = Long.valueOf((Integer) children.get("resourcetype"));
////                ResourceVO r10 = new ResourceVO(id, resourcename, resourcetype, null,null);
////                JSONArray ch = children.getJSONArray("children");
////                for(int i = 0;i<ch.size();i++){
////                    JSONObject object = ch.getJSONObject(i);
////                    ResourceVO r100 = new ResourceVO(object.getString("id"), object.getString("resourcename"), Long.valueOf((Integer)object.get("resourcetype")), null,null);
////                    JSONObject relativeData = object.getJSONObject("relativeData");
////                    RelativeData rel100 = new RelativeData(relativeData.getString("tbType"));
////                    r100.setRelativeData(rel100);
////                    r10.getChildren().add(r100);
////                }
////                r2.getChildren().add(r10);
////            }
//            return childrenJson;
//        }
////        result.add(r1);
//        return jsonObject;
//    }

    @Override
    public UserDBVO userDataList(String table) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
        result.setConstants(new UserInfoVO(loginUser.getUserId(),loginUser.getUsername(),"",""));
        List<DataSetVO> dataSetList=new ArrayList<>();
        DataSetVO dateSet1=new DataSetVO();
        dateSet1.setDataSetId(table);
        List<Map> valueList1=this.wfFormMapper.selectTableDataList(table);

        dateSet1.setValueList(valueList1);

        dataSetList.add(dateSet1);

        result.setDataSetList(dataSetList);
        return result;
    }

    @Override
    public UserDBVO userDataListParam(ParamVo paramVo) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
        result.setConstants(new UserInfoVO(loginUser.getUserId(),loginUser.getUsername(),"",""));
        List<DataSetVO> dataSetList=new ArrayList<>();
        DataSetVO dateSet1=new DataSetVO();
        dateSet1.setDataSetId(paramVo.getTable());
        List<Map> valueList1=this.wfFormMapper.selectTableDataListParam(paramVo);

        dateSet1.setValueList(valueList1);

        dataSetList.add(dateSet1);

        result.setDataSetList(dataSetList);
        return result;
    }


    @Override
//    @Slave
    @DataSource(value = DataSourceType.SLAVE)
    public UserDBVO userDataListParamSlave(ParamVo paramVo) {

//        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
//        result.setConstants(new UserInfoVO(loginUser.getUserId(),loginUser.getUsername(),"",""));
        List<DataSetVO> dataSetList=new ArrayList<>();
        DataSetVO dateSet1=new DataSetVO();
        dateSet1.setDataSetId(paramVo.getTable());
        List<Map> valueList1=this.wfFormMapper.selectTableDataListParam(paramVo);

        dateSet1.setValueList(valueList1);

        dataSetList.add(dateSet1);

        result.setDataSetList(dataSetList);
        return result;
    }

    //=================3.0 jdbc 动态读取数据源=================
    @Autowired
    DruidConfig druidConfig;

    @Override
//    @Slave
//    @DataSource(value = DataSourceType.SLAVE)
    public List<TableColumn> findFieldListJdbc(String table, JdbcEntity jdbcEntity) {
//        String jdbcSql = "select column_name, (case when (is_nullable = 'no' > column_key != 'PRI') then '1' else null end) as is_required, (case when column_key = 'PRI' then '1' else '0' end) as is_pk, ordinal_position as sort, column_comment, (case when extra = 'auto_increment' then '1' else '0' end) as is_increment, column_type from information_schema.columns where table_schema = (select database()) and table_name = ('"+table+"') order by ordinal_position";
        String jdbcSql = "select column_name, (case when (is_nullable = 'no' && column_key != 'PRI') then '1' else null end) as is_required, (case when column_key = 'PRI' then '1' else '0' end) as is_pk, ordinal_position as sort, column_comment, (case when extra = 'auto_increment' then '1' else '0' end) as is_increment, column_type from information_schema.columns where table_schema = (select database()) and table_name = ('"+table+"') order by ordinal_position";
        if("postgresql".equals(jdbcEntity.getLinktype())){
            jdbcSql = "select '' AS is_increment,a.attnum AS sort,a.attname AS column_name,format_type(a.atttypid,a.atttypmod) as column_type,(case when (select count(*) from pg_constraint where conrelid = a.attrelid and conkey[1]=attnum and contype='p')>0  then '1' else '0' end) as is_pk,(case when a.attnotnull=true  then '1' else '0' end) as is_required,col_description(a.attrelid,a.attnum) as column_comment from pg_attribute a where attstattarget=-1 and attrelid = (select oid from pg_class where relname ='"+table+"')";
        }
        return JdbcUtils.jdbcTableColumn(jdbcEntity.getClassforName(),jdbcEntity.getLinkurl(),jdbcEntity.getUsername(),jdbcEntity.getPassword(),jdbcSql);
    }

    @Override
//    @Slave
//    @DataSource(value = DataSourceType.SLAVE)
    public UserDBVO userDataListParamJdbc(ParamVo paramVo, JdbcEntity jdbcEntity) {

//        String classForName = "com.mysql.cj.jdbc.Driver";
//        String url = "jdbc:mysql://127.0.0.1:3306/demo1?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8";
//        String username = "root";
//        String password = "123456";
//        String sql = "select * from person where 1=1";
        String sql = "select * from "+paramVo.getTable()+" where 1=1";

        StringBuilder builder = new StringBuilder(sql);

        Map map = paramVo.getMap();
        for(Object key:map.keySet()){
            String value = map.get(key).toString();
            System.out.println("key="+key+", vlaue="+value);
            if(!"null".equals(key) && !"null".equals(value)){
                builder.append(" and "+key+"="+map.get(key));
            }
        }


//        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserDBVO result=new UserDBVO();
//        result.setConstants(new UserInfoVO(loginUser.getUserId(),loginUser.getUsername(),"",""));
        List<DataSetVO> dataSetList=new ArrayList<>();
        DataSetVO dateSet1=new DataSetVO();
        dateSet1.setDataSetId(paramVo.getTable());
//        List<Map> valueList1=this.wfFormMapper.selectTableDataListParam(paramVo);
//        List<Map> valueList1 = JdbcUtils.jdbc(classForName,url,username,password,sql);
        List<Map> valueList1 = JdbcUtils.jdbc(jdbcEntity.getClassforName(),jdbcEntity.getLinkurl(),jdbcEntity.getUsername(),jdbcEntity.getPassword(),builder.toString());

        dateSet1.setValueList(valueList1);

        dataSetList.add(dateSet1);

        result.setDataSetList(dataSetList);
        return result;
    }

    public static void main(String[] args) {
        try {
            jd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static  void  jd()throws Exception, SQLException, InstantiationException, IllegalAccessException
//    {
//        //加载数据库驱动包
//        Class clas= Class.forName("com.mysql.cj.jdbc.Driver");
//        Driver driver= (Driver) clas.newInstance();
//        //注册驱动
//        DriverManager.registerDriver(driver);
//        System.out.println("数据库驱动加载成功");
//        //建立数据库链接
//        Connection connection= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo1?user=data_platform&password=68063bdc0cbcd52b&useUnicode=true&characterEncoding=UTF-8","root","123456");
//        //创建操作命令
//        Statement statement=connection.createStatement();
//        //执行SQL语句
//        ResultSet resultSet=statement.executeQuery("select * from person ");
//
//        //处理结果集
//        while(resultSet.next())
//        {
//            String name=resultSet.getString("id");
//            System.out.println(String.format("data_platform:name=%s",name));
//        }
//        //关闭结果集
//        if (resultSet !=null)
//        {
//            try {
//                resultSet.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        //关闭命令
//        if (statement!=null)
//        {
//            try {
//                statement.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        //关闭连接命令
//        if (connection!=null)
//        {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static  void  jd()throws Exception, SQLException, InstantiationException, IllegalAccessException
    {
        //加载数据库驱动包
        Class clas= Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver= (Driver) clas.newInstance();
        //注册驱动
        DriverManager.registerDriver(driver);
        System.out.println("数据库驱动加载成功");
        //建立数据库链接
        Connection connection= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo1?user=data_platform&password=68063bdc0cbcd52b&useUnicode=true&characterEncoding=UTF-8","root","123456");
        //创建操作命令
        Statement statement=connection.createStatement();
        //执行SQL语句
        ResultSet resultSet=statement.executeQuery("select * from person ");

        List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
        ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        //处理结果集
        while(resultSet.next())
        {
//            String name=resultSet.getString("id");
//            System.out.println(String.format("data_platform:name=%s",name));
            Map<String,Object> rowData = new HashMap<String,Object>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), resultSet.getObject(i));
            }
            list.add(rowData);
        }
        System.out.println("list============="+list);
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
