package cn.net.aichain.edge.ms.module.risk.assessment;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.foundation.os.OSUtil;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfo;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;
import cn.net.aichain.edge.ms.module.etl.dataloader.AppFirmInfoEtlService;
import cn.net.aichain.edge.ms.module.etl.dataloader.CreditScore;
import cn.net.aichain.edge.ms.module.etl.dataloader.FirmTaxDataEtlService;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Future;

@Service
public class LoadDataAndReportService {
    @Autowired
    EtlJobInfoDao etlJobInfoDao;
    @Autowired
    AppFirmInfoEtlService appFirmInfoEtlService;
    @Autowired
    FirmTaxDataEtlService firmTaxDataEtlService;

    @Async
    public Future<TreeMap<String, Object>> loadDataAndReport(String applyId, boolean force) throws Exception {
        Props props = new Props("config/etl.properties");
        String destUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String destUsername = props.getStr("destdb.main.mysql.username");
        String destPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource destDs = new DruidDataSource();
        destDs.setUrl(destUrl);
        destDs.setUsername(destUsername);
        destDs.setPassword(destPassword);
        Db destDb = DbUtil.use(destDs);

        TreeMap<String, Object> result = new TreeMap<>();
        EtlJobInfo etlJobInfo = new EtlJobInfo();
        etlJobInfo.setName("report_" + applyId);
        etlJobInfo.setContent("任务已提交。正在导入企业基本信息……");
        etlJobInfo.setReadStatus((byte)0);
        etlJobInfo.setJobStatus((byte)0);
        etlJobInfo.setCreateDate(new Date());
        etlJobInfo = etlJobInfoDao.save(etlJobInfo);
        long id = etlJobInfo.getId();

        int loadFirmInfoResult = appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyId);
        if (loadFirmInfoResult == 0 || force) {
            etlJobInfoDao.updateMiddleContentById("导入企业基本信息完成。正在导入企业税务信息……", id);
        } else if (loadFirmInfoResult == 1) {
            etlJobInfoDao.updateContentById("由于查询数据宝超时，无法导入完整的企业基本信息。任务结束。", id);
            destDs.close();
            return new AsyncResult<>(result);
        } else {
            etlJobInfoDao.updateContentById("导入企业基本信息时出现未知错误。任务结束。", id);
            destDs.close();
            return new AsyncResult<>(result);
        }
        firmTaxDataEtlService.loadByApplyId(applyId);
        etlJobInfoDao.updateMiddleContentById("导入企业税务信息完成。正在计算授信额度……", id);
        //查询是否有历史数据
        Entity score = CreditScore.getScoreResult(applyId, destDb);
        if (score == null){
            String args = applyId + " EX_DB {'HOSTNAME':'127.0.0.1','USERNAME':'root','PASSWORD':'Admin$1533','DATABASE':'credit','PORT':3306,'CHARSET':'utf8'} score_result";
            OSUtil.executePythonAndGetResult("model_main.py", args,"/python3Lib/algorithms");
            score = CreditScore.getScoreResult(applyId, destDb);
        }
        if (score != null) {
            result.put("apply_id", applyId);
            result.put("estimate_income", score.getStr("estimate_income"));
            result.put("credit_score", score.getStr("credit_score"));
            result.put("amount_credit", score.getStr("amount_credit"));
        }
        etlJobInfoDao.updateContentById(JSONUtil.parseFromMap(result).toString(), id);
        destDs.close();
        return new AsyncResult<>(result);
    }
}
