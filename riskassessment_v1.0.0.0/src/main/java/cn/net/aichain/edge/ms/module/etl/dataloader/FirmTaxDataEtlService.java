package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfo;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;
import cn.net.aichain.edge.ms.jpa.firm.*;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/***
 * ETL
 * 税务数据包括：
 * 1、企业的月销售数据
 * 2、下游企业的年销售数据
 */
@Service
public class FirmTaxDataEtlService {
    @Autowired
    FirmMonthsOutputDao firmMonthsOutputDao;
    @Autowired
    DownstreamYearOutputDao downstreamYearOutputDao;
    @Autowired
    EtlJobInfoDao etlJobInfoDao;
    // 按 申请id 将 税务数据 存到 credit 库
    public boolean loadByApplyId(String applyId)throws Exception{
        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        String taxUrl = props.getStr("srcdb.taxs.mysql.jdbcUrl");
        String taxUsername = props.getStr("srcdb.taxs.mysql.username");
        String taxPassword = props.getStr("srcdb.taxs.mysql.password");
        DruidDataSource taxDs = new DruidDataSource();
        taxDs.setUrl(taxUrl);
        taxDs.setUsername(taxUsername);
        taxDs.setPassword(taxPassword);
        Db taxDb = DbUtil.use(taxDs);


        // 根据apply id 获得 纳税人识别号 和 申请时间
            String nsrsbh="";
            String applyTime="";
            String fApply="SELECT * FROM f_apply WHERE APPLY_ID=?";
            Entity firmApply=osDb.queryOne(fApply,applyId);
            if (firmApply == null ) {
                osDs.close();
                taxDs.close();
                return true;
            }
            nsrsbh=firmApply.getStr("NSRSBH");
            applyTime=firmApply.getStr("APPLY_TIME");
            // 根据applyId 计算税务数据
            // code
            int size_1=0;
            int size_2=0;
            List<DownstreamYearOutput> downstreamYearOutputs=downstreamYearOutputDao.findByApplyId(applyId);
            if (downstreamYearOutputs.size()==0)  {
                saveDownstream(applyId,nsrsbh,applyTime,taxDb);
            }
            List<FirmMonthsOutput> firmMonthsOutputs=firmMonthsOutputDao.findByApplyId(applyId);
            if (firmMonthsOutputs.size()==0) {
                size_2=saveFirmMonthsOutput(applyId,nsrsbh,applyTime,taxDb);
            }
            osDs.close();
            taxDs.close();
            //判断数据是否已经存在
            return downstreamYearOutputs.size()==0 && firmMonthsOutputs.size()==0;
    }

