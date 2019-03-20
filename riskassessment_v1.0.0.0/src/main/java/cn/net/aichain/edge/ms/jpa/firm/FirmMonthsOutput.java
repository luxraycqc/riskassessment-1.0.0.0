package cn.net.aichain.edge.ms.jpa.firm;

import cn.hutool.core.text.replacer.StrReplacer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

/***
 * 企业月销售额
 */
@Entity
public class FirmMonthsOutput {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;
    
    @Column(name = "applyId",nullable = false)
    String applyId;

    String months;

    String outputQuarter;
    String seller;
    String sellerTaxpayerId;

    @Column(columnDefinition = "text")
    String downstreamFirmBuyerGroup;
    @Column(columnDefinition = "text")
    String downstreamFirmBuyerIds;
    long downstreamFirmBuyerNum;
    String typeOfInvoice;
    @Column(columnDefinition = "text")
    String typeOfTaxrate;
    long validInvoiceNum;
    long invalidInvoiceNum;
    long totalInvoiceNum;
    long validRedInvoiceNum;

    BigDecimal invalidInvoiceAmt;
    BigDecimal validInvoiceAmt;
    BigDecimal redInvoiceAmt;
    BigDecimal businessIncome;
    BigDecimal addedValueTaxAmt;
    BigDecimal totalTaxAmt;

    public void setValidInvoiceAmt(BigDecimal validInvoiceAmt) {
        this.validInvoiceAmt = validInvoiceAmt;
    }

    public void setRedInvoiceAmt(BigDecimal redInvoiceAmt) {
        this.redInvoiceAmt = redInvoiceAmt;
    }

    public void setInvalidInvoiceAmt(BigDecimal invalidInvoiceAmt) {
        this.invalidInvoiceAmt = invalidInvoiceAmt;
    }

    public void setDownstreamFirmBuyerIds(String downstreamFirmBuyerIds) {
        this.downstreamFirmBuyerIds = downstreamFirmBuyerIds;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getDownstreamFirmBuyerIds() {
        return downstreamFirmBuyerIds;
    }

    public String getApplyId() {
        return applyId;
    }

    public BigDecimal getValidInvoiceAmt() {
        return validInvoiceAmt;
    }

    public BigDecimal getRedInvoiceAmt() {
        return redInvoiceAmt;
    }

    public BigDecimal getInvalidInvoiceAmt() {
        return invalidInvoiceAmt;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getMonths() {
        return months;
    }

    public BigDecimal getBusinessIncome() {
        return businessIncome;
    }

    public long getDownstreamFirmBuyerNum() {
        return downstreamFirmBuyerNum;
    }

    public long getInvalidInvoiceNum() {
        return invalidInvoiceNum;
    }

    public BigDecimal getAddedValueTaxAmt() {
        return addedValueTaxAmt;
    }

    public BigDecimal getTotalTaxAmt() {
        return totalTaxAmt;
    }

    public long getTotalInvoiceNum() {
        return totalInvoiceNum;
    }

    public long getValidInvoiceNum() {
        return validInvoiceNum;
    }

    public long getValidRedInvoiceNum() {
        return validRedInvoiceNum;
    }

    public String getDownstreamFirmBuyerGroup() {
        return downstreamFirmBuyerGroup;
    }

    public String getOutputQuarter() {
        return outputQuarter;
    }

    public String getSeller() {
        return seller;
    }

    public String getSellerTaxpayerId() {
        return sellerTaxpayerId;
    }

    public String getTypeOfInvoice() {
        return typeOfInvoice;
    }

    public String getTypeOfTaxrate() {
        return typeOfTaxrate;
    }

    public void setAddedValueTaxAmt(BigDecimal addedValueTaxAmt) {
        this.addedValueTaxAmt = addedValueTaxAmt;
    }

    public void setBusinessIncome(BigDecimal businessIncome) {
        this.businessIncome = businessIncome;
    }

    public void setDownstreamFirmBuyerGroup(String downstreamFirmBuyerGroup) {
        this.downstreamFirmBuyerGroup = downstreamFirmBuyerGroup;
    }

    public void setDownstreamFirmBuyerNum(long downstreamFirmBuyerNum) {
        this.downstreamFirmBuyerNum = downstreamFirmBuyerNum;
    }

    public void setInvalidInvoiceNum(long invalidInvoiceNum) {
        this.invalidInvoiceNum = invalidInvoiceNum;
    }

    public void setOutputQuarter(String outputQuarter) {
        this.outputQuarter = outputQuarter;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setSellerTaxpayerId(String sellerTaxpayerId) {
        this.sellerTaxpayerId = sellerTaxpayerId;
    }

    public void setTotalInvoiceNum(long totalInvoiceNum) {
        this.totalInvoiceNum = totalInvoiceNum;
    }

    public void setTotalTaxAmt(BigDecimal totalTaxAmt) {
        this.totalTaxAmt = totalTaxAmt;
    }

    public void setTypeOfInvoice(String typeOfInvoice) {
        this.typeOfInvoice = typeOfInvoice;
    }

    public void setTypeOfTaxrate(String typeOfTaxrate) {
        this.typeOfTaxrate = typeOfTaxrate;
    }

    public void setValidInvoiceNum(long validInvoiceNum) {
        this.validInvoiceNum = validInvoiceNum;
    }

    public void setValidRedInvoiceNum(long validRedInvoiceNum) {
        this.validRedInvoiceNum = validRedInvoiceNum;
    }

}
