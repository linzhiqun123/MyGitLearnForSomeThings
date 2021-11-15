package com.clpm.quartz.job;

import java.io.Serializable;
import java.util.Date;

public class JobTask implements Serializable {

    private int jobId;

    public JobTask() {
    }


    private String jobName;
    private String createAuthor;
    private Date endTime;
    private Date createTime;
    private String jobData;

    public JobTask(String jobName, String createAuthor, Date endTime, Date createTime, String jobData) {
        this.jobName = jobName;
        this.createAuthor = createAuthor;
        this.endTime = endTime;
        this.createTime = createTime;
        this.jobData = jobData;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCreateAuthor() {
        return createAuthor;
    }

    public void setCreateAuthor(String createAuthor) {
        this.createAuthor = createAuthor;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
