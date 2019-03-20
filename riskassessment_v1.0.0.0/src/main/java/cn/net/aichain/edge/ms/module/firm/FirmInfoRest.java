package cn.net.aichain.edge.ms.module.firm;

import cn.hutool.db.Entity;
import cn.net.aichain.edge.ms.module.etl.dataloader.AppFirmInfoEtlService;
import cn.net.aichain.edge.ms.module.etl.dataloader.CreditScore;
import cn.net.aichain.edge.ms.module.etl.dataloader.EtlAsyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/firmInfo")
@Api(tags = "1.企业信息接口")
public class FirmInfoRest {
    @Autowired
    AppFirmInfoEtlService appFirmInfoEtlService;
    @Autowired
    EtlAsyncService etlAsyncService;
//    @RequestMapping(value = "/importJsonBatch", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "filePath", defaultValue = "dataloader/firm_name_list_test.txt", value = "企业清单相对于classpath的路径", required = true)
//    })
//    public String importJsonBatch(final String filePath) throws Exception {
//        int result = importFirmRawInfoBatch(filePath);
//        return "成功读取了" + result + "家企业的信息。";
//    }
    @RequestMapping(value = "/loadFirmInfo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyStartDate", defaultValue = "20181108", value = "申请日期起始（含起始日），例：20181108", required = true),
            @ApiImplicitParam(name = "applyEndDate", defaultValue = "20181108", value = "申请日期截止（含截止日），例：20181108", required = true)
    })
    public String loadFirmInfo(String applyStartDate, String applyEndDate) throws Exception {
        Future<Long> re = appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyStartDate, applyEndDate);
        long count = re.get();
        return "成功更新了" + count + "家企业的信息。";
    }

    @RequestMapping(value = "/loadFirmInfoAsync", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyStartDate", defaultValue = "20181108", value = "申请日期起始（含起始日），例：20181108", required = true),
            @ApiImplicitParam(name = "applyEndDate", defaultValue = "20181108", value = "申请日期截止（含截止日），例：20181108", required = true)
    })
    public Map<String, Object> loadFirmInfoAsync(String applyStartDate, String applyEndDate) throws Exception {
        return etlAsyncService.loadFirmInfoFromOsToCredit(applyStartDate, applyEndDate);
    }

    @RequestMapping(value = "/updateFirmInfo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId", defaultValue = "CF454B5323D34C8885F4C0C1A7F737A0", value = "企业的申请id", required = true)
    })
    public String updateFirmInfo(String applyId) throws Exception{
        int re = appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyId);
        if (re == 0) {
            return "更新完毕。";
        } else if (re == 1) {
            return "查询数据宝超时，更新了部分数据。";
        } else {
            return "更新失败！错误代码为" + re;
        }
    }

    @RequestMapping(value = "/getFirmApplyId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "firmName", value = "企业名称", defaultValue ="南京盛天公路工程有限公司", required = true)
    })
    public List<Entity> getFirmApplyId(final String firmName) throws Exception{
        return CreditScore.getFirmApplyId(firmName);
    }

    @RequestMapping(value = "/showAvailableApplyIds", method = RequestMethod.GET)
    public List<String> showAvailableApplyIds() throws Exception{
        return appFirmInfoEtlService.showAvailableApplyIds();
    }
}
