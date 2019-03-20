package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfo;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;
import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfo;
import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfoDao;
import cn.net.aichain.edge.ms.utils.PlaceNameUtil;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class AppFirmInfoEtlService {
    @Autowired
    AppFirmInfoDao appFirmInfoDao;
    @Autowired
    EtlJobInfoDao etlJobInfoDao;
    @Async
    public Future<Long> loadFirmInfoFromOsToCredit(String applyStartDate, String applyEndDate) {
        LogFactory.get().info("开始导入企业基本信息，申请时间范围为" + applyStartDate + "至" + applyEndDate);
        EtlJobInfo etlJobInfo = new EtlJobInfo();
        etlJobInfo.setName("loadFirmInfoFromOsToCredit_" + applyStartDate + "_" +applyEndDate);
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

        String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String creditUsername = props.getStr("destdb.main.mysql.username");
        String creditPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource creditDs = new DruidDataSource();
        creditDs.setUrl(creditUrl);
        creditDs.setUsername(creditUsername);
        creditDs.setPassword(creditPassword);
        Db creditDb = DbUtil.use(creditDs);

        long count = 0, fail = 0;
        try {
            List<Entity> applyInfoList = osDb.query("SELECT APPLY_ID, ENTERPRISE_NAME, APPLY_TIME, APPLY_CREDIT_LIMIT FROM f_apply WHERE TO_DAYS(APPLY_TIME) BETWEEN TO_DAYS(?) AND TO_DAYS(?)", applyStartDate, applyEndDate);
            for (Entity applyInfo : applyInfoList) {
                AppFirmInfo foundAppFirmInfo = appFirmInfoDao.findByApplyId(applyInfo.getStr("APPLY_ID"));
                if (foundAppFirmInfo != null) {
                    continue; // 避免重复插入
                }
                AppFirmInfo appFirmInfo = new AppFirmInfo();
                appFirmInfo.setApplyId(applyInfo.getStr("APPLY_ID"));
                appFirmInfo.setFirmName(applyInfo.getStr("ENTERPRISE_NAME"));
                appFirmInfo.setApplyDate(applyInfo.getDate("APPLY_TIME"));
                appFirmInfo.setApplyCreditLimit(applyInfo.getStr("APPLY_CREDIT_LIMIT"));
                Entity firmInfo = osDb.queryOne("SELECT business_no, business_date, business_addr, business_scope FROM f_qyxx WHERE record_id=?", appFirmInfo.getApplyId());
                if (firmInfo == null) continue;
                appFirmInfo.setBusinessNo(firmInfo.getStr("business_no"));
                appFirmInfo.setBusinessDate(firmInfo.getDate("business_date"));
                appFirmInfo.setBusinessAddress(firmInfo.getStr("business_addr"));
                appFirmInfo.setBusinessScope(firmInfo.getStr("business_scope"));
                Date businessDate = DateUtil.parse(firmInfo.getStr("business_date"));
                Date applyDate = DateUtil.parse(applyInfo.getStr("APPLY_TIME"));
                long registeredYear = DateUtil.betweenYear(businessDate, applyDate, false);
                appFirmInfo.setRegisteredYear((int)registeredYear);
                String[] cityCountyName = PlaceNameUtil.extractCityCountyName(appFirmInfo.getBusinessAddress());
                appFirmInfo.setCity(cityCountyName[0]);
                appFirmInfo.setCounty(cityCountyName[1]);
                // 查询数据宝获得注册资本
                int sjb = AppFirmInfoLoader.loadFromSJB(appFirmInfo);
                if (sjb != 0) {
                    LogFactory.get().warn(appFirmInfo.getFirmName() + "查询数据宝获取注册资本失败！");
                    fail ++;
                }
                if (cityCountyName[0] != null && cityCountyName[1] != null) {
                    appFirmInfo.setCounty_CAGR(CreditScore.getCountyCagr(cityCountyName[0], cityCountyName[1], creditDb));
                }
                appFirmInfoDao.save(appFirmInfo);
                count++;
            }
        } catch (Exception e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
        String sjbFailInfo = "";
        if (fail > 0) sjbFailInfo = "但是有" + fail + "家企业的注册资本无法从数据宝获取，请查看后台日志。";
        etlJobInfoDao.updateContentById("任务已完成，成功更新了" + count + "家企业的基本信息。" + sjbFailInfo, id);
        LogFactory.get().info("导入完毕，成功更新了" + count + "家企业的基本信息。" + sjbFailInfo);
        osDs.close();
        creditDs.close();
        return new AsyncResult<>(count);
    }

    public int loadFirmInfoFromOsToCredit(String applyId) {
        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String creditUsername = props.getStr("destdb.main.mysql.username");
        String creditPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource creditDs = new DruidDataSource();
        creditDs.setUrl(creditUrl);
        creditDs.setUsername(creditUsername);
        creditDs.setPassword(creditPassword);
        Db creditDb = DbUtil.use(creditDs);

        try {
            Entity applyInfo = osDb.queryOne("SELECT APPLY_ID, ENTERPRISE_NAME, APPLY_TIME , APPLY_CREDIT_LIMIT FROM f_apply WHERE APPLY_ID=?", applyId);
            if (applyInfo == null) {
                creditDs.close();
                osDs.close();
                return -1;
            }
            String firmName = applyInfo.getStr("ENTERPRISE_NAME");
            Date applyDate = applyInfo.getDate("APPLY_TIME");
            String applyCreditLimit=applyInfo.getStr("APPLY_CREDIT_LIMIT");
            Entity firmInfo = osDb.queryOne("SELECT business_no, business_date, business_addr, business_scope FROM f_qyxx WHERE record_id=?", applyId);
            if (firmInfo == null) {
                creditDs.close();
                osDs.close();
                return -2;
            }
            String businessNo = firmInfo.getStr("business_no");
            Date businessDate = firmInfo.getDate("business_date");
            String businessAddress = firmInfo.getStr("business_addr");
            String businessScope = firmInfo.getStr("business_scope");
            int registeredYear = (int)(DateUtil.betweenYear(businessDate, applyDate, false));
            String[] cityCountyName = PlaceNameUtil.extractCityCountyName(businessAddress);
            String city = cityCountyName[0];
            String county = cityCountyName[1];
            AppFirmInfo appFirmInfoTemp = new AppFirmInfo();
            appFirmInfoTemp.setFirmName(firmName);
            // 查询数据宝获得注册资本
            int sjb = AppFirmInfoLoader.loadFromSJB(appFirmInfoTemp);
            String registeredCapital = appFirmInfoTemp.getRegisteredCapital();
            double countyCAGR = 0.0;
            if (city != null && county != null) {
                countyCAGR = CreditScore.getCountyCagr(city, county, creditDb);
            }
            creditDb.execute("UPDATE app_firm_info SET firm_name=?, apply_date=?, business_no=?, business_date=?, " +
                    "business_address=?, business_scope=?, registered_year=?, city=?, county=?, registered_capital=?, county_cagr=? " +
                    "WHERE apply_id=?", firmName, applyDate, businessNo, businessDate, businessAddress, businessScope, registeredYear,
                    city, county, registeredCapital, countyCAGR, applyId
                    );
            AppFirmInfo foundAppFirmInfo = appFirmInfoDao.findByApplyId(applyId);
            if (foundAppFirmInfo == null) {
                AppFirmInfo appFirmInfo = new AppFirmInfo();
                appFirmInfo.setApplyId(applyId);
                appFirmInfo.setFirmName(firmName);
                appFirmInfo.setApplyDate(applyDate);
                appFirmInfo.setBusinessNo(businessNo);
                appFirmInfo.setBusinessDate(businessDate);
                appFirmInfo.setBusinessAddress(businessAddress);
                appFirmInfo.setBusinessScope(businessScope);
                appFirmInfo.setRegisteredYear(registeredYear);
                appFirmInfo.setCity(city);
                appFirmInfo.setCounty(county);
                appFirmInfo.setRegisteredCapital(registeredCapital);
                appFirmInfo.setCounty_CAGR(countyCAGR);
                appFirmInfo.setApplyCreditLimit(applyCreditLimit);
                appFirmInfoDao.save(appFirmInfo);
            }
            if (sjb != 0) {
                creditDs.close();
                osDs.close();
                return 1;
            } else {
                creditDs.close();
                osDs.close();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogFactory.get().error(e);
            creditDs.close();
            osDs.close();
            return -3;
        }
    }

    public List<String> showAvailableApplyIds() throws Exception {
        Props props = new Props("config/etl.properties");
        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
        String osUsername = props.getStr("srcdb.os.mysql.username");
        String osPassword = props.getStr("srcdb.os.mysql.password");
        DruidDataSource osDs = new DruidDataSource();
        osDs.setUrl(osUrl);
        osDs.setUsername(osUsername);
        osDs.setPassword(osPassword);
        Db osDb = DbUtil.use(osDs);

        List<Entity> entities = osDb.query("select APPLY_ID, ENTERPRISE_NAME from f_apply limit 20");
        List<String> applyIds = new ArrayList<>();
        for (Entity entity : entities) {
            applyIds.add(entity.getStr("APPLY_ID") + "|" + entity.getStr("ENTERPRISE_NAME"));
        }
        osDs.close();
        return applyIds;
    }
}
