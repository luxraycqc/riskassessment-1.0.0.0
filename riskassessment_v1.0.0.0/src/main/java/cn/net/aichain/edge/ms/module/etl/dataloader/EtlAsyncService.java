package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.core.date.DateUtil;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfo;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EtlAsyncService {
    @Autowired
    EtlJobInfoDao etlJobInfoDao;
    @Autowired
    AppFirmInfoEtlService appFirmInfoEtlService;
    @Autowired
    FirmTaxDataEtlService firmTaxDataEtlService;
    public Map<String, Object> loadFirmTaxDataToCreditAsync(String applyStartDate, String applyEndDate) {
        String etlName = "loadFirmTaxDataToCredit_" + applyStartDate + "_" + applyEndDate;
        List<EtlJobInfo> etlJobInfoList = etlJobInfoDao.findAllByName(etlName);
        TreeMap<String, Object> result = new TreeMap();
        ArrayList<String> jobData = new ArrayList<>();
        int count = 0;
        boolean allCompleted = true;
        for (EtlJobInfo etlJobInfo : etlJobInfoList) {
            if (etlJobInfo.getReadStatus() != 1 || etlJobInfo.getJobStatus() != 1) {
                Date createDate = etlJobInfo.getCreateDate();
                if (DateUtil.betweenMs(createDate, new Date()) > 86400000) {
                    etlJobInfoDao.updateContentById("任务超过24小时未完成，可能已终止或出现异常。", etlJobInfo.getId());
                    continue;
                }
                jobData.add("任务创建时间：" + etlJobInfo.getCreateDate() + "，任务状态：" + etlJobInfo.getContent());
                if (etlJobInfo.getJobStatus() == 1 && etlJobInfo.getReadStatus() != 1) etlJobInfoDao.updateReadStatusById(etlJobInfo.getId());
                if (etlJobInfo.getJobStatus() != 1) allCompleted = false;
                count ++;
            }
        }
        if (count == 0) {
            List<String> applyIdList = new ArrayList<>();
            firmTaxDataEtlService.loadFirmTaxDataToCredit(applyStartDate, applyEndDate, applyIdList);
            result.put("info", "任务已创建，您可以重新提交该请求以获取最新的任务执行状态。");
        } else {
            result.put("info", "24小时以内有" + count + "条未完成或未读的任务和本次请求参数相同。");
            result.put("jobData", jobData);
            if (allCompleted) result.put("msg", "若要重新执行该任务，您可以重新提交该请求。");
            else result.put("msg", "24小时以内还有未完成的的任务和本次请求参数相同，本次请求无效。");
        }
        return result;
    }

    public Map<String, Object> loadFirmInfoFromOsToCredit(String applyStartDate, String applyEndDate) {
        String etlName = "loadFirmInfoFromOsToCredit_" + applyStartDate + "_" + applyEndDate;
        List<EtlJobInfo> etlJobInfoList = etlJobInfoDao.findAllByName(etlName);
        TreeMap<String, Object> result = new TreeMap();
        ArrayList<String> jobData = new ArrayList<>();
        int count = 0;
        boolean allCompleted = true;
        for (EtlJobInfo etlJobInfo : etlJobInfoList) {
            if (etlJobInfo.getReadStatus() != 1 || etlJobInfo.getJobStatus() != 1) {
                Date createDate = etlJobInfo.getCreateDate();
                if (DateUtil.betweenMs(createDate, new Date()) > 86400000) {
                    etlJobInfoDao.updateContentById("任务超过24小时未完成，可能已终止或出现异常。", etlJobInfo.getId());
                    continue;
                }
                jobData.add("任务创建时间：" + etlJobInfo.getCreateDate() + "，任务状态：" + etlJobInfo.getContent());
                if (etlJobInfo.getJobStatus() == 1 && etlJobInfo.getReadStatus() != 1) etlJobInfoDao.updateReadStatusById(etlJobInfo.getId());
                if (etlJobInfo.getJobStatus() != 1) allCompleted = false;
                count ++;
            }
        }
        if (count == 0) {
            appFirmInfoEtlService.loadFirmInfoFromOsToCredit(applyStartDate, applyEndDate);
            result.put("info", "任务已创建，您可以重新提交该请求以获取最新的任务执行状态。");
        } else {
            result.put("info", "24小时以内有" + count + "条未完成或未读的任务和本次请求参数相同。");
            result.put("jobData", jobData);
            if (allCompleted) result.put("msg", "若要重新执行该任务，您可以重新提交该请求。");
            else result.put("msg", "24小时以内还有未完成的的任务和本次请求参数相同，本次请求无效。");
        }
        return result;
    }
}
