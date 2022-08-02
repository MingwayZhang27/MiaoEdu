# MiaoEdu
### 1、项目介绍
本项目是一个在线教育平台，采用B2C模式，平台分为前台用户系统和后台管理系统两部分。
前台用户系统包括课程信息，讲师信息和章节内容。
	后台管理系统包括：讲师管理、课程分类管理、课程管理、统计分析、Banner管理、订单管理、权限管理等功能。

### 2、技术架构

前端技术架构：Node.js+Vue+Element-UI+ECharts

（1）后台管理系统前端使用的是vue-element-admin，而vue-element-admin是基于element-ui 的一套后台管理系统集成方案。

（2）前台用户系统前端使用的是NUXT，Nuxt.js 是一个基于 Vue.js 的轻量级应用框架,可用来创建服务端渲染 (SSR) 应用,也可充当静态站点引擎生成静态站点应用,具有优雅的代码结构分层和热加载等特性。

**其中banner前台展示代码部分：**

---------------------------------------------------------



后端技术架构：SpringBoot+SpringCloud+MyBatisPlus+SpringSecurity+Redis+Nginx+EasyExcel

其中nginx提供的路由转发和负载均衡代码默认注释掉。

​	本项目采用微服务架构，不同的微服务一般会有**不同的网络地址**，而外部客户端可能需要调用多个服务的接口才能完成一个业务需求。使用Nacos作为注册中心和配置中心(在Nacos创建统一配置文件)，Feign实现服务调用，用Gateway提供网关服务(Gateway核心就是一个过滤器)。

#### 	*Feigin：*


#### 	*Gateway：*

​	下面介绍一下Spring Cloud Gateway中几个重要的概念。

（1）**路由**。路由是网关最基础的部分，路由信息有一个ID、一个目的URL、一组断言和一组Filter组成。如果断言路由为真，则说明请求的URL和配置匹配

（2）**断言**。Java8中的断言函数。Spring Cloud Gateway中的断言函数输入类型是Spring5.0框架的ServerWebExchange。Spring Cloud Gateway中的断言函数允许开发者去定义匹配来自于httprequest中的任何信息，比如请求头和参数等。

（3）**过滤器**。一个标准的Spring webFilter。Spring cloud gateway中的filter分为两种类型的Filter，分别是Gateway Filter和Global Filter。过滤器Filter将会对请求和响应进行修改处理


​	Spring cloud Gateway发出请求。然后再由**Gateway Handler Mapping**中找到与请求相匹配的路由，将其发送到**Gateway web handler**。Handler再通过指定的**过滤器链**将请求发送到我们实际的服务执行业务逻辑，然后返回。

#### 	*canal：*




#### 	*SpringSecurity：*

使用SpringSecurity实现认证和授权功能，并用JWT技术实现注册分布式单点登录。

权限管理包含三个功能模块：`菜单管理`、`角色管理`和`用户管理`


Web 应用的安全性包括`用户认证`（Authentication）和`用户授权`（Authorization）两个部分。

```
1、用户名密码认证成功，获取用户的权限值；并以用户名为Key，权限列表为Value的形式存储在Redis缓存中：使用RedisTemplate实现。
2、根据用户名相关信息返回token，浏览器将token记录到cookie中，没请求都会在请求头中携带token
3、请求中，Spring-security会解析请求头中的token，并获取用户名；
4、根据用户名为key，去redis中获取value值权限列表
5、根据权限列表来判断用户是否有权限访问
```

#### 	***Redis:***

使用Redis对首页面信息进行缓存，并且用于记录登录信息。


添加redis配置类，添加@Cacheable注释，缓存课程和讲师。


​	使用ECharts做图表展示，使用EasyExcel完成分类批量添加。

#### 	*ECharts：*

#### 	*EasyExcel*

​	课程分类存储结构：一级分类和二级分类

EasyExcel是阿里巴巴开源的一个excel处理框架，`以使用简单、节省内存著称`。EasyExcel能大大减少占用内存的主要原因是在解析Excel时没有将文件数据一次性全部加载到内存中，而是从磁盘上一行行读取数据，逐个解析。

**课程分类读取Excel文件方法：**

