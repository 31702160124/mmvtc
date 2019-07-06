package com.funsoul.jwcmmvtc.config;

public class urlConfig {

    public final static String baseurl = "http://jwc.mmvtc.cn/";

    //登入
    public final static String checkCodeUrl = "http://jwc.mmvtc.cn/CheckCode.aspx";
    public final static String loginurl = "http://jwc.mmvtc.cn/default2.aspx";
    public final static String[] loginfrom = new String[]{
            "__VIEWSTATE", "dDw3OTkxMjIwNTU7Oz5qFv56B08dbR82AMSOW+P8WDKexA==",
            "TextBox1", "",
            "TextBox2", "",
            "TextBox3", "",
            "RadioButtonList1", "学生",
            "Button1", "",};
    public final static String[] jwcheader = new String[]{
            "Content-Type", "application/x-www-form-urlencoded",
            "Referer", "http://jwc.mmvtc.cn/xscjcx.aspx?xh=",
            "Cookie", ""};
    //退出
    public final static String loginOutUrl = "http://jwc.mmvtc.cn/xs_main.aspx?xh=";
    public final static String[] loginoutfrom = new String[]{
            "__EVENTTARGET", "likTc",
            "__VIEWSTATE", "dDwtMTM0NTkyMTI1NDs7Pvc/28t3Ic9R5SInH5252nziPfHq",
            "__EVENTARGUMENT", ""};

    //判断cookie是否过期
    public final static String cookieisoverUrl = "http://jwc.mmvtc.cn/xs_main.aspx?xh=";
    public final static String[] jwccookieheader = new String[]{
            "Cookie", "",
            "Referer", "http://jwc.mmvtc.cn/xscjcx.aspx?xh="};
}
