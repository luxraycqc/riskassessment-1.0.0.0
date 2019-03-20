package cn.net.aichain.edge.ms.jpa.firm;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 企业基本信息
 */
@Entity
public class AppFirmInfo {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;

    String applyId; // 申请id

    String uniSocialCreditCode; // 统一社会信用代码

    String organizationCode; // 组织机构代码

    String taxpayerId; // 纳税人识别号

    String businessNo; // 工商注册登记号码

    String firmType; // 企业类型

    String firmName; // 企业名称

    String businessSector; // 工商登记机关

    String legalRepresentative; // 法定代表人

    String businessStatus; // 经营状态

    String registeredCapital; // 注册资本

    String registeredAddress; // 注册地址

    String businessAddress; // 经营地址

    String depositBank; // 开户银行

    String applyCreditLimit;// 申请贷款金额

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date establishDate; // 成立日期

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date businessDate; // 工商注册登记日期

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date applyDate; // 申请日期

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date approvalDate; // 核准日期

    int registeredYear; // 注册时间（年）

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date businessStart; // 营业期限起

    @Column(columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    Date businessEnd; // 营业期限至

    @Column(columnDefinition = "varchar(500)")
    String businessScope; // 经营范围

    @Column(columnDefinition = "varchar(10)")
    String city; // 所在地级市

    @Column(columnDefinition = "varchar(10)")
    String county; // 所在县级市/县/区

    double county_CAGR; // 县区复合年增长率

    byte taxpayerQualification; // 一般纳税人资质

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

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getTaxpayerId() {
        return taxpayerId;
    }

    public void setTaxpayerId(String taxpayerId) {
        this.taxpayerId = taxpayerId;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getFirmType() {
        return firmType;
    }

    public void setFirmType(String firmType) {
        this.firmType = firmType;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getBusinessSector() {
        return businessSector;
    }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank;
    }

    public Date getEstablishDate() {
        return establishDate;
    }

    public void setEstablishDate(Date establishDate) {
        this.establishDate = establishDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getBusinessStart() {
        return businessStart;
    }

    public void setBusinessStart(Date businessStart) {
        this.businessStart = businessStart;
    }

    public Date getBusinessEnd() {
        return businessEnd;
    }

    public void setBusinessEnd(Date businessEnd) {
        this.businessEnd = businessEnd;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public int getRegisteredYear() {
        return registeredYear;
    }

    public void setRegisteredYear(int registeredYear) {
        this.registeredYear = registeredYear;
    }

    public double getCounty_CAGR() {
        return county_CAGR;
    }

    public void setCounty_CAGR(double county_CAGR) {
        this.county_CAGR = county_CAGR;
    }

    public byte getTaxpayerQualification() {
        return taxpayerQualification;
    }

    public void setTaxpayerQualification(byte taxpayerQualification) {
        this.taxpayerQualification = taxpayerQualification;
    }

    public String getApplyCreditLimit() {
        return applyCreditLimit;
    }

    public void setApplyCreditLimit(String applyCreditLimit) {
        this.applyCreditLimit = applyCreditLimit;
    }
}
