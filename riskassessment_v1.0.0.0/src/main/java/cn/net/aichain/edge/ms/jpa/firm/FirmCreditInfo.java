package cn.net.aichain.edge.ms.jpa.firm;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 企业信用信息
 */
@Entity
public class FirmCreditInfo {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;

    String applyId; // 申请id

    int age; // 申请人年龄

    String nativePlace; // 申请人籍贯

    String breakingFaith; // 企业失信人信息

    String courtAnnouncement; // 法院公告信息

    String courtDecisionment; // 法院判决信息

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getBreakingFaith() {
        return breakingFaith;
    }

    public void setBreakingFaith(String breakingFaith) {
        this.breakingFaith = breakingFaith;
    }

    public String getCourtAnnouncement() {
        return courtAnnouncement;
    }

    public void setCourtAnnouncement(String courtAnnouncement) {
        this.courtAnnouncement = courtAnnouncement;
    }

    public String getCourtDecisionment() {
        return courtDecisionment;
    }

    public void setCourtDecisionment(String courtDecisionment) {
        this.courtDecisionment = courtDecisionment;
    }
}
