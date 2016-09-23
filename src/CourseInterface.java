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
    private String sessionUrl = ip + "default2.aspx";
    private String coursesUrl = ip + "xskbcx.aspx?xh=";
    private String username;
    private String password;
    private String checkCode;
    private String cookie;
    private String [] weeks = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private OkHttpClient okHttpClient;
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        username = request.getParameter("username");
        password = request.getParameter("password");
        checkCode = request.getParameter("checkCode");
        cookie = request.getParameter("cookie");
        response.setContentType("text/html;charset=GBK");
        if(username == null || password == null || checkCode == null || cookie == null){
            builder.append("{\"result\":\"failed\",\"reason\":\"data error\"}");
            response.getWriter().write(builder.toString());
        } else {
            okHttpClient = new OkHttpClient();
            okHttpClient.setFollowRedirects(false);
            cookie = getSessionId();
            if(cookie.equals("error")){
                response.getWriter().write(builder.toString());
            } else {
                String courseHtml = getCourses();
                System.out.println(courseHtml);
                response.getWriter().write(manageCourses(getCourses(Jsoup.parse(courseHtml))));
            }
        }
        builder.delete(0, builder.length());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String getSessionId(){
        /**
         * 请求主页，使cookie变得有效
         */
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("__VIEWSTATE", "dDwyODE2NTM0OTg7Oz4EPWKUJ7QVy9jt5geaO9kcCdS0zQ==")
                .add("txtUserName", username)
                .add("TextBox2", password)
                .add("txtSecretCode", checkCode)
                .add("RadioButtonList1", "学生")
                .add("Button1", "")
                .add("lbLanguage", "")
                .add("hidPdrs", "")
                .add("hidsc", "");
        Request request = new Request.Builder().url(sessionUrl).addHeader("Cookie", cookie)
                .addHeader("Referer", "http://222.24.19.201").post(builder.build()).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.code());
            if(response.code() == 302){
                this.builder.append("{\"result\":\"success\",\"courses\":[");
                return cookie;
            } else {
                this.builder.append("{\"result\":\"failed\",\"reason\":\"data error\"}");
                return "error";
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return "Network error";
        }
    }

    private String getCourses(){
        StringBuilder content = new StringBuilder();
        Request request = new Request.Builder().url(getCoursesUrl()).addHeader("Cookie", cookie)
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
        return coursesUrl + username + "&xm=&gnmkdm=N121603";
    }

}
