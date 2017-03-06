package com.pds.p2p.system.quartz.controller;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.pds.p2p.core.j2ee.action.JsonResult;
import com.pds.p2p.system.quartz.domain.ScheduleJob;
import com.pds.p2p.system.quartz.service.JobTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pds.p2p.core.j2ee.context.SpringContextHolder;
import com.pds.p2p.core.j2ee.web.BaseController;

@Controller
@RequestMapping("/task")
public class JobTaskController extends BaseController {
    // 日志记录器
    private static Logger logger = LogManager.getLogger(JobTaskController.class);
//    @Autowired
    private JobTaskService taskService;

    @RequestMapping("taskList")
    public String taskList(HttpServletRequest request) {
        List<ScheduleJob> taskList = taskService.getAllTask();
        request.setAttribute("taskList", taskList);
        return "quartz/task_index";
    }

    @RequestMapping("add")
    @ResponseBody
    public JsonResult<?> addTask(HttpServletRequest request, ScheduleJob scheduleJob) {
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        } catch (Exception e) {
            return JsonResult.failJsonResult("cron表达式有误，不能被解析！");
        }
        Object obj = null;
        try {
            if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
                obj = SpringContextHolder.getBean(scheduleJob.getSpringId(), Object.class);
            } else {
                Class clazz = Class.forName(scheduleJob.getBeanClass());
                obj = clazz.newInstance();
            }
        } catch (Exception e) {
        }
        if (obj == null) {
            return JsonResult.failJsonResult("未找到目标类！");
        } else {
            Class clazz = obj.getClass();
            Method method = null;
            try {
                method = clazz.getMethod(scheduleJob.getMethodName(), null);
            } catch (Exception e) {
            }
            if (method == null) {
                return JsonResult.failJsonResult("未找到目标方法!");
            }
        }
        try {
            taskService.addTask(scheduleJob);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failJsonResult("保存失败，检查 name group 组合是否有重复！");
        }

        return JsonResult.okJsonResult();
    }

    @RequestMapping("changeJobStatus")
    @ResponseBody
    public JsonResult<?> changeJobStatus(HttpServletRequest request, Long jobId, String cmd) {
        try {
            taskService.changeStatus(jobId, cmd);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return JsonResult.failJsonResult("任务状态改变失败！");
        }
        return JsonResult.okJsonResult();
    }

    @RequestMapping("updateCron")
    @ResponseBody
    public JsonResult<?> updateCron(HttpServletRequest request, Long jobId, String cron) {
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        } catch (Exception e) {
            return JsonResult.failJsonResult("cron表达式有误，不能被解析！");
        }
        try {
            taskService.updateCron(jobId, cron);
        } catch (SchedulerException e) {
            return JsonResult.failJsonResult("cron更新失败！");
        }
        return JsonResult.okJsonResult();
    }

    @RequestMapping("runAJobNow")
    @ResponseBody
    public JsonResult<?> runAJobNow(HttpServletRequest request, Long jobId) {
        try {
            ScheduleJob job = taskService.getTaskById(jobId);
            taskService.runAJobNow(job);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return JsonResult.failJsonResult(e.getMessage());
        }
        return JsonResult.okJsonResult();
    }
}
