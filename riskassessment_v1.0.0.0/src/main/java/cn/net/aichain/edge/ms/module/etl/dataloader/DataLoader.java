package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public final class DataLoader {
	@Autowired
	EtlJobInfoDao etlJobInfoDao;
	@Autowired
	AppFirmInfoEtlService appFirmInfoEtlService;
	@Autowired
	FirmTaxDataEtlService firmTaxDataEtlService;

	@Scheduled(cron = "0 0 23 * * ?")
	//@Scheduled(cron = "0 0/1 * * * ?")
	public void loadFirmInfo() throws Exception {
		String applyStartDate = DateUtil.format(new Date(System.currentTimeMillis() - 86400000 * 5), "yyyyMMdd");
		String applyEndDate = DateUtil.format(new Date(System.currentTimeMillis() - 86400000 * 3), "yyyyMMdd");
		List<String> applyIdList = new ArrayList<>();
		Future<Long> re1 = appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyStartDate, applyEndDate);
		Future<Long> re2 = firmTaxDataEtlService.loadFirmTaxDataToCredit(applyStartDate, applyEndDate, applyIdList);
		Props props = new Props("config/etl.properties");
		String destUrl = props.getStr("destdb.main.mysql.jdbcUrl");
		String destUsername = props.getStr("destdb.main.mysql.username");
		String destPassword = props.getStr("destdb.main.mysql.password");
		DruidDataSource destDs = new DruidDataSource();
		destDs.setUrl(destUrl);
		destDs.setUsername(destUsername);
		destDs.setPassword(destPassword);
		Db destDb = DbUtil.use(destDs);
		re1.get();
		re2.get();
//		for (String applyId : applyIdList) {
//			CreditScore.calculateCreditScore(applyId, destDb);
//		}
		destDs.close();
	}

	public void loadFirmInfo(String today) throws Exception {
		long todayMs = DateUtil.parse(today, "yyyyMMdd").toTimestamp().getTime();
		String applyStartDate = DateUtil.format(new Date(todayMs - 86400000 * 5), "yyyyMMdd");
		String applyEndDate = DateUtil.format(new Date(todayMs - 86400000 * 3), "yyyyMMdd");
		List<String> applyIdList = new ArrayList<>();
		Future<Long> re1 = appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyStartDate, applyEndDate);
		Future<Long> re2 = firmTaxDataEtlService.loadFirmTaxDataToCredit(applyStartDate, applyEndDate, applyIdList);
		Props props = new Props("config/etl.properties");
		String destUrl = props.getStr("destdb.main.mysql.jdbcUrl");
		String destUsername = props.getStr("destdb.main.mysql.username");
		String destPassword = props.getStr("destdb.main.mysql.password");
		DruidDataSource destDs = new DruidDataSource();
		destDs.setUrl(destUrl);
		destDs.setUsername(destUsername);
		destDs.setPassword(destPassword);
		Db destDb = DbUtil.use(destDs);
		re1.get();
		re2.get();
		for (String applyId : applyIdList) {
			CreditScore.calculateCreditScore(applyId, destDb);
		}
		destDs.close();
	}

	public static ArrayList<String> taxUncompletedApplyIds;
	void DataLoader() {
		taxUncompletedApplyIds = new ArrayList<>();
	}

	public static void main(String[] args) {
        long todayMs = DateUtil.parse("20170409", "yyyyMMdd").toTimestamp().getTime();
        String applyStartDate = DateUtil.format(new Date(todayMs - 86400000 * 5), "yyyyMMdd");
        System.out.println(applyStartDate);
    }
}
