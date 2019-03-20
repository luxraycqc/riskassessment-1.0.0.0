package cn.net.aichain.edge.ms.jpa.firm;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/***
 * 准入条件
 * 用于存储企业申请时的各类准入条件、作为判断依据
 */
@Entity
public class AccessRequirement {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;

    String applyId; //申请id

    String firmName; //企业名称

    String bankName; //银行名称

    int businessYear; //营业执照经营年限 (>=24个月符合准入)

    String applicantBirthplace; // 申请人籍贯 存放行政区划代码 需要从数据宝获得

    String firmIndustry; // 企业所属行业 存放国民经济行业分类标准名称

    int applicantUntrustworthySituation;// 申请人司法失信情况 存放失信次数 需要从数据宝获得

    int firmUntrustworthySituation; // 企业司法失信情况 存放失信次数 需要从数据宝获得

    String taxCreditGrade; // 纳税信用等级 （A、B、C、D、M）D 不准入

    int applicantAge;// 申请人年龄 需要从数据宝获得

    String gender; // 申请人性别 男1 女0 需要从数据宝获得

    int firstSalesInvoiceDate; // 第一笔销项发票距今期限

    int lastSalesInvoiceDate;// 最后一笔销项发票距今期限

    double redInvoiceRate; // 过去12个月红字冲正发票的金额比例

    int flag; // 是否满足准入条件 0不满足 1满足

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getFirmName() {
        return firmName;
    }

    public double getRedInvoiceRate() {
        return redInvoiceRate;
    }

    public int getApplicantAge() {
        return applicantAge;
    }

    public String getApplicantBirthplace() {
        return applicantBirthplace;
    }


    public String getBankName() {
        return bankName;
    }


    public String getFirmIndustry() {
        return firmIndustry;
    }

    public String getTaxCreditGrade() {
        return taxCreditGrade;
    }

    public void setApplicantAge(int applicantAge) {
        this.applicantAge = applicantAge;
    }

    public void setApplicantBirthplace(String applicantBirthplace) {
        this.applicantBirthplace = applicantBirthplace;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setFirmIndustry(String firmIndustry) {
        this.firmIndustry = firmIndustry;
    }

    public void setRedInvoiceRate(double redInvoiceRate) {
        this.redInvoiceRate = redInvoiceRate;
    }

    public void setTaxCreditGrade(String taxCreditGrade) {
        this.taxCreditGrade = taxCreditGrade;
    }

    public void setFirstSalesInvoiceDate(int firstSalesInvoiceDate) {
        this.firstSalesInvoiceDate = firstSalesInvoiceDate;
    }

    public void setFirmUntrustworthySituation(int firmUntrustworthySituation) {
        this.firmUntrustworthySituation = firmUntrustworthySituation;
    }

    public void setBusinessYear(int businessYear) {
        this.businessYear = businessYear;
    }

    public void setApplicantUntrustworthySituation(int applicantUntrustworthySituation) {
        this.applicantUntrustworthySituation = applicantUntrustworthySituation;
    }

    public void setLastSalesInvoiceDate(int lastSalesInvoiceDate) {
        this.lastSalesInvoiceDate = lastSalesInvoiceDate;
    }

    public int getApplicantUntrustworthySituation() {
        return applicantUntrustworthySituation;
    }

    public int getBusinessYear() {
        return businessYear;
    }

    public int getFirmUntrustworthySituation() {
        return firmUntrustworthySituation;
    }

    public int getFirstSalesInvoiceDate() {
        return firstSalesInvoiceDate;
    }

    public int getLastSalesInvoiceDate() {
        return lastSalesInvoiceDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
