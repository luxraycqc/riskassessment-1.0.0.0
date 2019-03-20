package cn.net.aichain.edge.ms.module.risk.assessment;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.foundation.os.OSUtil;
import cn.net.aichain.edge.ms.jpa.firm.*;
import cn.net.aichain.edge.ms.message.WebMessage;
import cn.net.aichain.edge.ms.module.etl.dataloader.*;
import com.alibaba.druid.pool.DruidDataSource;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RestController
@RequestMapping(value = { "/riskAssessment" })
@Api(tags = "0.风控报告结果")
public final class RiskAssessmentRest {
    @Autowired
    AppFirmInfoDao appFirmInfoDao;
    @Autowired
    FirmMonthsOutputDao firmMonthsOutputDao;
    @Autowired
    RiskMgrService riskMgrService;
    @Autowired
    FirmTaxDataEtlService firmTaxDataEtlService;
    @Autowired
    LoadDataAndReportService loadDataAndReportService;
    @Autowired
    AppFirmInfoEtlService appFirmInfoEtlService;
    @Autowired
    AccessRequirementEtlService accessRequirementEtlService;
    @Autowired
    AccessRequirementDao accessRequirementDao;
    @Autowired
    DataLoader dataLoader;
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId", defaultValue = "0973D35C50DB4972858E97AF5D79CD6B", value = "企业的申请id", required = true)
    })
    public WebMessage report(final String applyId) throws Exception{
        final WebMessage msg = new WebMessage();
        // 数据库连接池
        Props props = new Props("config/etl.properties");
        String destUrl = props.getStr("destdb.main.mysql.jdbcUrl");
        String destUsername = props.getStr("destdb.main.mysql.username");
        String destPassword = props.getStr("destdb.main.mysql.password");
        DruidDataSource destDs = new DruidDataSource();
        destDs.setUrl(destUrl);
        destDs.setUsername(destUsername);
        destDs.setPassword(destPassword);
        Db destDb = DbUtil.use(destDs);

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
        //字段说明
        final WebMessage explanation = new WebMessage();
        explanation.msg.put("firmName","企业名称");
        explanation.msg.put("uniSocialCreditCode","社会统一信用代码");
        explanation.msg.put("address","地址");
        explanation.msg.put("businessStart","成立日期");
        explanation.msg.put("registeredCapital","注册资本(万元)");
        explanation.msg.put("creditScore","信用评分");
        explanation.msg.put("lastYearSalesAmount","上年度销售额");
        explanation.msg.put("creditAmount","建议授信金额(万元)");
        explanation.msg.put("applyCreditAmount","申请授信金额(万元)");
        explanation.msg.put("firmMonthSalesAmount","月销售额统计");
        explanation.msg.put("concentrationRatio","主营商品集中度"); //？？？
        explanation.msg.put("firmPortraitScore","企业画像"); //???
        explanation.msg.put("mainProduct","主营商品销售额");//???
        explanation.msg.put("top10DownstreamSalesAmounts","十大下游企业开票额");//???
        explanation.msg.put("sizeOfTop10DownstreamSalesAmounts","十大下游企业开票额的数量");//???
        explanation.msg.put("AccessOrNot","是否满足准入条件");
        explanation.msg.put("AccessRequirement","准人条件信息");
        msg.msg.put("explanation",explanation);

        // 同步接口
        // load 税务数据
        firmTaxDataEtlService.loadByApplyId(applyId);
        // load 企业信息
        appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyId);
        // 准入条件计算
        AccessRequirement accessRequirement=accessRequirementDao.findByApplyId(applyId);
        if (accessRequirement == null){
            int res = accessRequirementEtlService.loadAccessRequirement(applyId,osDb,taxDb,destDb);
            if (res == -1 ) {
                msg.msg.put("AccessOrNot","准入条件相关信息不全，请补全。");
            }
            accessRequirement=accessRequirementDao.findByApplyId(applyId);
        }
        if (accessRequirement != null){
            msg.msg.put("AccessRequirement",accessRequirement);
            if (accessRequirement.getFlag() == 1 ) msg.msg.put("AccessOrNot","满足准入条件");
            else msg.msg.put("AccessOrNot","不满足准入条件");
        }

        // 进行计算
        // 查询是否有历史数据
        int count = 0 ;
        Entity score = null;
        // 判空
      //  if (accessRequirement != null && accessRequirement.getFlag() == 1){
            score = CreditScore.getScoreResult(applyId, destDb);
            if (score == null){
                // 判断月份要求
                count = accessRequirementEtlService.calMonthsCount(applyId,osDb,destDb);
                if (count >= 12){
                    String args = applyId + " EX_DB {'HOSTNAME':'127.0.0.1','USERNAME':'root','PASSWORD':'Admin$1533','DATABASE':'credit','PORT':3306,'CHARSET':'utf8'} score_result";
                    OSUtil.executePythonAndGetResult("model_main.py", args,"/python3Lib/algorithms");
                    score = CreditScore.getScoreResult(applyId, destDb);
                }
                else {
                    //不满足条件的 放入税务不完整数组中 用etl更新数据
                    dataLoader.taxUncompletedApplyIds.add(applyId);
                    msg.msg.put("AccessOrNot","税务数据不满足12个月要求，请补充税务数据");
                }
            }
     //   }
        msg.msg.put("MonthsCount",count);

        try {
            Entity report=CreditScore.getScoreResult(applyId,destDb);
            if (report != null)  msg.msg.put("report", report);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // report 报告数据
        // 基本信息
        AppFirmInfo appFirmInfo= appFirmInfoDao.findByApplyId(applyId);
        if (appFirmInfo != null) {
            msg.msg.put("firmName", appFirmInfo.getFirmName()); // 企业名称
            msg.msg.put("taxpayerId", appFirmInfo.getTaxpayerId()); // 纳税人识别号
            msg.msg.put("uniSocialCreditCode",appFirmInfo.getUniSocialCreditCode());//社会统一信用代码
            msg.msg.put("businessStart",appFirmInfo.getBusinessDate()); //成立日期
            msg.msg.put("registeredCapital", appFirmInfo.getRegisteredCapital()); // 注册资本
            msg.msg.put("address",appFirmInfo.getBusinessAddress());//地址
            msg.msg.put("applyCreditAmount",appFirmInfo.getApplyCreditLimit());//申请授信金额
        }
        if(score != null){
            msg.msg.put("creditScore", score.getStr("credit_score")); // 信用评分
            msg.msg.put("creditAmount",score.getStr("amount_credit")); //建议授信金额
            msg.msg.put("lastYearSalesAmount",score.getStr("estimate_income")) ;// 上年度销售额
        }
        // 月销售额统计
        List<FirmMonthsOutput> firmMonthsOutputs=firmMonthsOutputDao.findByApplyId(applyId);
        if (firmMonthsOutputs != null) msg.msg.put("firmMonthSalesAmount",firmMonthsOutputs);
        //十大下游企业开票额 和 企业数量
        msg.msg.put("top10DownstreamSalesAmounts"," ");
        msg.msg.put("sizeOfTop10DownstreamSalesAmounts",10);
        //集中度
        msg.msg.put("concentrationRatio"," ");
        //主营商品销售额
        msg.msg.put("mainProduct"," ");
        //企业画像
        msg.msg.put("firmPortraitScore"," ");
        //企业平均画像
        msg.msg.put("firmPortraitScoreAverage"," ");
        msg.msg.put("creditScoreAverage"," ");
        msg.msg.put("firmScaleAverage"," ");
        msg.msg.put("industryStatus"," ");
        msg.msg.put("stabilityAverage"," ");
        msg.msg.put("growthOpportunity"," ");
        // 关闭数据库连接
        destDs.close();
        osDs.close();
        taxDs.close();
        return msg;
    }

    @RequestMapping(value = "/reportAsync", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId", defaultValue = "0973D35C50DB4972858E97AF5D79CD6B", value = "企业的申请id", required = true)
    })
    public WebMessage reportAsync(final String applyId) throws Exception {
	    return riskMgrService.report(applyId, false);
    }

//    @RequestMapping(value = "/reportForceAsync", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "applyId", defaultValue = "0973D35C50DB4972858E97AF5D79CD6B", value = "企业的申请id", required = true)
//    })
//    public WebMessage reportForceAsync(final String applyId) throws Exception {
//        return riskMgrService.report(applyId, true);
//    }
}