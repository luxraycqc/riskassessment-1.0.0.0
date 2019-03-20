package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.utils.FileUtil;
import com.alibaba.druid.pool.DruidDataSource;

import java.util.List;

public class SqlProcedureIniter {
    public static void createTables() throws Exception{
        Props props = new Props("config/etl.properties");
        String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String creditUsername = props.getStr("destdb.main.mysql.username");
        String creditPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource creditDs = new DruidDataSource();
        creditDs.setUrl(creditUrl);
        creditDs.setUsername(creditUsername);
        creditDs.setPassword(creditPassword);
        Db creditDb = DbUtil.use(creditDs);
        String sql1 = FileUtil.readFileContentAsString("sql/create_table_firm_profile_params.sql");
        if (sql1 != null) creditDb.execute(sql1);
        String sql2 = FileUtil.readFileContentAsString("sql/create_table_score_result.sql");
        if (sql2 != null) creditDb.execute(sql2);
        String sql3 = FileUtil.readFileContentAsString("sql/create_table_mid_every_counties_CAGR.sql");
        if (sql3 != null) creditDb.execute(sql3);
        Entity countEntity = creditDb.queryOne("SELECT count(*) AS count FROM mid_every_counties_CAGR");
        long count = countEntity.getLong("count");
        if (count == 0) {
            List<String> sql4 = FileUtil.readFileContentAsStringList("sql/insert_into_mid_every_counties_CAGR.sql");
            if (sql4 != null) for (String insert : sql4) {
                creditDb.execute(insert);
            }
        }
        creditDs.close();
    }

}
