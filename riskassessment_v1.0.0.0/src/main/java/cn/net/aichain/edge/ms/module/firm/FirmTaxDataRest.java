package cn.net.aichain.edge.ms.module.firm;

import cn.net.aichain.edge.ms.module.etl.dataloader.EtlAsyncService;
import cn.net.aichain.edge.ms.module.etl.dataloader.FirmTaxDataEtlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/***
 * 用于更新税务数据
 * 税务数据包括 ：
 * 1、企业的月销售数据
 * 2、下游企业的年销售数据
 */
@RestController
@RequestMapping("/firmTaxData")
@Api(tags = "2.企业税务数据接口")
public class FirmTaxDataRest {
    @Autowired
    FirmTaxDataEtlService firmTaxDataEtlService;
    @Autowired
    EtlAsyncService etlAsyncService;
    @RequestMapping(value = "/loadFirmTaxData",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyStartDate", defaultValue = "20181108", value = "申请日期起始（含起始日），例：20181108", required = true),
            @ApiImplicitParam(name = "applyEndDate", defaultValue = "20181108", value = "申请日期截止（含截止日），例：20181108", required = true)
    })
    public String loadFirmTaxData(String applyStartDate,String applyEndDate) throws Exception {
        List<String> applyIdList = new ArrayList<>();
        Future<Long> re = firmTaxDataEtlService.loadFirmTaxDataToCredit(applyStartDate, applyEndDate, applyIdList);
        return "成功更新了" + re.get() + "家企业的税务数据。";
    }

    @RequestMapping(value = "/loadFirmTaxDataAsync",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyStartDate", defaultValue = "20181108", value = "申请日期起始（含起始日），例：20181108", required = true),
            @ApiImplicitParam(name = "applyEndDate", defaultValue = "20181108", value = "申请日期截止（含截止日），例：20181108", required = true)
    })
    public Map<String, Object> loadFirmTaxDataAsync(String applyStartDate, String applyEndDate) throws Exception {
        return etlAsyncService.loadFirmTaxDataToCreditAsync(applyStartDate,applyEndDate);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId",value = "申请ID",defaultValue = "0973D35C50DB4972858E97AF5D79CD6B",required = true)
    })
    @RequestMapping(value = "/loadFirmTaxDataByApplyId",method = RequestMethod.GET)
    public String loadByApplyId(String applyId) throws Exception{
        boolean result = firmTaxDataEtlService.loadByApplyId(applyId);
        if (result){
            return "成功更新了申请id为"+applyId+"的企业的税务数据。";
        }else return "申请id为"+applyId+"的企业的税务数据已经存在。";
    }
//
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "updateDate",value = "更新日期",defaultValue = "2018-07-27",required = true)
//    })
//    @RequestMapping(value = "loadByInvoiceDate",method = RequestMethod.GET)
//    public String loadByInvoiceDate(String updateDate) throws Exception{
//        firmTaxDataEtlService.loadByInvoiceDate(updateDate);
//        return updateDate;
//    }

}
