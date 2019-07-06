package com.funsoul.jwcmmvtc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.funsoul.jwcmmvtc.Interface.Ibitmap;
import com.funsoul.jwcmmvtc.Interface.Ichengji;
import com.funsoul.jwcmmvtc.Interface.Icookie;
import com.funsoul.jwcmmvtc.Interface.Ikebiao;
import com.funsoul.jwcmmvtc.Interface.Istudentinfo;
import com.funsoul.jwcmmvtc.config.okhttpConfig;
import com.funsoul.jwcmmvtc.config.urlConfig;
import com.funsoul.jwcmmvtc.config.userConfig;
import com.funsoul.jwcmmvtc.entity.kebiao;
import com.funsoul.jwcmmvtc.entity.user;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class jwcDao {
    public static OkHttpClient client = okhttpConfig.getClient(okhttpConfig.getjwcDao());
    private static String ViewState = "";
    private static String LogOutViewState = "";
    private static String jwcCookie = "";
    // 头像
    private static String HPicSrc;

    //cookie检测
    public static void checkCookie(final Icookie icookie) {
        Request request = new Request.Builder()
                .url(urlConfig.cookieisoverUrl + userConfig.getUsername())
                .addHeader(urlConfig.jwccookieheader[0], userConfig.getCookie())
                .addHeader(urlConfig.jwccookieheader[2], urlConfig.jwccookieheader[3] + userConfig.getUsername())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                String str1 = "Object moved"; //html.contains(str1)
                String str2 = "正方教务管理系统";//!html.contains(str2)
                if (html.contains(str1)) {
                    //cookie 失效
                    icookie.cookie(false);
                    userConfig.addError();
                    userConfig.setCookie("");
                    userConfig.saveIslogin(false);
                } else {
                    //cookie 可用
                    icookie.cookie(true);
                    userConfig.delError();
                    userConfig.saveIslogin(true);
                }
            }
        });
    }

    //获取登入所需的ViewState
    private static String findViewState(String html) {
        if (html.equals("")) {
            return "";
        }
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByAttributeValue("name", "__VIEWSTATE");
        return elements.first().val();
    }

    //获取验证码
    public static Bitmap getCheckCodeImg() throws IOException {
        Request request = new Request.Builder().url(urlConfig.baseurl).build();
        Response response = client.newCall(request).execute();
        ViewState = findViewState(response.body().string());

        request = new Request.Builder().url(urlConfig.checkCodeUrl).build();
        response = client.newCall(request).execute();
        byte[] body = response.body().bytes();
        Bitmap data = BitmapFactory.decodeByteArray(body, 0, body.length);
        Headers headers = response.headers();
        List<String> cookies = headers.values("Set-Cookie");
        for (String str : cookies) {
            String s = str.substring(0, str.indexOf(";"));
            jwcCookie = s;
        }
        return data;
    }

    //登入
    public static String[] Login(String user, String pwd, String checkCode) {
        if (ViewState.equals("")) {
            ViewState = urlConfig.loginfrom[1];
        }

        RequestBody body = new FormBody.Builder()
                .add(urlConfig.loginfrom[0], ViewState)
                .add(urlConfig.loginfrom[2], user)
                .add(urlConfig.loginfrom[4], pwd)
                .add(urlConfig.loginfrom[6], checkCode)
                .add(urlConfig.loginfrom[8], urlConfig.loginfrom[9])
                .add(urlConfig.loginfrom[10], urlConfig.loginfrom[11])
                .build();
        Request request = new Request.Builder()
                .url(urlConfig.loginurl)
                .post(body)
                .addHeader(urlConfig.jwcheader[0], urlConfig.jwcheader[1])
                .addHeader(urlConfig.jwcheader[4], jwcCookie)
                .addHeader("Host", "jwc.mmvtc.cn")
                .addHeader("Referer", "http://jwc.mmvtc.cn/default2.aspx")
                .build();
        String html = "";
        try {
            Response response = client.newCall(request).execute();
            html = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
            验证码不正确！！
            用户名不存在或未按照要求参加教学活动！！
            密码错误，您还有4次尝试机会！如忘记密码，请与教务处联系!
            密码错误，您密码输入错误已达5次，账号已锁定无法登录，次日自动解锁！如忘记密码，请与教务处联系!
         */

        String status, info;
        String[] rs = new String[2];
        if (html.contains("验证码不正确！！")) {
            status = "checkCode_error";
            info = "验证码不正确";
        } else if (html.contains("密码错误，您还有")) {
            status = "pwd_error";
            //使用正则
            String pattern = "密码错误.+?有(\\d+)次尝试机会";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(html);
            if (m.find()) {
                info = m.group();
            } else {
                info = "密码错误，请重新输入";
            }
        } else if (html.contains("错误已达5次，账号已锁定")) {
            status = "user_locked";
            info = "账号已锁定无法登录，次日自动解锁";
        } else if (html.contains("用户名不存在或未按照要求参加教学活动！！")) {
            status = "user_error";
            info = "用户名不存在";
        } else {
            status = "ok";
            Document doc = Jsoup.parse(html);
            info = doc.selectFirst(".info > ul > li:first-child").text();
            userConfig.setCookie(jwcCookie);
            userConfig.saveUserConfig(user, pwd);
            userConfig.setname(info);
            userConfig.saveIslogin(true);
            queryURL(doc);
            Elements elements = doc.getElementsByAttributeValue("name", "__VIEWSTATE");
            LogOutViewState = elements.first().val();
        }

        rs[0] = status;
        rs[1] = info;

        return rs;
    }

    //退出
    public static void jwcloginOut() {
        if (LogOutViewState.isEmpty()) {
            LogOutViewState = urlConfig.loginoutfrom[3];
        }
        RequestBody body = new FormBody.Builder()
                .add(urlConfig.loginoutfrom[0], urlConfig.loginoutfrom[1])
                .add(urlConfig.loginoutfrom[2], LogOutViewState)
                .add(urlConfig.loginoutfrom[4], urlConfig.loginoutfrom[5])
                .build();
        Request request = new Request.Builder()
                .url(urlConfig.loginOutUrl + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[0], urlConfig.jwcheader[1])
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String html = response.body().string();
            if (html.contains("登录")) {
                userConfig.setCookie("");
            } else {
                userConfig.setCookie("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            userConfig.setCookie("");
            userConfig.setHPicSrc("");
            userConfig.delUrlbody();
        }
    }

    //获取个人信息
    public static void getPersonalInfo(final Istudentinfo istudentinfo, final Ibitmap ibitmap) {
        String url = urlConfig.baseurl + userConfig.getGrxxUrl();
        Request request = new Request.Builder().url(url)
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                // 部分个人信息 用于显示在手机界面上
                String xh = doc.getElementById("xh").text();
                String xm = doc.getElementById("xm").text();
                String sex = doc.getElementById("lbl_xb").text();
                String zzmm = doc.getElementById("lbl_zzmm").text();
                String xi = doc.getElementById("lbl_xi").text();
                String zymc = doc.getElementById("lbl_zymc").text();
                String xzb = doc.getElementById("lbl_xzb").text();
                String dqszj = doc.getElementById("lbl_dqszj").text();
                String rxrq = doc.getElementById("lbl_rxrq").text();
                String xlcc = doc.getElementById("lbl_CC").text();
                String xxxs = doc.getElementById("lbl_xxxs").text();
                String xz = doc.getElementById("lbl_xz").text();
                String xjzt = doc.getElementById("lbl_xjzt").text();

                // 学生照片地址
                HPicSrc = urlConfig.baseurl + doc.getElementById("xszp").attr("src");
                userConfig.setHPicSrc(HPicSrc);

                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("xh", xh);
                infoMap.put("xm", xm);
                infoMap.put("sex", sex);
                infoMap.put("zzmm", zzmm);
                infoMap.put("xi", xi);
                infoMap.put("zymc", zymc);
                infoMap.put("xzb", xzb);
                infoMap.put("rxrq", rxrq);
                infoMap.put("xz", xz);
                infoMap.put("xjzt", xjzt);
                infoMap.put("dqszj", dqszj);
                infoMap.put("xlcc", xlcc);
                infoMap.put("xxxs", xxxs);
                istudentinfo.setMap(infoMap);
                Request request = new Request.Builder()
                        .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                        .url(HPicSrc).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        byte[] bytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ibitmap.setBitmap(bitmap);
                    }
                });
            }
        });
    }

    // 获取历年成绩
    public static void getchengji(final Ichengji ichengji) {

        // GET 学生成绩查询页面 得到 __VIEWSTATE
        Request request = new Request.Builder()
                .url(urlConfig.baseurl + userConfig.getCjcxUrl())
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strHTML = response.body().string();
                String vs = findViewState(strHTML);
                // 如果获取不到vs值 则采用固定的值
                if (vs.equals("")) {
                    vs = "dDwtMTAyNzg2MDg2Njt0PHA8bDx4aDtkeWJ5c2NqO3p4Y2pjeHhzO3NmZGNiaztTb3J0RXhwcmVzO1NvcnREaXJlO2RnMztzdHJfdGFiX2JqZzs+O2w8MzE3MDIxNjAxMTE7XGU7MDtcZTtrY21jO2FzYztiamc7emZfY3hjanRqXzMxNzAyMTYwMTExOz4+O2w8aTwxPjs+O2w8dDw7bDxpPDI+O2k8Nz47aTwyMz47aTwyOT47aTwzMT47aTwzMz47aTwzNT47aTwzNj47aTwzOD47aTw0MD47aTw0Mj47aTw0ND47aTw0Nj47aTw0OD47aTw1MD47aTw1Mj47aTw1ND47aTw1Nj47aTw1Nz47aTw1OT47aTw2MT47aTw2Mz47aTw2NT47aTw2Nz47PjtsPHQ8dDw7dDxpPDIwPjtAPFxlOzIwMDEtMjAwMjsyMDAyLTIwMDM7MjAwMy0yMDA0OzIwMDQtMjAwNTsyMDA1LTIwMDY7MjAwNi0yMDA3OzIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzsyMDE3LTIwMTg7MjAxOC0yMDE5OzIwMTktMjAyMDs+O0A8XGU7MjAwMS0yMDAyOzIwMDItMjAwMzsyMDAzLTIwMDQ7MjAwNC0yMDA1OzIwMDUtMjAwNjsyMDA2LTIwMDc7MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7MjAxMy0yMDE0OzIwMTQtMjAxNTsyMDE1LTIwMTY7MjAxNi0yMDE3OzIwMTctMjAxODsyMDE4LTIwMTk7MjAxOS0yMDIwOz4+Oz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGtjeHptYztrY3h6ZG07Pj47Pjt0PGk8NT47QDzlv4Xkv67or7475LiT5Lia6YCJ5L+u6K++O+S7u+aEj+mAieS/ruivvjvmoKHkvIHlvIDlj5Hor747XGU7PjtAPDAxOzAyOzAzOzA0O1xlOz4+Oz47Oz47dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWtpuWPt++8mjMxNzAyMTYwMTExO288dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w85aeT5ZCN77ya6ZmI5aWV5qGmO288dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w85a2m6Zmi77ya6K6h566X5py65bel56iL57O7O288dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w85LiT5Lia77yaO288dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w86L2v5Lu25oqA5pyvO288dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w86KGM5pS/54+t77yaMTfnp7vliqjkupLogZQxO288dD47Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjt0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MD47aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47Pj47dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+O2w8aTwwPjs+O2w8dDw7bDxpPDA+O2k8MT47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs+Pj47Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MD47aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47Pj47dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47cDxsPHN0eWxlOz47bDxESVNQTEFZOm5vbmU7Pj4+Ozs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+O2w8aTwwPjtpPDE+O2k8Mj47aTwzPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w85pys5LiT5Lia5YWxMjQ45Lq6O288Zj47Pj47Pjs7Pjs+Pjt0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+Pjs+Ozs+Oz4+O3Q8O2w8aTwwPjs+O2w8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47Oz47Pj47dDw7bDxpPDA+Oz47bDx0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjs+Pjs+Pjt0PHA8cDxsPFRleHQ7PjtsPHpmOz4+Oz47Oz47dDxwPHA8bDxJbWFnZVVybDs+O2w8Li9leGNlbC85NzA1MjcwLmpwZzs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDzoh7Pku4rmnKrpgJrov4for77nqIvmiJDnu6nvvJo7bzx0Pjs+Pjs+Ozs+O3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs+O2w8aTwxPjtpPDA+O2k8MD47bDw+Oz4+O3A8bDxzdHlsZTs+O2w8RElTUExBWTpibG9jazs+Pj47Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8Ozs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPHQ+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+tA32flyhvuUmh7aAtanH9ixN/O8=";
                }

                // POST
                RequestBody body = new FormBody.Builder()
                        .add("__EVENTTARGET", "")
                        .add("__EVENTARGUMENT", "")
                        .add("__VIEWSTATE", vs)
                        .add("ddlXN", "")
                        .add("ddlXQ", "")
                        .add("ddl_kcxz", "")
                        .add("btn_zcj", "历年成绩")
                        .build();
                Request request2 = new Request.Builder()
                        .url(urlConfig.baseurl + userConfig.getCjcxUrl())
                        .addHeader(urlConfig.jwcheader[0], urlConfig.jwcheader[1])
                        .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                        .addHeader(urlConfig.jwcheader[4], userConfig.getCookie()).post(body)
                        .build();

                client.newCall(request2).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ichengji.chemgji(dealWithGradeHtml(response.body().string()));
                    }
                });

            }
        });

    }

    //处理成绩
    private static ArrayList<user> dealWithGradeHtml(String grade_html) {
        Document doc = Jsoup.parse(grade_html);
        // 从第二个tr开始
        Elements esTr = doc.select(".datelist tr:nth-child(n+2)");

        ArrayList<user> gradeList = new ArrayList<user>();

        for (Element element : esTr) {
            Elements esTd = element.getElementsByTag("td");
            if (esTd.size() == 15) {
                String temp[] = new String[15];
                int i = 0;
                for (Element e : esTd) {
                    temp[i] = e.text();
                    i++;
                }
                user grade = new user();
                grade.setYear(temp[0]);
                grade.setTerm(temp[1]);
                grade.setCode(temp[2]);
                grade.setName(temp[3]);
                grade.setProperty(temp[4]);
                grade.setBelong(temp[5]);
                grade.setCredit(temp[6]);
                grade.setGrade_point(temp[7]);
                grade.setScore(temp[8]);
                grade.setMinor_tag(temp[9]);
                grade.setRetest_score(temp[10]);
                grade.setResume_score(temp[11]);
                grade.setCollege(temp[12]);
                grade.setNote(temp[13]);
                grade.setRebuild_tag(temp[14]);

                gradeList.add(grade);
            }
        }

        //        Log.i("tag", "dealWithGradeHtml-list-size: " + gradeList.size());
        return gradeList;
    }

    //获取个人课表
    public static void getKeBiao(final Ikebiao ikebiao) {

        Request request = new Request.Builder()
                .url(urlConfig.baseurl + userConfig.getBjkbcxUrl())
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ikebiao.kebiao(dealWithKebiaoHtml(response.body().string()));
            }
        });

    }

    //处理个人课表
    private static ArrayList<kebiao> dealWithKebiaoHtml(String kebiao_html) {
        Document doc = Jsoup.parse(kebiao_html);
        // 从第二个tr开始
        Elements esTr = doc.select(".blacktab tr");
        ArrayList<kebiao> kebiaoList = new ArrayList<kebiao>();
        for (Element element : esTr) {
            Elements esTd = element.getElementsByTag("td");
            String temp[] = new String[8];
            int j = 0;
            for (Element e : esTd) {
                //                if (e.text().indexOf("第") != -1) {
                //                    continue;
                //                }
                if (e.text().indexOf("上") != -1) {
                    continue;
                }
                if (e.text().indexOf("下") != -1) {
                    continue;
                }
                if (e.text().indexOf("晚") != -1) {
                    continue;
                }
                temp[j] = e.text();
                j++;
            }
            kebiao kebiao = new kebiao();
            kebiao.setTime(temp[0]);
            kebiao.setMonday(temp[1]);
            kebiao.setTuesday(temp[2]);
            kebiao.setWednesday(temp[3]);
            kebiao.setThursday(temp[4]);
            kebiao.setFriday(temp[5]);
            kebiao.setSaturday(temp[6]);
            kebiao.setSunday(temp[7]);
            kebiaoList.add(kebiao);
        }

        Log.i("课表", "dealWithKebiaoHtml: " + kebiaoList.get(0).getTime());

        return kebiaoList;
    }

    //获取修改密码devs
    private static String getChangpwdVss() {
        Request request = new Request.Builder()
                .url(urlConfig.baseurl + userConfig.getMmxgUrl())
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .build();
        String vss = null;
        try {
            Response response = client.newCall(request).execute();
            vss = findViewState(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vss;
    }

    //修改密码
    public static String changePwd(String oldPwd, String newPwd) {
        String vss = getChangpwdVss();
        if (vss == null) {
            vss = "dDwtMzg5NzE5MDc3Ozs+SGJXPq6dHHqtmWcPNRKJMexpc0I=";
        }

        RequestBody body = new FormBody.Builder()
                .add("__VIEWSTATE", vss)
                .add("TextBox2", oldPwd)
                .add("TextBox3", newPwd)
                .add("TextBox4", newPwd)
                .add("Button1", "")
                .build();
        Request request = new Request.Builder()
                .url(urlConfig.baseurl + userConfig.getMmxgUrl())
                .addHeader(urlConfig.jwcheader[0], urlConfig.jwcheader[1])
                .addHeader(urlConfig.jwcheader[2], urlConfig.jwcheader[3] + userConfig.getUsername())
                .addHeader(urlConfig.jwcheader[4], userConfig.getCookie())
                .post(body)
                .build();

        String html = "";
        try {
            Response response = client.newCall(request).execute();
            html = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String pattern = "<script language=\\'javascript\\'>alert\\(\\'(.*?)\\'\\);.*?</script>";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(html);
        String result = "";
        if (m.find()) {
            //            m.group();
            result = m.group(1);
        }
        Log.i("买买买", "changePwd: " + result);
        return result;
    }

    //获取其他地址
    private static String grxxUrl, cjcxUrl, mmxgUrl, bjkbcxUrl;

    private static void queryURL(Document document) {
        Elements eles = document.getElementsByAttributeValue("target", "zhuti");
        for (Element e : eles) {
            if (e.text().contains("个人信息")) {
                grxxUrl = e.attr("href");
                continue;
            }
            if (e.text().contains("密码修改")) {
                mmxgUrl = e.attr("href");
                continue;
            }
            if (e.text().contains("班级课表查询")) {
                bjkbcxUrl = e.attr("href");
                continue;
            }
            if (e.text().contains("学习成绩查询")) {
                cjcxUrl = e.attr("href");
            }
        }
        userConfig.setUrlbody(grxxUrl, cjcxUrl, mmxgUrl, bjkbcxUrl);
    }

}

