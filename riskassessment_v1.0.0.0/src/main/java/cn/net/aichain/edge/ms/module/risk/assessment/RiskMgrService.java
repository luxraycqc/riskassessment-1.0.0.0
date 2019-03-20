package cn.net.aichain.edge.ms.module.risk.assessment;

import cn.hutool.core.date.DateUtil;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfo;
import cn.net.aichain.edge.ms.jpa.etl.EtlJobInfoDao;
import cn.net.aichain.edge.ms.message.WebMessage;
import cn.net.aichain.edge.ms.module.etl.dataloader.AppFirmInfoEtlService;
import cn.net.aichain.edge.ms.module.etl.dataloader.CreditScore;
import cn.net.aichain.edge.ms.module.etl.dataloader.FirmTaxDataEtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.net.aichain.edge.ms.jpa.firm.AppFirmInfoDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RiskMgrService {
	@Autowired
	LoadDataAndReportService loadDataAndReportService;
	@Autowired
	EtlJobInfoDao etlJobInfoDao;

	WebMessage report(String applyId, boolean force) throws Exception {
		String etlName = "report_" + applyId;
		List<EtlJobInfo> etlJobInfoList = etlJobInfoDao.findAllByName(etlName);
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
		WebMessage msg = new WebMessage();
		if (count == 0) {
			loadDataAndReportService.loadDataAndReport(applyId, force);
			msg.msg.put("info", "任务已创建，您可以重新提交该请求以获取最新的任务执行状态。");
		} else {
			msg.msg.put("info", "24小时以内有" + count + "条未完成或未读的任务和本次请求参数相同。");
			msg.msg.put("jobData", jobData);
			if (allCompleted) msg.msg.put("msg", "若要重新执行该任务，您可以重新提交该请求。");
			else msg.msg.put("msg", "24小时以内还有未完成的的任务和本次请求参数相同，本次请求无效。");
		}
		return msg;
	}
}
