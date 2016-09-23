#西邮课表API接口

###支持POST/GET请求,
**请求url:http://119.29.181.219:8080/Course/get**

**请求成功返回数据
{"result":"success","courses":[{"name":"xxx","time":"xxx","teacher":"xxx","classroom":"xxx"},......]”}**

**请求失败返回数据{"result":"failed","reason":"data error"}**

###请求参数说明
**username：学号**

**password：密码**

**checkCode：验证码**

**cookie：请求验证码返回的sessionID**

**在请求数据时，应该先请求验证码URL：http://222.24.19.201/checkCode.aspx, 拿到返回的sessionID以及验证码图片，客户端输入用户名(username),密码(password),验证码(checkCode),携带这些数据以及sessionID请求即可**

###返回参数说明
**name:课程名称, time:上课时间, teacher:任课教师, classroom:上课教室**

**返回课程顺序**

**1-2节周一到周五**

**3-4节周一到周五**

**5-6节周一到周五**

**7-8节周一到周五**

**目前只显示了有课的，没课的没有显示，在处理数据时按照返回顺序以及课程时间依次部署到客户端显示，后续会把所有时间的课程都返回，，，**