##### 1、EduSubjectController

    @RestController
    @RequestMapping("/eduservice/edu-subject")
    @CrossOrigin //解决跨域问题
    @Api(description="课程分类管理")
    public class EduSubjectController {
    @Autowired
    private EduSubjectService eduSubjectService;
    
    //添加课程分类
    //获取上传过来的文件，把文件内容读取出来
    @PostMapping("/addSubject")
    public R addSubject(MultipartFile file){
        //获取上传的excel文件 MultipartFile
    
        eduSubjectService.saveSubject(file,eduSubjectService);
        //判断返回集合是否为空
    
        return R.ok();
    }}

##### 2、创建和Excel对应的实体类

    @Data
    @ToString
    public class SubjectData {
    //一级分类
    @ExcelProperty(index = 0)
    private String oneSubjectName;
    
    //二级分类
    @ExcelProperty(index = 1)
    private String twoSubjectName;}
    
##### 3、SubjectExcelListener监听器

    public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
        //因为SubjectExcelListener不能交给spring进行ioc管理，需要自己手动new，不能注入其他对象
        //不能实现数据库操作
    
        public EduSubjectService eduSubjectService;
    
        //有参，传递subjectService用于操作数据库
        public SubjectExcelListener(EduSubjectService eduSubjectService) {
            this.eduSubjectService = eduSubjectService;
        }
    
        //无参
        public SubjectExcelListener() {
        }
    
        //读取excel内容，一行一行读取
        @Override
        public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
            //表示excel中没有数据，就不需要读取了
            if (subjectData==null){
                throw new AchangException(20001,"添加失败");
            }
    
            //一行一行读取，每次读取有两个值，第一个值一级分类，第二个值二级分类
            //判断是否有一级分类是否重复
            EduSubject existOneSubject = this.existOneSubject(eduSubjectService, subjectData.getOneSubjectName());
            if (existOneSubject == null){ //没有相同的一级分类，进行添加
                existOneSubject = new EduSubject();
                existOneSubject.setParentId("0"); //设置一级分类id值，0代表为一级分类
                existOneSubject.setTitle(subjectData.getOneSubjectName());//设置一级分类名
                eduSubjectService.save(existOneSubject);//给数据库添加一级分类
            }
    
            //获取一级分类的id值
            String parent_id = existOneSubject.getId();
            //判断是否有耳机分类是否重复
            EduSubject existTwoSubject = this.existTwoSubject(eduSubjectService, subjectData.getTwoSubjectName(), parent_id);
            if (existTwoSubject==null){//没有相同的二级分类，进行添加
                existTwoSubject = new EduSubject();
                existTwoSubject.setParentId(parent_id); //设置二级分类id值
                existTwoSubject.setTitle(subjectData.getTwoSubjectName());//设置二级分类名
                eduSubjectService.save(existTwoSubject);//给数据库添加二级分类
            }
    
        }//判断一级分类不能重复添加
        private EduSubject existOneSubject(EduSubjectService eduSubjectService,String name){
            QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("title",name)
                    .eq("parent_id","0");
            EduSubject oneSubject = eduSubjectService.getOne(wrapper);
            return oneSubject;
        }
    
        //判断二级分类不能重复添加
        private EduSubject existTwoSubject(EduSubjectService eduSubjectService,String name,String parentId){
            QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("title",name)
                    .eq("parent_id",parentId);
            EduSubject twoSubject = eduSubjectService.getOne(wrapper);
            return twoSubject;
        }
    
        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }}

##### 4、SubjctService

###### **1）接口**

```
public interface EduSubjectService extends IService<EduSubject> {
    //添加课程分类
    void saveSubject(MultipartFile file,EduSubjectService eduSubjectService);
}
```

###### **2）impl**

    @Service
    public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {
        //添加课程分类
        @Override
        public void saveSubject(MultipartFile file,EduSubjectService eduSubjectService) {
            try {
                //文件输入流
                InputStream is = file.getInputStream();
    
                //调用方法进行读取
                EasyExcel.read(is, SubjectData.class,new SubjectExcelListener(eduSubjectService))
                        .sheet().doRead();
    
            }catch (Exception e){
                e.printStackTrace();
                throw new AchangException(20002,"添加课程分类失败");
            }
    }}
​	本项目使用阿里云OSS存储图像，使用阿里云视频点播实现视频的上传、删除和播放，使用阿里云短信服务通过手机验证码实现用户注册功能，并且实现微信扫码登录和微信扫码支付功能。
