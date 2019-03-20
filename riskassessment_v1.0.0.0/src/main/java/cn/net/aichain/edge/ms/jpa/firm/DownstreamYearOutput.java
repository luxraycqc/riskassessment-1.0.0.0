package cn.net.aichain.edge.ms.jpa.firm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

/***
 * 下游企业年销售
 */
@Entity
public class DownstreamYearOutput {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;
    
    @Column(name = "applyId",nullable = false)
    String applyId;
    @Column(name = "years",nullable = false)
    String years;
    String downstreamFirmBuyer;

    String downstreamFirmBuyerIds;
    BigDecimal invalidInvoiceAmt;
    BigDecimal validInvoiceAmt;
    BigDecimal redInvoiceAmt;
    BigDecimal transactionAmount;

    public BigDecimal getInvalidInvoiceAmt() {
        return invalidInvoiceAmt;
    }

    public BigDecimal getRedInvoiceAmt() {
        return redInvoiceAmt;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public BigDecimal getValidInvoiceAmt() {
        return validInvoiceAmt;
    }

    public String getApplyId() {
        return applyId;
    }

    public String getDownstreamFirmBuyer() {
        return downstreamFirmBuyer;
    }

    public String getDownstreamFirmBuyerIds() {
        return downstreamFirmBuyerIds;
    }

    public String getYears() {
        return years;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public void setDownstreamFirmBuyer(String downstreamFirmBuyer) {
        this.downstreamFirmBuyer = downstreamFirmBuyer;
    }

    public void setDownstreamFirmBuyerIds(String downstreamFirmBuyerIds) {
        this.downstreamFirmBuyerIds = downstreamFirmBuyerIds;
    }

    public void setInvalidInvoiceAmt(BigDecimal invalidInvoiceAmt) {
        this.invalidInvoiceAmt = invalidInvoiceAmt;
    }

    public void setRedInvoiceAmt(BigDecimal redInvoiceAmt) {
        this.redInvoiceAmt = redInvoiceAmt;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setValidInvoiceAmt(BigDecimal validInvoiceAmt) {
        this.validInvoiceAmt = validInvoiceAmt;
    }

    public void setYears(String years) {
        this.years = years;
    }

}
