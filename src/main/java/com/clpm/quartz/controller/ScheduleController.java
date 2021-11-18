package com.clpm.quartz.controller;

import com.clpm.quartz.Jpa.YxStoreOrderQueryCriteria;
import com.clpm.quartz.Jpa.YxStoreProduct;
import com.clpm.quartz.Jpa.YxStoreProoductRepository;
import com.clpm.quartz.config.CodeTable;
import com.clpm.quartz.util.QueryHelp;
import com.clpm.quartz.config.Limit;
import com.clpm.quartz.job.JobTask;
import com.clpm.quartz.job.SendEmailJob;
import com.clpm.quartz.pojo.CommonResult;
import com.clpm.quartz.pojo.Page;
import com.clpm.quartz.pojo.User;
import com.clpm.quartz.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务调度相关接口
 * Created by macro on 2020/9/29.
 */
@Api(tags = "ScheduleController", description = "定时任务调度相关接口")
@Controller
@Slf4j
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    YxStoreProoductRepository yxStoreProoductRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @ApiOperation("定时发送邮件")
    @PostMapping("/sendEmail")
    public CommonResult sendEmail(@RequestParam(name = "data",required = false) String data) {
//        LocalDate nowLocalDate = LocalDate.now();
//        Date date = Date.from(nowLocalDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
        Date date = new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND,10);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("当前的时间为{}",df.format(date));
        log.info("设置的时间为{}",df.format(calendar.getTime()));
        //延时10s执行任务
        String jobName = scheduleService.scheduleFixTimeJob(SendEmailJob.class, calendar.getTime(), data);
        return CommonResult.success(jobName);
    }

//    @ApiOperation("定时发送站内信")
//    @PostMapping("/sendMessage")
//    public CommonResult sendMessage(@RequestParam String startTime,@RequestParam String data) {
//        Date date = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_FORMAT);
//        String jobName = scheduleService.scheduleFixTimeJob(SendMessageJob.class, date, data);
//        return CommonResult.success(jobName);
//    }
//
//    @ApiOperation("通过CRON表达式调度任务")
//    @PostMapping("/scheduleJob")
//    public CommonResult scheduleJob(@RequestParam String cron, @RequestParam String data) {
//        String jobName = scheduleService.scheduleJob(CronProcessJob.class, cron, data);
//        return CommonResult.success(jobName);
//    }

    @ApiOperation("取消定时任务")
    @PostMapping("/cancelScheduleJob")
    public CommonResult cancelScheduleJob(@RequestParam String jobName) {
        Boolean success = scheduleService.cancelScheduleJob(jobName);
        return CommonResult.success(success);
    }


    @GetMapping("/getAllScheduleJobTasks")
    @ApiOperation("获取所有的定时任务,按照分页参数展示")
    @ApiImplicitParams({@ApiImplicitParam(name = "size",value = "分页启始页数",required = true,paramType = "query"),
    @ApiImplicitParam(name = "index",value = "分页的长度",required = true,paramType = "query")
    })
    public List<JobTask> getAllScheduleJobTasks(@RequestParam(value = "size")Integer size,
                                               @RequestParam(value="index")Integer index
                                               ){
        List<JobTask> allScheduleJobTasks = scheduleService.getAllScheduleJobTasks(new Page(size, index));
        return allScheduleJobTasks;
    }


    @GetMapping("/JpaQueryBuilder")
    @ApiOperation("JpaQueryBuilder测试")
    @ResponseBody
    @ApiImplicitParams({@ApiImplicitParam(name ="KeyWord",value = "关键词",paramType = "query",required = true)})
    public String JpaQueryBuilder(@RequestParam(value="KeyWord")String KeyWord){

        //Interceptor测试
        YxStoreProduct storeProduct = yxStoreProoductRepository.findYxStoreById(716);
        YxStoreProduct product = new YxStoreProduct();
        product.copy(storeProduct);
        product.setId(null);
        yxStoreProoductRepository.save(product);

        YxStoreOrderQueryCriteria criteria = new YxStoreOrderQueryCriteria();
        criteria.setKeyword(KeyWord);
        Pageable pageable = new PageRequest(0, 2);
        org.springframework.data.domain.Page<YxStoreProduct> yxStoreProoductRepositoryAll = yxStoreProoductRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = null;
            try {
                predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return criteriaBuilder.and(predicate,criteriaBuilder.isNotNull(root.get("omProductId")));
        }, pageable);

        List<YxStoreProduct> yxStoreProoductRepositoryAllContent = yxStoreProoductRepositoryAll.getContent();

        System.out.println(yxStoreProoductRepositoryAllContent.size());

        if(!CollectionUtils.isEmpty(yxStoreProoductRepositoryAllContent)){


            String[] toArray = yxStoreProoductRepositoryAll.stream().map(yxStoreProduct -> {
                return yxStoreProduct.getKeyword();
            }).toArray(String[]::new);

            Map<String, YxStoreProduct> stringYxStoreProductMap = yxStoreProoductRepositoryAll.stream().collect(Collectors.toMap(val -> val.getKeyword(),
                    Function.identity(), (v1, v2) -> v2
            ));

            return yxStoreProoductRepositoryAll.stream().map(item->{
                return item.getKeyword();
            }).collect(Collectors.joining(","));
        }


        YxStoreProduct yxStoreProduct = yxStoreProoductRepository.findYxStoreById(712);
        if(yxStoreProduct!=null){
            return yxStoreProduct.getStoreInfo();
        }
        return "Not Found";
    }

    //一分钟内最多反问4次本接口
    @Limit(key = "test", period = 60, count = 4, name = "testLimit", prefix = "limit")
    @GetMapping("/index")
    @ApiOperation("限流测试,redis实现分布式锁测试")
    @ResponseBody
    public String indexHello(){
        String productInfo = CodeTable.codeMap.get("测试云产品");
        redisTemplate.opsForValue().set("elasticsearch","123455",10, TimeUnit.SECONDS);
        return "index";
    }


    @PostMapping("/submitForm")
    @ResponseBody
    public User submit(@RequestBody User user){
        return user;
    }
}
