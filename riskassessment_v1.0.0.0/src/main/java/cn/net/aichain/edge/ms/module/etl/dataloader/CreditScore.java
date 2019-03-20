package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.foundation.os.OSUtil;
import cn.net.aichain.edge.ms.utils.PlaceNameUtil;
import com.alibaba.druid.pool.DruidDataSource;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public final class CreditScore {
    //从 tax 库中统计 获得downstream_year_output 的数据
    public static List<Entity> getDownstream(String applyId,String nsrsbh,String applyTime,Db taxDb) throws Exception{
        //根据 applyId 纳税人识别号 和申请时间 获得 downstream_year_output 表的数据
        String downstreamYearOutput="SELECT ? AS apply_id," +
                "  DATE_FORMAT(kprq,'%Y') AS years, \n" +
                "  a.gfmc AS \"downstream_firm_buyer\",\n" +
                "  GROUP_CONCAT(DISTINCT a.gfsbh)  AS \"downstream_firm_buyer_ids\",\n" +
                "  ROUND(SUM(CASE zfbz WHEN \"Y\" THEN je ELSE 0 END),2) AS \"invalid_invoice_amt\",\n" +
                "  ROUND(SUM(CASE zfbz WHEN \"N\" THEN CASE WHEN je>0 THEN  je  END ELSE 0 END),2) AS \"valid_invoice_amt\",\n" +
                "  ROUND(SUM(CASE WHEN je<0 THEN  CASE zfbz WHEN \"N\" THEN je END ELSE 0 END),2) AS \"red_invoice_amt\" ,\n" +
                "  ROUND(SUM(CASE zfbz WHEN \"N\" THEN je ELSE 0 END),2) AS \"transaction_amount\" \n" +
                "FROM  `invoice_structure` a \n" +
                "WHERE  a.xfsbh =? " +
                "AND a.kprq <= DATE_FORMAT(?,\"%Y-%m-%d\")\n" +
                "AND a.type = \"XXFP\"\n" +
                "GROUP BY downstream_firm_buyer,years";
             List<Entity> downstream = taxDb.query(downstreamYearOutput,applyId,nsrsbh,applyTime);
            return downstream;
    }

    //从 tax 库中统计 获得firm_months_output 的数据
    public static List<Entity> getFirmMonthsOutput(String applyId,String nsrsbh,String applyTime,Db taxDb) throws Exception{
        //根据 applyId 纳税人识别号 和 申请时间 获得 firm_months_output 表的数据
        String firmMonthsOutput="SELECT  ? AS apply_id, " +
                "-- GROUP_CONCAT(DISTINCT b.APPLY_ID) AS \"applyId\",\n" +
                "  DATE_FORMAT(kprq,'%Y-%m') AS months,\n" +
                " GROUP_CONCAT( DISTINCT QUARTER(a.kprq) )AS output_quarter,\n" +
                "GROUP_CONCAT(DISTINCT a.xfmc)  AS \"seller\",\n" +
                "GROUP_CONCAT( DISTINCT a.xfsbh)  AS \"seller_taxpayer_id\",\n" +
                "-- 税务信息\n" +
                "GROUP_CONCAT(gfmc) AS \"downstream_firm_buyer_group\",\n" +
                "GROUP_CONCAT(DISTINCT gfsbh) AS \"downstream_firm_buyer_ids\",\n" +
                "COUNT(DISTINCT gfmc) AS \"downstream_firm_buyer_num\",\n" +
                "GROUP_CONCAT(DISTINCT fp_lb) AS \"type_of_invoice\",\n" +
                "GROUP_CONCAT(slv) AS \"type_of_taxrate\", ###要去掉\n" +
                "COUNT(CASE zfbz WHEN \"N\" THEN CASE WHEN je>0 THEN  kprq END END) AS \"valid_invoice_num\", #有效、且为正\n" +
                "COUNT(CASE zfbz WHEN \"Y\" THEN kprq END) AS \"invalid_invoice_num\",\n" +
                "COUNT(CASE WHEN je<0 THEN  CASE zfbz WHEN \"N\" THEN kprq END END ) AS \"valid_red_invoice_num\",\n" +
                "COUNT(zfbz) AS \"total_invoice_num\",\n" +
                "ROUND(SUM(CASE zfbz WHEN \"Y\" THEN je ELSE 0 END),2) AS \"invalid_invoice_amt\",\n" +
                "ROUND(SUM(CASE zfbz WHEN \"N\" THEN CASE WHEN je>0 THEN  je  END ELSE 0 END),2) AS \"valid_invoice_amt\",  #有效、且为正\n" +
                "ROUND(SUM(CASE WHEN je<0 THEN  CASE zfbz WHEN \"N\" THEN je END ELSE 0 END),2) AS \"red_invoice_amt\" ,\n" +
                "ROUND(SUM(CASE zfbz WHEN \"N\" THEN je ELSE 0 END),2) AS \"business_income\" , #营业收入：有效金额 + 红冲金额\n" +
                "ROUND(SUM(CASE fp_lb WHEN \"S\" THEN CASE zfbz WHEN \"N\" THEN se END ELSE 0 END),2) AS \"added_value_tax_amt\",\n" +
                "ROUND(SUM(CASE zfbz WHEN \"N\" THEN se END),2) AS \"total_tax_amt\"  #无作废\n" +
                "FROM invoice_structure a\n" +
                "WHERE a.xfsbh= ? " +
                " AND a.kprq <= DATE_FORMAT(?,\"%Y-%m-%d\")\n" +
                " AND a.type = \"XXFP\"\n" +
                " GROUP BY DATE_FORMAT(kprq,'%Y-%m')";
            List<Entity> monthsOutput=taxDb.query(firmMonthsOutput,applyId,nsrsbh,applyTime);
            return  monthsOutput;
    }

    // 根据 city 和 country 获得 cagr 参数
    public static double getCountyCagr(String city,String county, Db creditDb){
        double county_cagr=0.0;
        try {
            Entity res= creditDb.queryOne("SELECT COUNTY_CAGR FROM mid_every_counties_CAGR " +
                    "WHERE CITY=? AND DISTRICT=?", city, county);
            if (res!=null) county_cagr=res.getDouble("COUNTY_CAGR");
        }catch (Exception e){
            e.printStackTrace();
        }
        return county_cagr;
    }

    // 根据企业名称查询申请记录
    public static List<Entity> getFirmApplyId(String firmName) throws Exception{
        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");

        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        List<Entity> result=osDb.query("SELECT APPLY_ID AS apply_id,ENTERPRISE_NAME AS firm_name, APPLY_TIME AS apply_time\n" +
                "FROM f_apply\n" +
                "WHERE ENTERPRISE_NAME=?", firmName);
        osDs.close();
        return result;

    }

    // 根据申请id 查询结果
    public static Entity getScoreResult(String applyId, Db destDb) throws Exception{
        Entity result=destDb.queryOne("SELECT *\n" +
                "FROM score_result\n" +
                "WHERE apply_id=?",applyId);
        return result;
    }
    // 根据申请id 调用python算法 执行出结果（有结果则不执行）
    public static Entity calculateCreditScore(String applyId, Db destDb) throws Exception {
        // 先判断在数据库中是否已经有该条数据了
        // 若有 则不执行python 若没有 执行python
        Entity result=CreditScore.getScoreResult(applyId, destDb);
        if (result == null){
            String args = applyId + " EX_DB {'HOSTNAME':'127.0.0.1','USERNAME':'root','PASSWORD':'Admin$1533','DATABASE':'credit','PORT':3306,'CHARSET':'utf8'} score_result";
            List<String> res=OSUtil.executePythonAndGetResult("model_main.py", args,"/python3Lib/algorithms");
            result=CreditScore.getScoreResult(applyId, destDb);
        }
        return result;
    }
}
