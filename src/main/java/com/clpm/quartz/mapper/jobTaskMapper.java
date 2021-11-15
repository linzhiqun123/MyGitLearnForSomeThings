package com.clpm.quartz.mapper;

import com.clpm.quartz.config.InterceptAnnotation;
import com.clpm.quartz.job.JobTask;
import com.clpm.quartz.pojo.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface jobTaskMapper {

    public void insertQuartzTask(JobTask jobTask);

    @InterceptAnnotation
    List<JobTask> findJobTasks(Page page);
}
