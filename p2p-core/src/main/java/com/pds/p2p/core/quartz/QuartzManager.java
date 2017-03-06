package com.pds.p2p.core.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.base.Preconditions;

/**
 * <pre>
 * Cron-Expressions are used to configure instances of CronTrigger. Cron-Expressions are strings that
 * are actually made up of seven sub-expressions, that describe individual details of the schedule.
 * These sub-expression are separated with white-space, and represent:
 *
 * Seconds
 * Minutes
 * Hours
 * Day-of-Month
 * Month
 * Day-of-Week
 * Year (optional field)
 *
 * An example of a complete cron-expression is the string "0 0 12 ? * WED" - which means "every Wednesday at 12:00:00
 * pm".
 *
 * Individual sub-expressions can contain ranges and/or lists. For example, the day of week field in the previous
 * (which reads "WED")
 * example could be replaced with "MON-FRI", "MON,WED,FRI", or even "MON-WED,SAT".
 *
 * Wild-cards (the '?' character) can be used to say "every" possible value of this field.
 * Therefore the '?' character in the "Month" field of the previous example simply means "every month".
 * A '*' in the Day-Of-Week field would therefore obviously mean "every day of the week".
 *
 * All of the fields have a set of valid values that can be specified. These values should be fairly obvious
 * - such as the numbers 0 to 59 for seconds and minutes, and the values 0 to 23 for hours. Day-of-Month can be
 * any value 1-31, but you need to be careful about how many days are in a given month!
 * Months can be specified as values between 0 and 11, or by using the strings JAN, FEB,
 * MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC.
 * Days-of-Week can be specified as values between 1 and 7 (1 = Sunday) or by
 * using the strings SUN, MON, TUE, WED, THU, FRI and SAT.
 *
 * The '/' character can be used to specify increments to values. For example,
 * if you put  '0/15' in the Minutes field, it means 'every 15th minute of the hour, starting at minute zero'.
 * If you used '3/20' in the Minutes field, it would mean 'every 20th minute of the hour, starting at minute three' -
 * or in other words it is the same as specifying '3,23,43' in the Minutes field. Note the subtlety that "/35"
 *  does not mean "every 35 minutes" - it mean "every 35th minute of the hour, starting at minute zero" -
 *  or in other words the same as specifying '0,35'.
 *
 * The '?' character is allowed for the day-of-month and day-of-week fields. It is used to specify "no specific value".
 * This is useful when you need to specify something in one of the two fields, but not the other. See the examples
 *  below (and CronTrigger JavaDoc) for clarification.
 *
 * The 'L' character is allowed for the day-of-month and day-of-week fields. This character is short-hand for "last",
 * but it has different meaning in each of the two fields. For example, the value "L" in the day-of-month field means
 * "the last day of the month" - day 31 for January, day 28 for February on non-leap years.
 * If used in the day-of-week field by itself, it simply means "7" or "SAT".
 * But if used in the day-of-week field after another value, it means "the last xxx day of the month" -
 * for example "6L" or "FRIL" both mean "the last friday of the month". You can also specify an offset
 *  from the last day of the month, such as "L-3" which would mean the third-to-last day of the calendar month.
 *  When using the 'L' option, it is important not to specify lists, or ranges of values,
 *  as you'll get confusing/unexpected results.
 *
 * The 'W' is used to specify the weekday (Monday-Friday) nearest the given day. As an example, if you were to
 * specify "15W"
 * as the value for the day-of-month field, the meaning is: "the nearest weekday to the 15th of the month".
 *
 * The '#' is used to specify "the nth" XXX weekday of the month. For example, the value of "6#3" or "FRI#3" in
 * the day-of-week field means "the third Friday of the month".
 *
 * Here are a few more examples of expressions and their meanings - you can find even more in the JavaDoc for
 * org.quartz.CronExpression
 *
 * Example Cron Expressions
 *
 * CronTrigger Example 1 - an expression to create a trigger that simply fires every 5 minutes
 *
 * "0 0/5 * * * ?"
 *
 * CronTrigger Example 2 - an expression to create a trigger that fires every 5 minutes, at 10 seconds after the minute
 * (i.e. 10:00:10 am, 10:05:10 am, etc.).
 *
 * "10 0/5 * * * ?"
 *
 * CronTrigger Example 3 - an expression to create a trigger that fires at 10:30, 11:30, 12:30, and 13:30,
 * on every Wednesday and Friday.
 *
 * "0 30 10-13 ? * WED,FRI"
 *
 * CronTrigger Example 4 - an expression to create a trigger that fires every half hour between the hours of 8
 * am and 10 am on the 5th and 20th of every month. Note that the trigger will NOT fire at 10:00 am,
 * just at 8:00, 8:30, 9:00 and 9:30
 * "0 0/30 8-9 5,20 * ?"
 *
 * Note that some scheduling requirements are too complicated to express with a single trigger -
 * such as "every 5 minutes between 9:00 am and 10:00 am, and every 20 minutes between 1:00 pm and 10:00 pm".
 * The solution in this scenario is to simply create two triggers, and register both of them to run the same job.
 *
 * CronTrigger配置格式:
 *
 * 格式: [秒] [分] [小时] [日] [月] [周] [年]
 *  序号	说明
 *  是否必填	 允许填写的值	允许的通配符
 *  1	 秒	 是	 0-59 	  , - * /
 *  2	 分	 是	 0-59     , - * /
 *  3	小时	 是	 0-23	  , - * /
 *  4	 日	 是	 1-31	  , - * ? / L W
 *  5	 月	 是	 1-12 or  JAN-DEC	  , - * /
 *  6	 周	 是	 1-7  or  SUN-SAT	  , - * ? / L #
 *  7	 年	 否	 empty 或 1970-2099	  , - * /
 * 通配符说明:
 * 表示所有值. 例如:在分的字段上设置 "*",表示每一分钟都会触发。
 * ? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值。例如:要在每月的10号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为"?" 具体设置为 0 0 0 10 * ?
 * - 表示区间。例如 在小时上设置 "10-12",表示 10,11,12点都会触发。
 * , 表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发
 * / 用于递增触发。如在秒上面设置"5/15" 表示从5秒开始，每增15秒触发(5,20,35,50)。 在月字段上设置'1/3'所示每月1号开始，每隔三天触发一次。
 * L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会依据是否是润年[leap]),
 * 在周字段上表示星期六，相当于"7"或"SAT"。如果在"L"前加上数字，则表示该数据的最后一个。例如在周字段上设置"6L"这样的格式,则表示“本月最后一个星期五"
 * W 表示离指定日期的最近那个工作日(周一至周五). 例如在日字段上设置"15W"，表示离每月15号最近的那个工作日触发。如果15号正好是周六，则找最近的周五(14号)触发, 如果15号是周未，则找最近的下周一(16号)触发
 * .如果15号正好在工作日(周一至周五)，则就在该天触发。如果指定格式为 "1W",它则表示每月1号往后最近的工作日触发。如果1号正是周六，则将在3号下周一触发。(注，"W"前只能设置具体的数字,不允许区间"-").
 * 小提示
 * 'L'和 'W'可以一组合使用。如果在日字段上设置"LW",则表示在本月的最后一个工作日触发(一般指发工资 )
 *
 * # 序号(表示每月的第几个周几)，例如在周字段上设置"6#3"表示在每月的第三个周六.注意如果指定"#5",正好第五周没有周六，则不会触发该配置(用在母亲节和父亲节再合适不过了)
 * 小提示
 * 周字段的设置，若使用英文字母是不区分大小写的 MON 与mon相同.
 *
 * 常用示例:
 *
 * 0 0 12 * * ?	 		 每天12点触发
 * 0 15 10 ? * *	 	 每天10点15分触发
 * 0 15 10 * * ?		 每天10点15分触发
 * 0 15 10 * * ? *		  每天10点15分触发
 * 0 15 10 * * ? 2005	2005年每天10点15分触发
 * 0 *  14 * * ?		 每天下午的 2点到2点59分每分触发
 * 0 0/5 14 * * ?	             每天下午的 2点到2点59分(整点开始，每隔5分触发)
 * 0 0/5 14,18 * * ?	 每天下午的 2点到2点59分(整点开始，每隔5分触发),每天下午的 18点到18点59分(整点开始，每隔5分触发)
 * 0 0-5 14 * * ?	             每天下午的 2点到2点05分每分触发
 * 0 10,44 14 ? 3 WED	3月分每周三下午的 2点10分和2点44分触发
 * 0 15 10 ? * MON-FRI	 从周一到周五每天上午的10点15分触发
 * 0 15 10 15 * ?	 每月15号上午10点15分触发
 * 0 15 10 L * ?	 每月最后一天的10点15分触发
 * 0 15 10 ? * 6L	 每月最后一周的星期五的10点15分触发
 * 0 15 10 ? * 6L 2002-2005	 从2002年到2005年每月最后一周的星期五的10点15分触发
 * 0 15 10 ? * 6#3	 每月的第三周的星期五开始触发
 * 0 0 12 1/5 * ?	 每月的第一个中午开始每隔5天触发一次
 * 0 11 11 11 11 ?	 每年的11月11号 11点11分触发(光棍节)
 * </pre>
 *
 * @author Administrator
 */
