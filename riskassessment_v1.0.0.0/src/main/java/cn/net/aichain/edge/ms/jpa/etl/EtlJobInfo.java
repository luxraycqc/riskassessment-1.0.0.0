package cn.net.aichain.edge.ms.jpa.etl;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class EtlJobInfo {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "org.mark.lib.id.SnowflakeId")
    long id;

    String name; // 任务名称
    
    String content; // 报文

	Byte readStatus; // 是否已读

	Byte jobStatus; // 是否已完成

    @Column(columnDefinition = "datetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date createDate; // 创建时间

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Byte getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Byte readStatus) {
		this.readStatus = readStatus;
	}

	public Byte getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Byte jobStatus) {
		this.jobStatus = jobStatus;
	}
}
