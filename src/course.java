import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Dangelo on 2016/9/13.
 */
public class CourseInterface extends HttpServlet{

    private String ip = "http://222.24.19.201/";
    private String checkCodeUrl = ip + "checkCode.aspx";
    private String sessionUrl = ip + "default2.aspx";
    private String coursesUrl = ip + "xskbcx.aspx?xh=";
//    private String username;
//    private String password;
//    private String checkCode;
//    private String cookie;
    private String [] values = new String[4];
    private String [] weeks = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private OkHttpClient okHttpClient;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        values[0] = request.getParameter("username");
        System.out.println(values[0]);
        values[1] = request.getParameter("password");
        values[2] = request.getParameter("checkCode");
        values[3] = request.getParameter("cookie");
        response.setContentType("text/html;charset=GBK");
//        for (String value : values) {
//            response.getWriter().write(value);
//        }
        okHttpClient = new OkHttpClient();
        okHttpClient.setFollowRedirects(false);
        System.out.println("1234");
        getSessionId();
        System.out.println("0000");
        String courseHtml = getCourses();
        System.out.println("abcd");
        System.out.println(courseHtml);
        response.getWriter().write(manageCourses(getCourses(Jsoup.parse(courseHtml))));
        System.out.println("test");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String getCheckCode(){
        /**
         * 获取验证码，同时得到cookie,但是cookie此时并没有效果，需要经过请求一次主页才有用。
         */
        Request request = new Request.Builder().url(checkCodeUrl).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() == 200){
                if(response.header("Set_Cookie") != null){
                    String cookie = response.header("Set-Cookie");
                    return cookie.substring(0, cookie.indexOf(';'));
                } else {
                    return "cookie is null";
                }
            } else {
                return "Network error";
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return "Network error";
        }
    }

    private String getSessionId(){
        /**
         * 请求主页，使cookie变得有效
         */
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("__VIEWSTATE", "dDwyODE2NTM0OTg7Oz4EPWKUJ7QVy9jt5geaO9kcCdS0zQ==")
                .add("txtUserName", values[0])
                .add("TextBox2", values[1])
                .add("txtSecretCode", values[2])
                .add("RadioButtonList1", "学生")
                .add("Button1", "")
                .add("lbLanguage", "")
                .add("hidPdrs", "")
                .add("hidsc", "");
        Request request = new Request.Builder().url(sessionUrl).addHeader("Cookie", values[3])
                .addHeader("Referer", "http://222.24.19.201").post(builder.build()).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() == 302){
                return values[3];
            } else {
                return "Network error";
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return "Network error";
        }
    }

    private String getCourses(){
        StringBuilder content = new StringBuilder();
        Request request = new Request.Builder().url(getCoursesUrl()).addHeader("Cookie", values[3])
                .addHeader("Referer", getCoursesUrl()).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() == 200){
                if(response.header("Content-Length").equals("276")){
                    return "no evaluate";
                }
                BufferedReader reader = new BufferedReader(response.body().charStream());
                String line;
                while ((line = reader.readLine()) != null){
                    content.append(line);
                }
                reader.close();
                return content.toString();
            } else {
                return "NetWork error";
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return "Network error";
        }
    }

    private String manageCourses(List<Map<String, String>> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"result\":\"success\",\"courses\":[");
        for (int i = 0; i < list.size(); i++) {
            for (String week1 : weeks) {
                String week = list.get(i).get(week1);
                System.out.println("week--->" + "前" + week + "后");
                if (week.length() != 1) {
                    int temp;
                    String name = week.substring(0, (temp = week.indexOf("周")));
                    String time = week.substring(temp, week.indexOf('{'));
                    String teacher = week.substring(week.lastIndexOf('}') + 2, week.lastIndexOf(' '));
                    String classroom = week.substring(week.lastIndexOf(' ') + 1, week.length());
                    System.out.println("i--->" + String.valueOf(i));
                    System.out.println("week1--->" + week1);
                    System.out.println("week--->" + name + "\n" + teacher + '\n' + classroom);
//                    list.get(i).put(week1, name + "\n" + teacher + "\n@" + classroom);
                    builder.append("{\"name\":\"").append(name).append("\",")
                            .append("\"time\":\"").append(time).append("\",")
                            .append("\"teacher\":\"").append(teacher).append("\",")
                            .append("\"classroom\":\"").append(classroom).append("\"}");
                    if(week1.equals(weeks[weeks.length - 1]) && i == list.size() - 1){
                        builder.append("]}");
                        break;
                    } else {
                        builder.append(",");
                    }
                }
                if(week1.equals(weeks[weeks.length - 1]) && i == list.size() - 1){
                    builder.deleteCharAt(builder.length() - 1);
                    builder.append("]}");
                }
            }
        }
        return builder.toString();
    }

    private List<Map<String, String>> getCourses(Document document) {
        List<Map<String, String>> list = new ArrayList<>();
        Element element = document.getElementById("Table1");
        Elements elements = element.getElementsByTag("tr");
        /**
         * 从下标为2的节点开始， 每两个节点获取一次课表
         */
        for (int i = 2; i < elements.size(); i += 2) {
            /**
             * 得到每天对应节数的课表集合
             */
            Elements mElements = elements.get(i).getElementsByAttributeValue("align", "Center");
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < weeks.length; j++) {
                map.put(weeks[j], mElements.get(j).text());
                System.out.println("weeks--->" + mElements.get(j).text());
                System.out.println("weeksLength--->" + String.valueOf(mElements.get(j).text().length()));
            }
            list.add(map);
        }
        return list;
    }

    private String getCoursesUrl(){
        return coursesUrl + values[0] + "&xm=&gnmkdm=N121603";
    }
}