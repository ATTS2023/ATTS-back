package com.atts.attschedule.utils;

import com.atts.attschedule.Entity.CourseSchedule;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取课程表
 */
@Slf4j
public class FindCourseString {
    /**
     * <h3>初始化浏览器,并设置为不关闭状态</h3>
     * <h3>driver打开广大官网登录页面</h3>
     *
     * @return driver,打开了广大官网登录页面的driver
     */
    private WebDriver brower_getdriver(){
        //1.设置控制项
        FirefoxOptions options = new FirefoxOptions();
        //2.精细化控制处理浏览器行为
        options.setCapability("moz:firefoxOptions", Map.of(
                "prefs", Map.of(
                        "fission.webContentIsolationStrategy", 0,
                        "fission.bfcacheInParent", false
                )
        ));
        //3.driver打开广大官网,并返回driver
        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://newcas.gzhu.edu.cn/cas/login?service=https%3A%2F%2Fnewmy.gzhu.edu.cn%2Fup%2Fview%3Fm%3Dup");
        return driver;
    }

    /**
     * <h3>获取生课程字符串</h3>
     *
     * @return elestr,最终获取生课程的字符串(课程日期以换行符区分)
     * @throws InterruptedException
     */
    public String getCousrse() throws InterruptedException {
        //第一步:进入到课表页面,等待爬取
        //1.1 获取广大官网登录页面,并获取相应的登录账号以及登录密码框
        WebDriver driver = this.brower_getdriver();
        WebElement un = driver.findElement(By.id("un"));
        WebElement pd = driver.findElement(By.id("pd"));

        //1.2 填写账号密码,并点击确认键进行登录
        un.sendKeys("your-id");
        pd.sendKeys("your-password");
        WebElement btn = driver.findElement(By.id("index_login_btn"));
        btn.click();

        //1.3 进入到数字广大-融合门户
        String new_url = "http://jwxt.gzhu.edu.cn/sso/driot4login";
        driver.get(new_url);

        //1.4 等待6秒后进入到教学系统平台
        Thread.sleep(6000);
        WebElement btn_sec = driver.findElement(By.id("btn_yd"));
        btn_sec.click();

        //1.5 进入到课表页面,等待一秒钟,以便课表元素的生成
        new_url = "http://jwxt.gzhu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default&su=";
        driver.get(new_url);
        Thread.sleep(1000);

        //第二步骤 正式爬取生课表字符串
        //2.1 使用elestr进行生字符串的存储,遍历id为 1-1 至 7-11 的课表数据
        String elestr = "";
        for (int i = 1; i <= 7; i = i + 1) {
            for (int j = 1; j <= 11; j = j + 1) {
                String courseId = i + "-" + j;
                //2.2 获取id为courserId的元素,并添加到elestr中
                try {
                    WebElement Eachcourse = driver.findElement(By.id(courseId));
                    String EachcourseHTML = Eachcourse.getAttribute("outerHTML");
                    Document Coursedoc = Jsoup.parse(EachcourseHTML);
                    elestr += Coursedoc.text();
                    elestr += " ";
                }catch (NoSuchElementException e) {
                // 处理元素未找到的情况
                }
            }
            //2.3 每完成一天的爬取,添加一个\n ,换行符,以便解析数据时易于区分星期
            elestr += "\n";
        }
        //2.4 关闭浏览器,并返回生课表字符串
        driver.quit();
        return elestr;
    }

    /**
     * <h3>解析生课表字符串,并创建对象后返回List集合</h3>
     *
     * @return list,存储具体课表对象的集合
     * @throws InterruptedException
     */
    public List<CourseSchedule> getCourse() throws InterruptedException {
        //第一步 获取到生课表字符串
        //1.1 将生课表字符串的  * & 全部去除
        String course = this.getCousrse();
        course = course.replaceAll("[*&]", "");
        /*log.info("课表:");
        log.info(course);*/

        //第二步 解析生课表字符串,(依照空格进行切分)存储进strarray中
        //2.1 将生课表字符串拆分,分为7天(星期一至七),每个strarray 为当天的生课表字符串
        String pattern = "(.*?)(\\n|$)";
        String[] strarray = new String[8];
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(course);

        //2.2 将每天的生课表字符串存储进strarray中 0-6 分别代表 星期一至七
        int week = 0;
        while (m.find()) {
            String matchedText = m.group(1).trim().replaceAll("\\n", ""); // 获取匹配的文本并去除首尾空格
            strarray[week] = matchedText;
            week++;
        }

        //第三步 将生课表字符串放进自定义对象中,并作为list集合返回
        //3.1 创建链表,存储课程
        List<CourseSchedule> list = new ArrayList<>();
        int count = 0;//记录第几个数据被写入,满5个时,创建并写入CourseSchedule类
        int remainder = 0;//求模后的余数,用于判断是第几个数据,如:余数1为课程名称,2为时间,3为课程地点

        //3.2 从生课表字符串获取到的具体课程信息
        String CourseName = "";//课程名称
        String teacher = "";//课程教师
        String classRoom = "";//课程地点
        String WeekTimes = "";//课程周次
        int WeekDate; //课程日期(周一/周二/..),由strarray[i]中的i确定,0-6分别代表星期一至七
        String Session = "";//课程节次(第5-6节/第1-2节)

        //3.3 遍历处理星期一至七的课程
        for (int i = 0; i < 7; i++) {
            //3.3.1 先将星期i+1 的课程 按照空格切分开,放进elestr数组中
            String[] elestr = strarray[i].split("\\s+");//依据空格分开
            WeekDate = i + 1;//课程日期(周一/周二/..)
            for (int j = 0; j < elestr.length; j++) {
                //3.3.2 按照余数,确定爬取到的数据为  课程名称/课程时间/课程教师/课程地点....
                remainder = j % 12;
                if(remainder == 0){
                    //课程名称
                    CourseName = elestr[j];
                    count++;
                }else if(remainder == 1){
                    //3.3.3 利用正则表达式切割课程时间为 课程节次(5-6节) 和 课程周次(第1-16周)
                    Pattern pTime = Pattern.compile("\\((.*?)\\)(.*)");
                    Matcher mTIme = pTime.matcher(elestr[j]);
                    if(mTIme.find()){
                        String insideBrackets = mTIme.group(1); // 获取括号内的内容,即课程节次
                        String outsideBrackets = mTIme.group(2); // 获取括号外的内容,即课程授课周数
                        Session = insideBrackets;
                        WeekTimes = outsideBrackets;
                    }
                    count++;
                }else if(remainder == 2){
                    //课程地点(大学城/桂花岗/..)
                    classRoom = elestr[j];
                    count++;
                }else if(remainder == 3){
                    //课程地点(具体教室)
                    classRoom += elestr[j];
                    count++;
                }else if(remainder == 4){
                    //课程教师
                    teacher = elestr[j];
                    count++;
                    //3.3.4 课程教师位列爬取数据中末位,当爬取至课程教师后即可创建CourseSchedule对象
                    if(count == 5){
                        CourseSchedule added = new CourseSchedule(CourseName,teacher,classRoom,WeekTimes,WeekDate,Session);
                        //写入后空数据
                        CourseName = "";
                        teacher = "";
                        classRoom = "";
                        WeekTimes = "";
                        Session = "";
                        //3.3.5 最后将课程添加至list中,以待返回
                        list.add(added);
                        count = 0;
                    }
                }
            }
        }
        return list;
    }


}
