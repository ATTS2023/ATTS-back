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
     * 初始化浏览器,并设置为不关闭状态
     * @return
     */
    private WebDriver brower_getdriver(){
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability("moz:firefoxOptions", Map.of(
                "prefs", Map.of(
                        "fission.webContentIsolationStrategy", 0,
                        "fission.bfcacheInParent", false
                )
        ));
        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://newcas.gzhu.edu.cn/cas/login?service=https%3A%2F%2Fnewmy.gzhu.edu.cn%2Fup%2Fview%3Fm%3Dup");
        return driver;
    }
    public String getCousrse() throws InterruptedException {
        WebDriver driver = this.brower_getdriver();
        WebElement un = driver.findElement(By.id("un"));
        WebElement pd = driver.findElement(By.id("pd"));

        un.sendKeys("32206200022");
        pd.sendKeys("20030112Chen@");
        WebElement btn = driver.findElement(By.id("index_login_btn"));
        btn.click();
        String new_url = "http://jwxt.gzhu.edu.cn/sso/driot4login";
        driver.get(new_url);
        Thread.sleep(6000);
        WebElement btn_sec = driver.findElement(By.id("btn_yd"));
        btn_sec.click();
        new_url = "http://jwxt.gzhu.edu.cn/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default&su=";
        driver.get(new_url);
        Thread.sleep(1000);
        String elestr = "";
        for (int i = 1; i <= 7; i = i + 1) {
            for (int j = 1; j <= 9; j = j + 2) {
                String courseId = i + "-" + j;
                log.info(courseId);
                try {
                    WebElement Eachcourse = driver.findElement(By.id(courseId));
                    String EachcourseHTML = Eachcourse.getAttribute("outerHTML");
                    Document Coursedoc = Jsoup.parse(EachcourseHTML);
                    elestr += Coursedoc.text();
                    elestr += " ";
                    log.info(elestr);
                }catch (NoSuchElementException e) {
                // 处理元素未找到的情况
                }
            }
            elestr += "\n";
        }
        driver.quit();
        return elestr;
    }
    public List<CourseSchedule> getCourse() throws InterruptedException {
        String course = this.getCousrse();
        String pattern = "(.*?)(\\n|$)";
        String[] strarray = new String[8];
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(course);

        int week = 0;//第几组 0-6 代表 星期1-7
        while (m.find()) {
            String matchedText = m.group(1).trim().replaceAll("\\n", ""); // 获取匹配的文本并去除首尾空格
            strarray[week] = matchedText;
            week++;
        }

        List<CourseSchedule> list = new ArrayList<>();//创建链表,存储课程
        int count = 0;//利用这个记录样第几个数据被写入,满5个时,创建并写入类里面
        int remainder = 0;//求模后的余数,用于判断是第几个数据,如:余数1为课程名称,2为时间

        String CourseName = "";
        String teacher = "";
        String classRoom = "";
        String WeekTimes = "";
        int WeekDate;
        String Session = "";

        for (int i = 0; i < 7; i++) {
            String[] elestr = strarray[i].split("\\s+");//依据空格分开
            WeekDate = i + 1;
            for (int j = 0; j < elestr.length; j++) {
                remainder = j % 12;
                if(remainder == 0){
                    CourseName = elestr[j];
                    count++;
                }else if(remainder == 1){
                    //利用正则表达式,分开(5-6节) 上课时间 和 第4周 周次
                    Pattern pTime = Pattern.compile("\\((.*?)\\)(.*)");
                    Matcher mTIme = pTime.matcher(elestr[j]);
                    if(mTIme.find()){
                        String insideBrackets = mTIme.group(1); // 获取括号内的内容
                        String outsideBrackets = mTIme.group(2); // 获取括号外的内容
                        Session = insideBrackets;
                        WeekTimes = outsideBrackets;
                    }
                    count++;
                }else if(remainder == 2){
                    classRoom = elestr[j];
                    count++;
                }else if(remainder == 3){
                    classRoom += elestr[j];
                    count++;
                }else if(remainder == 4){
                    teacher = elestr[j];
                    count++;
                    if(count == 5){
                        CourseSchedule added = new CourseSchedule(CourseName,teacher,classRoom,WeekTimes,WeekDate,Session);
                        CourseName = "";
                        teacher = "";
                        classRoom = "";
                        WeekTimes = "";
                        Session = "";
                        list.add(added);
                        count = 0;
                    }
                }
            }
        }
        return list;
    }
}