    // 供etl使用 按 申请id 更新税务数据（先delete 再 insert）
    public boolean etlLoadByApplyId(String applyId) throws Exception{
        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        String taxUrl = props.getStr("srcdb.taxs.mysql.jdbcUrl");
        String taxUsername = props.getStr("srcdb.taxs.mysql.username");
        String taxPassword = props.getStr("srcdb.taxs.mysql.password");
        DruidDataSource taxDs = new DruidDataSource();
        taxDs.setUrl(taxUrl);
        taxDs.setUsername(taxUsername);
        taxDs.setPassword(taxPassword);
        Db taxDb = DbUtil.use(taxDs);

        // 根据apply id 获得 纳税人识别号 和 申请时间
        String nsrsbh = "";
        String applyTime = "";
        String fApply = "SELECT * FROM f_apply WHERE APPLY_ID=?";
        Entity firmApply = osDb.queryOne(fApply, applyId);
        if (firmApply == null) {
            osDs.close();
            taxDs.close();
            return true;
        }
        nsrsbh = firmApply.getStr("NSRSBH");
        applyTime = firmApply.getStr("APPLY_TIME");
        // 根据applyId 计算税务数据
        int size_1 = 0;
        int size_2 = 0;
        // 先删除下游企业税务数据 再更新
        List<DownstreamYearOutput> downstreamYearOutputs = downstreamYearOutputDao.findByApplyId(applyId);
        if (downstreamYearOutputs.size() != 0){
            for (DownstreamYearOutput downstreamYearOutput : downstreamYearOutputs){
                downstreamYearOutputDao.delete(downstreamYearOutput);
            }
        }
        size_1 = saveDownstream(applyId, nsrsbh, applyTime, taxDb);
        LogFactory.get().info("导入完毕，成功更新了申请id为" + applyId + "的下游企业税务数据。数据条数为"+ size_1);
        // 先删除企业月销售税务数据 再更新
        List<FirmMonthsOutput> firmMonthsOutputs = firmMonthsOutputDao.findByApplyId(applyId);
        if (firmMonthsOutputs.size() != 0){
            for (FirmMonthsOutput firmMonthsOutput : firmMonthsOutputs){
                firmMonthsOutputDao.delete(firmMonthsOutput);
            }
        }
        size_2 = saveFirmMonthsOutput(applyId, nsrsbh, applyTime, taxDb);
        LogFactory.get().info("导入完毕，成功更新了申请id为" + applyId + "的企业月销售税务数据。数据条数为"+ size_2);
        osDs.close();
        taxDs.close();
        return downstreamYearOutputs.size() == 0 && firmMonthsOutputs.size() == 0;
    }
    // 按 申请时间段 将 税务数据 存到 credit 库
    @Async
    public Future<Long> loadFirmTaxDataToCredit(String applyStartDate, String applyEndDate, List<String> applyIdList) {
        LogFactory.get().info("开始导入企业税务信息，申请时间范围为" + applyStartDate + "至" + applyEndDate);
        EtlJobInfo etlJobInfo = new EtlJobInfo();
        etlJobInfo.setName("loadFirmTaxDataToCredit_" + applyStartDate + "_" + applyEndDate);
        etlJobInfo.setContent("任务正在执行中。");
        etlJobInfo.setReadStatus((byte)0);
        etlJobInfo.setJobStatus((byte)0);
        etlJobInfo.setCreateDate(new Date());
        etlJobInfo = etlJobInfoDao.save(etlJobInfo);
        long id = etlJobInfo.getId();

        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        String taxUrl = props.getStr("srcdb.taxs.mysql.jdbcUrl");
        String taxUsername = props.getStr("srcdb.taxs.mysql.username");
        String taxPassword = props.getStr("srcdb.taxs.mysql.password");
        DruidDataSource taxDs = new DruidDataSource();
        taxDs.setUrl(taxUrl);
        taxDs.setUsername(taxUsername);
        taxDs.setPassword(taxPassword);
        Db taxDb = DbUtil.use(taxDs);


        long count = 0;
        try {
            List<Entity> applyInfoList = osDb.query("SELECT APPLY_ID, ENTERPRISE_NAME, APPLY_TIME,NSRSBH FROM f_apply WHERE TO_DAYS(APPLY_TIME) BETWEEN TO_DAYS(?) AND TO_DAYS(?)", applyStartDate, applyEndDate);
            for (Entity applyInfo : applyInfoList) {
                String applyId=applyInfo.getStr("APPLY_ID");
                String nsrsbh=applyInfo.getStr("NSRSBH");
                String applyTime=applyInfo.getStr("APPLY_TIME");
                // 根据applyId 计算税务数据
                int size_1 = 0;
                int size_2 = 0;
                // 先删除下游企业税务数据 再更新
                List<DownstreamYearOutput> downstreamYearOutputs = downstreamYearOutputDao.findByApplyId(applyId);
                if (downstreamYearOutputs.size() != 0){
                    for (DownstreamYearOutput downstreamYearOutput : downstreamYearOutputs){
                        downstreamYearOutputDao.delete(downstreamYearOutput);
                    }
                }
                size_1 = saveDownstream(applyId, nsrsbh, applyTime, taxDb);
                LogFactory.get().info("导入完毕，成功更新了申请id为" + applyId + "的下游企业税务数据。数据条数为"+ size_1);
                //先删除企业月销售税务数据 再更新
                List<FirmMonthsOutput> firmMonthsOutputs = firmMonthsOutputDao.findByApplyId(applyId);
                if (firmMonthsOutputs.size() != 0){
                    for (FirmMonthsOutput firmMonthsOutput : firmMonthsOutputs){
                        firmMonthsOutputDao.delete(firmMonthsOutput);
                    }
                }
                size_2 = saveFirmMonthsOutput(applyId, nsrsbh, applyTime, taxDb);
                LogFactory.get().info("导入完毕，成功更新了申请id为" + applyId + "的企业月销售税务数据。数据条数为"+ size_2);
                count++;
                applyIdList.add(applyId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        etlJobInfoDao.updateContentById("任务已完成，成功更新了" + count + "家企业的税务数据。", id);
        osDs.close();
        taxDs.close();
        LogFactory.get().info("导入完毕，成功更新了" + count + "家企业的税务信息。");
        return new AsyncResult<>(count);
    }

    // 按 税务数据的提交时间 将 税务数据 存到credit 库
    public void loadByInvoiceDate(String updateDate) throws Exception{

        Props props = new Props("config/etl.properties");
        String taxUrl = props.getStr("srcdb.taxs.mysql.jdbcUrl");
        String taxUsername = props.getStr("srcdb.taxs.mysql.username");
        String taxPassword = props.getStr("srcdb.taxs.mysql.password");
        DruidDataSource taxDs = new DruidDataSource();
        taxDs.setUrl(taxUrl);
        taxDs.setUsername(taxUsername);
        taxDs.setPassword(taxPassword);
        Db taxDb = DbUtil.use(taxDs);
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);
        // 获得某天的税务数据中 去重后的所有 纳税人识别号
        String query="SELECT DISTINCT a.xfsbh from invoice_structure a\n" +
                "WHERE DATE_FORMAT(a.last_operation_time,\"%Y-%m-%d\")=DATE_FORMAT(?,\"%Y-%m-%d\")";
        List<Entity> rows = taxDb.query(query,updateDate);
        for (Entity row:rows) {
            // 通过纳税人识别号 在申请表中 找到对应的申请记录 获得 applyID 和 applyTime
            String queryApplyId = "SELECT a.APPLY_ID,a.APPLY_TIME from f_apply a WHERE a.NSRSBH=?";
            String nsrsbh=row.getStr("xfsbh");
            List<Entity> applyInfoList = osDb.query(queryApplyId,nsrsbh);
            for (Entity applyInfo : applyInfoList){
                String applyId,applyTime;
                applyId=applyInfo.getStr("APPLY_ID");
                applyTime=applyInfo.getStr("APPLY_TIME");
                // 查询是否存在 若存在 丢弃数据 放入新数据进去
                List<DownstreamYearOutput> downstreamYearOutputs=downstreamYearOutputDao.findByApplyId(applyId);
                System.out.println(applyId+" : "+downstreamYearOutputs.size());
                // 将税务数据重新计算 存到 credit 数据库中
                //saveDownstream(applyId,nsrsbh,applyTime,taxDb);
                //saveFirmMonthsOutput(applyId,nsrsbh,applyTime,taxDb);
            }
        }
        System.out.println(rows.size());
        osDs.close();
        taxDs.close();
    }

    // 将 下游企业的年销售数据 保存到数据库
    public int saveDownstream(final String applyId,String nsrsbh,String applyTime,Db taxDb) throws Exception{
        List<Entity> downstreams = CreditScore.getDownstream(applyId,nsrsbh,applyTime,taxDb);

        for(Entity downstream : downstreams)
        {
            String apply_id = downstream.getStr("apply_id");
            String years = downstream.getStr("years");
            String downstream_firm_buyer = downstream.getStr("downstream_firm_buyer");
            String downstream_firm_buyer_ids = downstream.getStr("downstream_firm_buyer_ids");
            BigDecimal invalid_invoice_amt = downstream.getBigDecimal("invalid_invoice_amt");
            BigDecimal valid_invoice_amt = downstream.getBigDecimal("valid_invoice_amt");
            BigDecimal red_invoice_amt = downstream.getBigDecimal("red_invoice_amt");
            BigDecimal transaction_amount = downstream.getBigDecimal("transaction_amount");
            try {
                DownstreamYearOutput row=new DownstreamYearOutput();
                row.setApplyId(apply_id);
                row.setYears(years);
                row.setDownstreamFirmBuyer(downstream_firm_buyer);
                row.setDownstreamFirmBuyerIds(downstream_firm_buyer_ids);
                row.setInvalidInvoiceAmt(invalid_invoice_amt);
                row.setValidInvoiceAmt(valid_invoice_amt);
                row.setRedInvoiceAmt(red_invoice_amt);
                row.setTransactionAmount(transaction_amount);
                downstreamYearOutputDao.save(row);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downstreams.size();
    }
    // 将 企业的月销售数据 保存到数据库
    public int saveFirmMonthsOutput(final String applyId,String nsrsbh,String applyTime,Db taxDb) throws Exception{
        List<Entity> firmMonths= CreditScore.getFirmMonthsOutput(applyId,nsrsbh,applyTime,taxDb);
        for(Entity firmMonth : firmMonths){
            String apply_id=firmMonth.getStr("apply_id");
            String months=firmMonth.getStr("months");
            String output_quarter=firmMonth.getStr("output_quarter");
            String seller=firmMonth.getStr("seller");
            String seller_taxpayer_id=firmMonth.getStr("seller_taxpayer_id");

            String downstream_firm_buyer_group=firmMonth.getStr("downstream_firm_buyer_group");
            String downstream_firm_buyer_ids=firmMonth.getStr("downstream_firm_buyer_ids");
            long downstream_firm_buyer_num=firmMonth.getLong("downstream_firm_buyer_num");
            String type_of_invoice=firmMonth.getStr("type_of_invoice");
            String type_of_taxrate=firmMonth.getStr("type_of_taxrate");

            long valid_invoice_num=firmMonth.getLong("valid_invoice_num");
            long invalid_invoice_num=firmMonth.getLong("invalid_invoice_num");
            long valid_red_invoice_num=firmMonth.getLong("valid_red_invoice_num");
            long total_invoice_num=firmMonth.getLong("total_invoice_num");
            BigDecimal invalid_invoice_amt=firmMonth.getBigDecimal("invalid_invoice_amt");

            BigDecimal valid_invoice_amt=firmMonth.getBigDecimal("valid_invoice_amt");
            BigDecimal red_invoice_amt=firmMonth.getBigDecimal("red_invoice_amt");
            BigDecimal business_income=firmMonth.getBigDecimal("business_income");
            BigDecimal added_value_tax_amt=firmMonth.getBigDecimal("added_value_tax_amt");
            BigDecimal total_tax_amt=firmMonth.getBigDecimal("total_tax_amt");
            try{
                FirmMonthsOutput firmMonthsOutput=new FirmMonthsOutput();

                firmMonthsOutput.setApplyId(apply_id);
                firmMonthsOutput.setMonths(months);
                firmMonthsOutput.setOutputQuarter(output_quarter);
                firmMonthsOutput.setSeller(seller);
                firmMonthsOutput.setSellerTaxpayerId(seller_taxpayer_id);

                firmMonthsOutput.setDownstreamFirmBuyerGroup(downstream_firm_buyer_group);
                firmMonthsOutput.setDownstreamFirmBuyerIds(downstream_firm_buyer_ids);
                firmMonthsOutput.setDownstreamFirmBuyerNum(downstream_firm_buyer_num);
                firmMonthsOutput.setTypeOfInvoice(type_of_invoice);
                firmMonthsOutput.setTypeOfTaxrate(type_of_taxrate);

                firmMonthsOutput.setValidInvoiceNum(valid_invoice_num);
                firmMonthsOutput.setInvalidInvoiceNum(invalid_invoice_num);
                firmMonthsOutput.setValidRedInvoiceNum(valid_red_invoice_num);
                firmMonthsOutput.setTotalInvoiceNum(total_invoice_num);
                firmMonthsOutput.setInvalidInvoiceAmt(invalid_invoice_amt);

                firmMonthsOutput.setValidInvoiceAmt(valid_invoice_amt);
                firmMonthsOutput.setRedInvoiceAmt(red_invoice_amt);
                firmMonthsOutput.setBusinessIncome(business_income);
                firmMonthsOutput.setAddedValueTaxAmt(added_value_tax_amt);
                firmMonthsOutput.setTotalTaxAmt(total_tax_amt);

                firmMonthsOutputDao.save(firmMonthsOutput);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return firmMonths.size();
    }
}
