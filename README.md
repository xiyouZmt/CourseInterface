#西邮课表API接口

###支持POST/GET请求,
**请求url:http://119.29.181.219:8080/Course/get**

###请求参数说明
**username：学号**

**password：密码**

**checkCode：验证码**

**cookie：请求验证码返回的sessionID**

**请求示例: http://119.29.181.219:8080/Course/get?username=xxxxxxxx&password=xxxxxxxx&checkCode=xxxx&cookie=xxxxxxxxxxxxxxxxxxxxxxx

**请求成功返回数据

{"result":"success","courses":[{"name":"xxx","time":"xxx","teacher":"xxx","classroom":"xxx"},......]”}**

**请求失败返回数据{"result":"failed","reason":"data error"}**

**请求成功返回示例

{"result":"success","courses":[{"name":"基于Verilog的FPGA设计基础 ","time":"周一第1,2节","teacher":"董梁","classroom":"FF307"},{"name":"数据库原理及应用A ","time":"周二第1,2节","teacher":"孟彩霞","classroom":"FZ505"},{"name":"基于Verilog的FPGA设计基础 ","time":"周三第1,2节","teacher":"董梁","classroom":"FF505"},{"name":"数据库原理及应用A ","time":"周四第1,2节","teacher":"孟彩霞","classroom":"FZ605"},{"name":"计算机专业英语 ","time":"周五第1,2节","teacher":"贾晖","classroom":"FF307"},{"name":"嵌入式系统原理与应用A ","time":"周一第3,4节","teacher":"李有谋","classroom":"FF307"},{"name":"Java语言程序设计B ","time":"周二第3,4节","teacher":"刘霞林","classroom":"FF505"},{"name":"嵌入式系统原理与应用A ","time":"周三第3,4节","teacher":"李有谋","classroom":"FF505"},{"name":"Java语言程序设计B ","time":"周四第3,4节","teacher":"刘霞林","classroom":"FF505"},{"name":"软件工程B ","time":"周五第3,4节","teacher":"舒新峰","classroom":"FF307"},{"name":"马克思主义基本原理概论 ","time":"周二第5,6节","teacher":"赵剑","classroom":"FF203"}]}

**在请求数据时，应该先请求验证码URL：http://222.24.19.201/checkCode.aspx, 拿到返回的sessionID以及验证码图片，客户端输入用户名(username),密码(password),验证码(checkCode),携带这些数据以及sessionID请求即可**

**注意在请求验证码时返回的sessionID一般示例是这样的: ASP.NET_SessionId=3ljlddvx5lbfqh55smo2sunv; path=/, 我们需要的是分号之前的那部分，所以要把分号以及后面的截掉。

###返回参数说明
**name:课程名称, time:上课时间, teacher:任课教师, classroom:上课教室**

**返回课程顺序**

**1-2节周一到周五**

**3-4节周一到周五**

**5-6节周一到周五**

**7-8节周一到周五**

**目前只显示了有课的，没课的没有显示，在处理数据时按照返回顺序以及课程时间依次部署到客户端显示，后续会把所有时间的课程都返回，，，**
