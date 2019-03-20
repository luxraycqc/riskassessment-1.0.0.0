package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.jpa.firm.AccessRequirement;
import cn.net.aichain.edge.ms.jpa.firm.AccessRequirementDao;
import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfo;
import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfoDao;
import cn.net.aichain.edge.ms.utils.IdentityUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AccessRequirementEtlService {
    @Autowired
    AccessRequirementDao accessRequirementDao;
    @Autowired
    AppFirmInfoDao appFirmInfoDao;
    // 计算准入条件 之前需要load 税务数据 和 企业信息数据
    public int loadAccessRequirement(String applyId,Db osDb,Db taxDb,Db destDb) throws Exception {

        String fApply="SELECT * FROM f_apply WHERE APPLY_ID=?";
        Entity firmApply=osDb.queryOne(fApply,applyId);
        if(firmApply == null) return -1;

        String firmName=firmApply.getStr("ENTERPRISE_NAME");
        String bankName=firmApply.getStr("BANK_NAME");
        String nsrsbh=firmApply.getStr("NSRSBH");  // 纳税人识别号
        String applyTime=firmApply.getStr("APPLY_TIME"); //申请时间
        String jkrType=firmApply.getStr("JKR_TYPE"); // 借款人类型，fddbr 法定代表人；sjkzr 实际控制人

        String applicantBirthplace = "";// 申请人籍贯
        int applicantAge = 0 ; //申请人年龄
        String gender = ""; //申请人性别
        String identyNo = ""; //申请人身份证号
        Entity applicant ;

        String sql="";
        // 获得借款人信息 籍贯 年龄 性别
        if("fddbr".equals(jkrType)){
            sql="SELECT identy_no FROM f_fddbr WHERE record_id=?";
        }
        else if ("sjkzr".equals(jkrType)){
            sql="SELECT identy_no FROM f_sjkzr WHERE record_id=?";
        }

        if (sql != ""){
            applicant= osDb.queryOne(sql,applyId);
            if (applicant == null)
            {
                System.out.println("无借款人信息");
                return -1;
            }
            if (applicant != null) identyNo = applicant.getStr("identy_no");
        }

        if (identyNo != null ){
            int[] arr= IdentityUtil.extractAreaCodeAgeAndGender(identyNo);
            applicantBirthplace=String.valueOf(arr[0]);
            applicantAge=arr[1];
            gender=String.valueOf(arr[2]);
        }

        // 获得 红冲发票比率 第一笔&最后一笔发票距今时间

        String queryDate="select DATEDIFF(?,MAX(kprq)) AS `last` ,DATEDIFF(?,MIN(kprq)) AS `first` \n" +
                "from invoice_structure WHERE xfsbh=? and kprq <= DATE_FORMAT(?,\"%Y-%m-%d\")";
        Entity invoiceDate=taxDb.queryOne(queryDate,applyTime,applyTime,nsrsbh,applyTime);
        if (invoiceDate == null) {
            System.out.println("无销项发票数据");
            return -1;
        }

        int last=invoiceDate.getInt("last");
        int first=invoiceDate.getInt("first");

        String queryRate="-- 计算过去12个月红字冲正发票的金额比例 \n" +
                "-- 参数 applyId applyTime appltTime\n" +
                "SELECT SUM(-1*red_invoice_amt) / SUM(valid_invoice_amt-red_invoice_amt) as rate\n" +
                "from firm_months_output WHERE apply_id=? and \n" +
                "months BETWEEN DATE_SUB(DATE_FORMAT(?,\"%Y-%m-%d\"),INTERVAL 1 year) \n" +
                "and DATE_FORMAT(?,\"%Y-%m-%d\")";
        Entity redRate=destDb.queryOne(queryRate,applyId,applyTime,applyTime);
        if (redRate == null) {
            System.out.println("无企业月销售数据");
            return -1;
        }
        if (redRate.getDouble("rate") == null) {
            System.out.println("无红冲发票比例");
            return -1;
        }
        double redInvoiceRate=redRate.getDouble("rate");
        // 获得 注册年限
        AppFirmInfo appFirmInfo=appFirmInfoDao.findByApplyId(applyId);
        int businessYear = 0;
        if (appFirmInfo == null) {
            System.out.println("无企业基本信息");
            return -1;
        }
        businessYear = appFirmInfo.getRegisteredYear();

        // 存入数据库
        AccessRequirement accessRequirement=new AccessRequirement();
        accessRequirement.setApplyId(applyId);
        accessRequirement.setFirmName(firmName);
        accessRequirement.setBankName(bankName);
        accessRequirement.setBusinessYear(businessYear);
        // 申请人司法情况
         loadFromSJB(accessRequirement);
        accessRequirement.setApplicantUntrustworthySituation(0);
        accessRequirement.setApplicantBirthplace(applicantBirthplace);
        accessRequirement.setGender(gender);
        accessRequirement.setApplicantAge(applicantAge);
        accessRequirement.setFirstSalesInvoiceDate(first);
        accessRequirement.setLastSalesInvoiceDate(last);
        accessRequirement.setRedInvoiceRate(redInvoiceRate);
        //判断 是否满足准入条件 获得 flag
        int flag = judgeAccessOrNot(accessRequirement);

        accessRequirement.setFlag(flag);

        accessRequirementDao.save(accessRequirement);

        return flag;

    }

    public static int loadFromSJB(AccessRequirement accessRequirement) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("token", "b6984d464ad1c6f2728e631b44af3258");
        request.put("name", accessRequirement.getFirmName());
        request.put("skip", 0);
        String resultFullStr = null;
        try {
            resultFullStr = HttpUtil.get("http://vpn1.chenkuo.com.cn:8888/sjb/SXR", request, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultFullStr == null) return 11;
        JSONObject resultFullJson = JSONUtil.parseObj(resultFullStr);
        String code = resultFullJson.getStr("code");
        if (!"10000".equals(code)) return 12;
        int total = resultFullJson.getJSONObject("data").getInt("total");
        accessRequirement.setApplicantUntrustworthySituation(total);
        return 0;
    }

    // 判断准入条件
    public static int judgeAccessOrNot(AccessRequirement accessRequirement){
        if (accessRequirement.getBankName().contains("紫金农村商业银行")){
            System.out.println("判断准入条件：企业经营年限"+accessRequirement.getBusinessYear());
            if (accessRequirement.getBusinessYear() < 2) return 0; // 企业经营年限>=24月
            System.out.println("判断准入条件：申请人籍贯" + accessRequirement.getApplicantBirthplace());
            if (accessRequirement.getApplicantBirthplace().substring(0,2).equals("15")) return 0; //内蒙古
            if (accessRequirement.getApplicantBirthplace().substring(0,2).equals("35")) return 0; //福建
            if (accessRequirement.getApplicantBirthplace().equals("3301")) return 0; //杭州
            System.out.println("判断准入条件：申请人司法失信情况" + accessRequirement.getApplicantUntrustworthySituation());
            if (accessRequirement.getApplicantUntrustworthySituation()>0) return 0;// 申请人被列入失信名单
            System.out.println("判断准入条件：申请人年龄" + accessRequirement.getApplicantAge());
            if (accessRequirement.getApplicantAge() > 60 || accessRequirement.getApplicantAge() < 25) return 0; //年龄在[25,60]
            System.out.println("判断准入条件：第一笔销项发票距今期限" + accessRequirement.getFirstSalesInvoiceDate());
            if (accessRequirement.getFirstSalesInvoiceDate() < 360) return 0; //第一笔发票 >= 360 天
            System.out.println("判断准入条件：最后一笔销项发票距今期限" + accessRequirement.getLastSalesInvoiceDate() );
            if (accessRequirement.getLastSalesInvoiceDate() > 90) return 0; //最后一笔发票 <= 90 天
            System.out.println("判断准入条件：过去12个月红冲金额比例" + accessRequirement.getRedInvoiceRate());
            if (accessRequirement.getRedInvoiceRate() > 0.300000) return 0; //红冲比例 <= 30%
        }
        else if (accessRequirement.getBankName().contains("江南农商行")){
            System.out.println("判断准入条件：企业经营年限"+accessRequirement.getBusinessYear());
            if (accessRequirement.getBusinessYear() < 2) return 0; // 企业经营年限>=24月
            System.out.println("判断准入条件：申请人籍贯" + accessRequirement.getApplicantBirthplace());
            if (accessRequirement.getApplicantBirthplace().substring(0,2).equals("15")) return 0; //内蒙古
            if (accessRequirement.getApplicantBirthplace().substring(0,2).equals("35")) return 0; //福建
            if (accessRequirement.getApplicantBirthplace().equals("3301")) return 0; //杭州
            System.out.println("判断准入条件：申请人司法失信情况" + accessRequirement.getApplicantUntrustworthySituation());
            if (accessRequirement.getApplicantUntrustworthySituation()>0) return 0;// 申请人被列入失信名单
            System.out.println("判断准入条件：申请人年龄" + accessRequirement.getApplicantAge());
            if (accessRequirement.getGender().equals("0")){
                if (accessRequirement.getApplicantAge() > 55 || accessRequirement.getApplicantAge() < 18) return 0; //女性 在[18,55]
            }
            if (accessRequirement.getGender().equals("1")){
                if (accessRequirement.getApplicantAge() > 60 || accessRequirement.getApplicantAge() < 18) return 0; //男性 在[18,60]
            }
            System.out.println("判断准入条件：第一笔销项发票距今期限" + accessRequirement.getFirstSalesInvoiceDate());
            if (accessRequirement.getFirstSalesInvoiceDate() < 540) return 0; //第一笔发票 >= 360 天
            System.out.println("判断准入条件：最后一笔销项发票距今期限" + accessRequirement.getLastSalesInvoiceDate() );
            if (accessRequirement.getLastSalesInvoiceDate() > 90) return 0; //最后一笔发票 <= 90 天
            System.out.println("判断准入条件：过去12个月红冲金额比例" + accessRequirement.getRedInvoiceRate());
            if (accessRequirement.getRedInvoiceRate() > 0.300000) return 0; //红冲比例 <= 30%
        }
        else return 0;
        return 1;
    }
    // 判断税务数据是否18个月
    public int calMonthsCount(String applyId,Db osDb,Db destDb) throws Exception{
        String fApply="SELECT * FROM f_apply WHERE APPLY_ID=?";
        Entity firmApply=osDb.queryOne(fApply,applyId);
        if(firmApply == null) return 0 ;
        String applyTime=firmApply.getStr("APPLY_TIME"); //申请时间
        String sql="select COUNT(*) as months\n" +
                "from firm_months_output\n" +
                "WHERE apply_id=? \n" +
                "and months BETWEEN  \n" +
                "DATE_SUB(DATE_FORMAT(?,\"%Y-%m-%d\"),INTERVAL 24 month) and DATE_FORMAT(?,\"%Y-%m-%d\")\n";
        Entity res = destDb.queryOne(sql,applyId,applyTime,applyTime);
        if (res == null) return 0;
        if (res.getInt("months") == null) return 0;
        int monthsCount = res.getInt("months");

        return monthsCount;
    }

}
