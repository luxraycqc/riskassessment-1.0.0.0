package cn.net.aichain.edge.ms.jpa.report;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

public final class ReportRiskAssessment {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;

    String applyId; // 申请id

    String uniSocialCreditCode; // 统一社会信用代码
    
    String report;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public String getUniSocialCreditCode() {
		return uniSocialCreditCode;
	}

	public void setUniSocialCreditCode(String uniSocialCreditCode) {
		this.uniSocialCreditCode = uniSocialCreditCode;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
    
}