public class QuartzManager {
    private static SchedulerFactory sf = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "group1";
    private static String TRIGGER_GROUP_NAME = "trigger1";

    private boolean started = false;

    public QuartzManager() {
    }

    public boolean isStarted() {
        return started;
    }

    /***
     * 每日几点几分，开始执行任务
     *
     * @param jobName  任务名称
     * @param jobClass 任务类
     * @param hour     小时
     * @param minute   分
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, int hour, int minute)
            throws SchedulerException {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.dailyAtHourAndMinute(hour, minute);
        this.addJob(jobName, jobClass, scheduleBuilder);
    }

    /***
     * 按scheduleBuilder指定的时间进行
     *
     * @param jobName
     * @param jobClass
     * @param scheduleBuilder
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, CronScheduleBuilder scheduleBuilder)
            throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        JobDetail job = newJob(jobClass).withIdentity(jobName, JOB_GROUP_NAME).build();
        Trigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME).withSchedule(scheduleBuilder).build();
        sched.scheduleJob(job, trigger);
    }

    /***
     * @param jobName
     * @param jobClass
     * @param delay          延迟时长
     * @param delaytimeUnit  延时时间单位
     * @param period         执行周期
     * @param periodtimeUnit 执行周期时间单位
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, int delay, IntervalUnit delaytimeUnit,
                                       int period, IntervalUnit periodtimeUnit) throws SchedulerException {
        this.addJob(jobName, jobClass, delay, delaytimeUnit, period, periodtimeUnit, 0);
    }

    public <T extends Job> void addJob(String jobName, Class<T> jobClass, int delay, IntervalUnit delaytimeUnit,
                                       int period, IntervalUnit periodtimeUnit, Map<String, Object> parms)
            throws SchedulerException {
        this.addJob(jobName, jobClass, delay, delaytimeUnit, period, periodtimeUnit, 0, parms);
    }

    /***
     * 使用conf确定延时与间隔执行的时间
     *
     * @param jobName
     * @param jobClass
     * @param conf
     * @param parms
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, Map<String, Object> parms,
                                       Map<String, Object> conf) throws SchedulerException {
        String delay = Preconditions.checkNotNull(MapUtils.getString(conf, "delay"), "There no delay in conf");
        String period = Preconditions.checkNotNull(MapUtils.getString(conf, "period"), "There no period in conf");
        IntervalUnit delayIntervalUnit = transIntervalUnit(delay);
        IntervalUnit periodIntervalUnit = transIntervalUnit(period);
        this.addJob(jobName, jobClass, transInterval(delay), delayIntervalUnit, transInterval(period),
                periodIntervalUnit, 0, parms);
    }

    protected int transInterval(String delay) {
        return org.apache.commons.lang3.math.NumberUtils.toInt(delay.substring(0, delay.length() - 1));
    }

    protected IntervalUnit transIntervalUnit(String delay) {
        char c = delay.charAt(delay.length() - 1);
        IntervalUnit intervalUnit = null;
        if (c == 's') {
            intervalUnit = IntervalUnit.SECOND;
        } else if (c == 'm') {
            intervalUnit = IntervalUnit.MINUTE;
        } else if (c == 'H') {
            intervalUnit = IntervalUnit.HOUR;
        }
        Preconditions.checkNotNull(intervalUnit, "intervalUnit no conf");
        return intervalUnit;
    }

    /***
     * @param jobClass
     * @param delay          延迟时长
     * @param delaytimeUnit  延时时间单位
     * @param period         执行周期
     * @param periodtimeUnit 执行周期时间单位
     * @param count          执行次数：count-1次
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, int delay, IntervalUnit delaytimeUnit,
                                       int period, IntervalUnit periodtimeUnit, int count) throws SchedulerException {
        this.addJob(jobName, jobClass, DateBuilder.futureDate(delay, delaytimeUnit), period, periodtimeUnit, count,
                null);
    }

    public <T extends Job> void addJob(String jobName, Class<T> jobClass, int delay, IntervalUnit delaytimeUnit,
                                       int period, IntervalUnit periodtimeUnit, int count, Map<String, Object> parms)
            throws SchedulerException {
        this.addJob(jobName, jobClass, DateBuilder.futureDate(delay, delaytimeUnit), period, periodtimeUnit, count,
                parms);
    }

    /***
     * @param jobName
     * @param jobClass
     * @param date           开始执行时间
     * @param period         执行周期
     * @param periodtimeUnit 执行周期的时间单位
     * @param count          总共执行次数:count-1次
     *
     * @throws SchedulerException
     */
    public <T extends Job> void addJob(String jobName, Class<T> jobClass, Date date, int period, IntervalUnit
            periodtimeUnit, int count, Map<String, Object> parms) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        JobDetail job = newJob(jobClass).withIdentity(jobName, JOB_GROUP_NAME).build();
        if (parms != null) {
            job.getJobDataMap().putAll(parms);
        }
        SimpleScheduleBuilder scheduleBuilder = buidScheduleBuilder(period, periodtimeUnit, count);
        Trigger trigger =
                newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME).startAt(date).withSchedule(scheduleBuilder)
                        .build();
        sched.scheduleJob(job, trigger);
    }

    private SimpleScheduleBuilder buidScheduleBuilder(int period, IntervalUnit timeUnit, int count) {
        SimpleScheduleBuilder scheduleBuilder = null;
        if (timeUnit == IntervalUnit.DAY) {
            if (count <= 1) {
                scheduleBuilder =
                        SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24 * period).repeatForever();
            } else {
                scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24 * period)
                        .withRepeatCount(count - 1);
            }
        } else if (timeUnit == IntervalUnit.HOUR) {
            if (count <= 1) {
                scheduleBuilder = SimpleScheduleBuilder.repeatHourlyForever(period);
            } else {
                scheduleBuilder = SimpleScheduleBuilder.repeatHourlyForTotalCount(count, period);
            }
        } else if (timeUnit == IntervalUnit.MINUTE) {
            if (count <= 1) {
                scheduleBuilder = SimpleScheduleBuilder.repeatMinutelyForever(period);
            } else {
                scheduleBuilder = SimpleScheduleBuilder.repeatMinutelyForTotalCount(count, period);
            }
        } else if (timeUnit == IntervalUnit.SECOND) {
            if (count <= 1) {
                scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(period);
            } else {
                scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, period);
            }
        } else {
            return null;
        }
        return scheduleBuilder;
    }

    public <T extends Job> void addJob(String jobName, Class<T> jobClass, String cronExpression)
            throws SchedulerException {
        this.addJob(jobName, jobClass, null, cronExpression);
    }

    public <T extends Job> void addJob(String jobName, Class<T> jobClass, Map<String, Object> parms,
                                       String cronExpression) throws SchedulerException {
        this.addJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME, jobClass, cronExpression, parms);
    }

    public <T extends Job> void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                                       Class<T> jobClass, String cronExpression, Map<String, Object> parms)
            throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        JobDetail job = newJob(jobClass).withIdentity(jobName, jobGroupName).build();//
        if (parms != null) {
            job.getJobDataMap().putAll(parms);
        }
        Trigger trigger =
                newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(cronSchedule(cronExpression))
                        .build();
        sched.scheduleJob(job, trigger);
    }

    public void modifyJobTime(String jobName, String cronExpression) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        TriggerKey triggerKey = new TriggerKey(jobName, TRIGGER_GROUP_NAME);
        Trigger newTrigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(cronExpression)).build();
        sched.rescheduleJob(triggerKey, newTrigger);
    }

    public void removeJob(String jobName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        TriggerKey triggerKey = new TriggerKey(jobName, TRIGGER_GROUP_NAME);
        sched.pauseTrigger(triggerKey);// 停止触发器
        sched.unscheduleJob(triggerKey);// 移除触发器
        sched.deleteJob(new JobKey(jobName, JOB_GROUP_NAME));// 删除任务
    }

    public void shutdown() throws SchedulerException {
        sf.getScheduler().shutdown(true);
    }

    public void start() throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        if (!sched.isShutdown() && !started) {
            sched.start();
            started = true;
        }
    }

}
