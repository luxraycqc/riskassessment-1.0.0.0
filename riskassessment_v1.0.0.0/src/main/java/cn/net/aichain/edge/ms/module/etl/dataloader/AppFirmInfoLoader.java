package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfo;
import com.alibaba.druid.pool.DruidDataSource;

import java.util.Date;
import java.util.HashMap;

public class AppFirmInfoLoader {
    // 调用数据宝的缓存接口获取本地数据库中的企业基本信息历史数据，填充相应字段
//    public static int loadFromSJBLocal(AppFirmInfo appFirmInfo) {
//        HashMap<String, Object> request = new HashMap<>();
//        request.put("token", "b6984d464ad1c6f2728e631b44af3258");
//        request.put("name", appFirmInfo.getFirmName());
//        request.put("limit", 100);
//        request.put("skip", 0);
//        String resultFullStr = null;
//        try {
//            resultFullStr = HttpUtil.get("http://vpn1.chenkuo.com.cn:8888/sjb/local/enterpriseBasicInfo", request, 2000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (resultFullStr == null) return 1;
//        JSONObject resultFullJson = JSONUtil.parseObj(resultFullStr);
//        int code = resultFullJson.getInt("returnCode");
//        if (code != 0) return 2;
//        JSONArray returnData = resultFullJson.getJSONArray("returnData");
//        if (returnData.size() <= 0) return 3;
//        JSONObject basicData = ((JSONObject) returnData.get(0)).getJSONObject("data").getJSONObject("basic");
//        String registeredCapital = basicData.getStr("regcap");
//        if (registeredCapital == null) return 4;
//        appFirmInfo.setRegisteredCapital(registeredCapital);
//        return 0;
//    }
    // 调用数据宝的接口获取企业基本信息，填充注册资本字段
    public static int loadFromSJB(AppFirmInfo appFirmInfo) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("name", appFirmInfo.getFirmName());
        request.put("token", "b6984d464ad1c6f2728e631b44af3258");
        request.put("type", "name");
        request.put("entType", 0);
        String resultFullStr = null;
        try {
            resultFullStr = HttpUtil.get("http://vpn1.chenkuo.com.cn:8888/sjb/enterpriseBasicInfo", request, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultFullStr == null) return 11;
        JSONObject resultFullJson = JSONUtil.parseObj(resultFullStr);
        String code = resultFullJson.getStr("code");
        if (!"10000".equals(code)) return 12;
        JSONObject basicData = resultFullJson.getJSONObject("data").getJSONObject("basic");
        String registeredCapital = basicData.getStr("regcap");
        if (registeredCapital == null) return 13;
        appFirmInfo.setRegisteredCapital(registeredCapital);
        return 0;
    }
    // 调用数据宝的接口获取企业基本信息，返回半原始json
//    public static String loadSemiRawFromSJB(String firmName) {
//        HashMap<String, Object> request = new HashMap<>();
//        request.put("token", "b6984d464ad1c6f2728e631b44af3258");
//        request.put("name", firmName);
//        request.put("type", "name");
//        request.put("entType", 0);
//        String resultFullStr = null;
//        try {
//            resultFullStr = HttpUtil.get("http://vpn1.chenkuo.com.cn:8888/sjb/enterpriseBasicInfo", request, 2000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (resultFullStr == null) return "no result";
//        JSONObject resultFullJson = JSONUtil.parseObj(resultFullStr);
//        int code = resultFullJson.getInt("code");
//        if (code != 10000) return "wrong result";
//        String basicData = resultFullJson.getJSONObject("data").getStr("basic");
//        System.out.println(basicData);
//        return basicData;
//    }
    // 调用数据宝的接口获取企业基本信息，返回原始json
    public static String loadRawFromSJB(String firmName) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("token", "b6984d464ad1c6f2728e631b44af3258");
        request.put("name", firmName);
        request.put("type", "name");
        request.put("entType", 0);
        String resultFullStr = null;
        try {
            resultFullStr = HttpUtil.get("http://vpn1.chenkuo.com.cn:8888/sjb/enterpriseBasicInfo", request, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(resultFullStr);
        return resultFullStr;
    }
    // 从chenkuo_os库中获取企业申请信息，填充相应字段
    @Deprecated
    public static int loadFromOs(AppFirmInfo appFirmInfo, Db osDb) {
        try {
            Entity applyInfo = osDb.queryOne("SELECT APPLY_ID, APPLY_TIME FROM f_apply WHERE ENTERPRISE_NAME = ?",
                    appFirmInfo.getFirmName());
            String applyId = applyInfo.getStr("APPLY_ID");
            appFirmInfo.setApplyId(applyId);
            Date applyTime = applyInfo.getDate("APPLY_TIME");
            appFirmInfo.setApprovalDate(applyTime);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
    // 从开发数据库中导出企业名称清单
//    public static void exportFirmNameList() {
//		String url = "jdbc:mysql://home1.aichain.net.cn:3306/chenkuo_v2?useCompression=true&useServerPrepStmts=false&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&useSSL=false&useUnicode=true&characterEncoding=UTF-8";
//		String username = "infosec";
//		String password = "admin1533";
//		DruidDataSource ds = new DruidDataSource();
//		ds.setUrl(url);
//		ds.setUsername(username);
//		ds.setPassword(password);
//		Db db = DbUtil.use(ds);
//        String sql = "SELECT a.ENTERPRISE_NAME FROM `ods_f_apply_1117` a, app_firm_model_result b " +
//                "WHERE b.report_id=CONVERT(a.APPLY_ID USING utf8) COLLATE utf8_unicode_ci";
//		try {
//            List<Entity> firmNameList = db.query(sql);
//            FileWriter fileWriter = new FileWriter("dataloader/firm_name_list.txt");
//            fileWriter.write(""); // 清空文件内容
//            for (Entity firmNameEntity : firmNameList) {
//                String firmName = firmNameEntity.getStr("ENTERPRISE_NAME");
//                fileWriter.append(firmName + "\n");
//            }
//        } catch (Exception e) {
//		    e.printStackTrace();
//        }
//    }
    // 读取清单中所有的企业，调用数据宝接口查询基本信息，存入chenkuo_credit数据库
//    @Deprecated
//    public static void importFirmInfoBatch(String filePath) throws Exception {
//        FileReader fileReader = new FileReader(filePath);
//        List<String> firmNameList = fileReader.readLines();
//        Props props = new Props("config/etl.properties");
//        String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
//        String osUsername = props.getStr("srcdb.os.mysql.username");
//        String osPassword = props.getStr("srcdb.os.mysql.password");
//        DruidDataSource osDs = new DruidDataSource();
//        osDs.setUrl(osUrl);
//        osDs.setUsername(osUsername);
//        osDs.setPassword(osPassword);
//        Db osDb = DbUtil.use(osDs);
//        String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
//        String creditUsername = props.getStr("destdb.main.mysql.username");
//        String creditPassword = props.getStr("destdb.main.mysql.password");
//        DruidDataSource creditDs = new DruidDataSource();
//        creditDs.setUrl(creditUrl);
//        creditDs.setUsername(creditUsername);
//        creditDs.setPassword(creditPassword);
//        Db creditDb = DbUtil.use(creditDs);
//        for (String firmName : firmNameList) {
//            if (firmName.length() > 0) {
//                AppFirmInfo appFirmInfo = new AppFirmInfo();
//                appFirmInfo.setFirmName(firmName);
//                if (loadFromSJB(appFirmInfo) == 0 || loadFromOs(appFirmInfo, osDb) == 0) {
//                    if (appFirmInfo.getApplyId() == null) appFirmInfo.setApplyId(String.valueOf(appFirmInfo.hashCode()));
//                        creditDb.execute("REPLACE INTO app_firm_info(apply_id, name_of_firm, registered_capital) VALUES(?,?,?)",
//                            appFirmInfo.getApplyId(), appFirmInfo.getFirmName(), appFirmInfo.getRegisteredCapital());
//                }
//            }
//        }
//    }
    // 读取清单中所有的企业，调用数据宝接口查询基本信息，存入chenkuo_credit数据库
//    public static int importFirmRawInfoBatch(String filePath) throws Exception {
//        Log log = LogFactory.get();
//        FileReader fileReader = new FileReader(filePath);
//        List<String> firmNameList = fileReader.readLines();
//        Props props = new Props("config/etl.properties");
//        String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
//        String creditUsername = props.getStr("destdb.main.mysql.username");
//        String creditPassword = props.getStr("destdb.main.mysql.password");
//        DruidDataSource creditDs = new DruidDataSource();
//        creditDs.setUrl(creditUrl);
//        creditDs.setUsername(creditUsername);
//        creditDs.setPassword(creditPassword);
//        Db creditDb = DbUtil.use(creditDs);
//        int count = 0;
//        for (String firmName : firmNameList) {
//            if (firmName.length() > 0) {
//                String firmInfo = loadRawFromSJB(firmName);
//                if (firmInfo != null) {
//                    creditDb.execute("INSERT INTO firm_info_raw(firm_name, firm_info, update_time) VALUES(?,?,?)",
//                            firmName, firmInfo, new Date());
//                    count++;
//                } else {
//                    log.info(firmName + "的信息查询失败");
//                }
//            }
//        }
//        return count;
//    }

//    public static void main(String[] args) throws Exception {
//        String firmName = "南京众合生物科技有限责任公司";
////        AppFirmInfo appFirmInfo = new AppFirmInfo();
////        appFirmInfo.setNameOfFirm(firmName);
//        loadRawFromSJB(firmName);
//    }
}
