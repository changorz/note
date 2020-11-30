## activiti工作流

## 一、导包与配置文件

```xml
        <properties>
            <java.version>1.8</java.version>
            <slf4j.version>1.6.6</slf4j.version>
            <log4j.version>1.2.12</log4j.version>
            <activiti.version>7.1.0.M6</activiti.version>
        </properties>		

		<!-- activiti -->
		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-engine</artifactId>
			<version>${activiti.version}</version>
		</dependency>

		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-spring</artifactId>
			<version>${activiti.version}</version>
		</dependency>

		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-bpmn-model</artifactId>
			<version>${activiti.version}</version>
		</dependency>

		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-bpmn-converter</artifactId>
			<version>${activiti.version}</version>
		</dependency>

		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-json-converter</artifactId>
			<version>${activiti.version}</version>
		</dependency>

		<dependency>
			<groupId>org.activiti</groupId>
			<artifactId>activiti-bpmn-layout</artifactId>
			<version>${activiti.version}</version>
		</dependency>


		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>

		<!-- log start -->
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>${log4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>${slf4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>${slf4j.version}</version>
		</dependency>
		<!-- log end -->

		<dependency>
			<groupId>org.mybatis</groupId>
			<artifactId>mybatis</artifactId>
			<version>3.4.5</version>
		</dependency>

		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.4</version>
		</dependency>
```

**配置文件 activiti.cfg.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
						http://www.springframework.org/schema/contex http://www.springframework.org/schema/context/spring-context.xsd
						http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--数据源配置dbcp-->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/activiti?useSSL=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=UTC" />
        <property name="username" value="root" />
        <property name="password" value="root" />
    </bean>
    <!--activiti单独运行的ProcessEngine配置对象(processEngineConfiguration),使用单独启动方式
        默认情况下：bean的id=processEngineConfiguration
    -->

    <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
    <!--代表数据源-->
    <property name="dataSource" ref="dataSource"></property>
    <property name="databaseSchemaUpdate" value="true"/>
    </bean>
</beans>
```



## 二、processEngine 创建方式

```java
// 方式一
    @Test
    public void testName() throws Exception {
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        config.setJdbcDriver("com.mysql.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/activiti？createDatabaseIfNotExist=true");
        config.setJdbcUsername("root");
        config.setJdbcPassword("4230");
        config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine engine = config.buildProcessEngine();
        System.out.println(engine);
        System.out.println(engine);
    }
// 方式二
    @Test
    public void testXml() throws Exception {
        ProcessEngineConfiguration config = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine engine = config.buildProcessEngine();
        System.out.println(engine);
    }

// 方式3：缺省的方式，需要保证配置文件的名称为activiti.cfg.xml
	@Test
    public void testdefault() throws Exception {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);
    }
```

但我们调用这个方法，就会加载配置文件，在数据库自动生成表。

### ProcessEngine 流程引擎

1. EngineServers：该接口定义了获取各种服务类实例对象的方法。

2. ProcessEngine：继承EngineServers接口，并增加了对流程引擎名称的获取以及关闭。

3. ProcessEngineImplement：对ProcessEngine接口中定义的方法实现。

4. ProcessEngines：该类负责管理所有的引擎ProcessEngine的集合，并负责流程引擎实例对象的注册、获取、注销等操作。

5. PricwssEngineConfiguration：该抽象类实现接口EngineServer，提供了一系列创建流程引擎配置类ProcessEngineConfigureaction实例对象的方法

6. ProcessEngineConfigurationImpl：该抽象类继承PricwssEngineConfiguration，负责创建一系列服务类实例对象、流程引擎实例对象以及ProcessEngineImpl类实例对象。该类可以通过流程配置文件交给Spring容器管理或者使用编程方式动态构造。

7. SpringProcessEngineConfiguration：主要用于整合Spring框架时使用，提供了几个重要的功能：

- 创建流程引擎实例对象；
- 流程引擎启动之后自动部署配置的流程文档（需要配置）
- 设置流程引擎连接的数据源、事务管理器等

8. StandaloneProcessEngineConfigueration：标准的流程引擎配置类。

9. MultiSchemaMultiTenantProcessEngineConfiguration：“多数据库多租户”流程引擎配置类，Activiti通过此类为开发人员提供了自动路由机制，这样当流程引擎需要连接对各数据库进行操作时，客户端无需关心引擎到底连接的是哪个数据库，该类通过路由规则自动选择需要自动操作的数据库，数据库的操作对客户端来说是透明的，客户端无需关心其内部路由的实现机制。

10. JtaProcessEnginConfiguration：故名自已，通过类名也知道该类支持JTA（Java Transaction API）

11. StandaloneInMenProcessEngineConfiguration：该类通常可以在开发环境中自测使用，默认采用H2数据可存储数据

### EngineServer 提供了一下服务

1. RepositoryServer：操作流程定义的方法。

2. Runtime：操作流程实例的方法。

3. FormServer：操作流程表单的方法。

4. TaskServer：操作任务的方法，例如（任务的完成、挂起、激活、添加处理人、认领、删除等操作）

5. HistoryServer：查询历史流程实例、历史变量、历史任务的方法

6. IdentityServer：操作用户或者用户组的方法。

7. ManagementServer：查询数据库表中的数据、表的元数据以及命令等方法。


参考文献：https://www.jianshu.com/p/406d6a2dad9c

## 三、表字段说明

`ACT_RE_*`: 'RE'表示repository。 这个前缀的表包含了**流程定义和流程静态资源** （图片，规则，等等）。

`ACT_RU_*`: 'RU'表示runtime。 这些运行时的表，包含流程实例，任务，变量，异步任务，等运行中的数据。 Activiti**只在流程实例执行过程中保存这些数据**， 在流程结束时就会删除这些记录。 这样运行时表可以一直很小速度很快。

`ACT_ID_*`: 'ID'表示identity。 这些表包含身份信息，比如用户，组等等。

`ACT_HI_*`: 'HI'表示history。 这些表包含历史数据，比如**历史流程实例**， **变量**，**任务**等等。

`ACT_GE_*`: 'GE'表示general。**通用数据**， 用于不同场景下，如存放资源文件。

详细解析 ===》[ 点我](https://blog.csdn.net/hj7jay/article/details/51302829)



## 四、文件部署

```java
    //流程定义部署
    public static void main(String[] args) {
        //1.创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("diagram/test1.bpmn")  //添加bpmn资源
                .addClasspathResource("diagram/test1.png")
                .name("请假申请单流程")
                .deploy();

        //4.输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }



	//流程定义部署  流程制作出来后要上传到服务器 zip文件更便于上传
    public static void main(String[] args) {
        //1.创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.转化出ZipInputStream流对象
        InputStream is = ActivitiDeployment.class.getClassLoader().getResourceAsStream("diagram/holidayBPMN.zip");

        //将 inputstream流转化为ZipInputStream流
        ZipInputStream zipInputStream = new ZipInputStream(is);

        //3.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请单流程")
                .deploy();

        //4.输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }
```

> 流程定义的部署影响的表

`act_re_deployment` 部署信息

 `act_re_procdef`   流程定义的一些信息

 `act_ge_bytearray` 流程定义的bpmn文件及png文件



## 五、启动流程实例

```java
    public static void main(String[] args) {
        //1.得到RunService对象
        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

        //2.创建流程实例  流程定义的key需要知道 holiday
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("test1");
        //3.输出实例的相关信息
        System.out.println("流程部署ID:"+processInstance.getDeploymentId());
        System.out.println("流程定义ID:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID:"+processInstance.getId());
        System.out.println("活动ID:"+processInstance.getActivityId());

    }
```

> 启动流程实例影响的表

`act_hi_actinst`   已完成的活动信息

`act_hi_identitylink`  参与者信息

`act_hi_procinst` 流程实例

`act_hi_taskinst`  任务实例

`act_ru_execution`  执行表

`act_ru_identitylink`  参与者信息

`act_ru_task` 任务



## 六、处理当前用户的任务

```java
 public static void main(String[] args) {

        //1.得到TaskService对象
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();

        //2.查询当前用户的任务
        // 当数据大于1时必须用list
        // org.activiti.engine.ActivitiException: Query return 3 results instead of max 1
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("test1")  // 流程ID
                .taskAssignee("zhangsan")       // 当前用户
                .list();

        taskList.stream().forEach(task -> {
            //3.处理任务,结合当前用户任务列表的查询操作的话,任务ID:task.getId()
            taskService.complete(task.getId());
            //4.输出任务的id
            System.out.println(task.getId());
        });
    }
```

> 处理当前用户的任务 背后操作的表

`act_hi_actinst`已完成的活动信息

`act_hi_identitylink`参与者信息

`act_hi_taskinst`任务实例

`act_ru_identitylink`参与者信息

`act_ru_task`任务



## 七、其他

#### 1. 启动时传入变量

```java
// 启动流程实例,同时还要指定业务标识businessKey  它本身就是请假单的id
// 第一个参数：是指流程定义key
// 第二个参数：业务标识businessKey
ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("test1", "1001");


// 设置assignee的取值   用户可以在界面上设置流程的执行人
    Map<String,Object> map = new HashMap<>();
    map.put("assignee0","zhangsan");
    map.put("assignee1","lishi");
    map.put("assignee2","wangwu");

// 启动流程实例，同时还要设置流程定义的assignee的值
ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday2", map);
```

#### 2. 任务办理时设置变量

```java
// 定义流程变量 
Map<String, Object> variables = new HashMap<String, Object>(); 
//变量名是holiday，变量值是holiday对象 
variables.put("holiday", holiday); 
taskService.complete(taskId, variables);
```

#### 3. 通过当前流程实例设置变量

```java
//4.通过实例id,来设置流程变量
//第一个参数：流程实例的id
//第二个参数：流程变量名
//第三个变量：流程变量名，所对应的值
runtimeService.setVariable("2501","holiday",holiday);

runtimeService.setVariables();
// 本地变量
runtimeService.setVariableLocal();
```

#### 4. 通过当前任务设置变量

```java
//给指定任务设置任务变量。此处变量类型为String
taskService.setVariable(taskId,"variable1", "hello");

// 本地变量
taskService.setVariableLocal();
```

#### 任务拾取

Ps:  注意taskAssignee 与 taskCandidateUser 的区别

```
.taskAssignee("zhangsan")        // 当前用户任务的查询
.taskCandidateUser("zhangsan")   // 后选者任务查询
```

```java
taskList.stream().forEach(task -> {
    taskService.claim(task.getId(), "zhangsan");
    System.out.println("任务拾取成功");
    //3.处理任务,结合当前用户任务列表的查询操作的话,任务ID:task.getId()
    taskService.complete(task.getId());
    //4.输出任务的id
    System.out.println(task.getId());
});
```

#### 任务归还与交接

```java
// 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务 
Task task = taskService.createTaskQuery().taskId(taskId) .taskAssignee(userId).singleResult(); if (task != null) { 
    // 如果设置为null，归还组任务,该 任务没有负责人 
    taskService.setAssignee(taskId, null); 
    // taskService.setAssignee(taskId, "lisi"); 
}
```











#### 删除已经部署的流程定义

```java
 /**
     * 注意事项：
     *     1.当我们正在执行的这一套流程没有完全审批结束的时候，此时如果要删除流程定义信息就会失败
     *     2.如果公司层面要强制删除,可以使用repositoryService.deleteDeployment("1",true);
     *     //参数true代表级联删除，此时就会先删除没有完成的流程结点，最后就可以删除流程定义信息  false的值代表不级联
     *
     * @param args
     */
    public static void main(String[] args) {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.执行删除流程定义  参数代表流程部署的id
        repositoryService.deleteDeployment("1");
    }
```



#### 历史数据的查看

```java
public static void main(String[] args) throws IOException {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        //3.得到HistoricActivitiInstanceQuery对象
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        historicActivityInstanceQuery.processInstanceId("2501");//设置流程实例的id
        //4.执行查询
        List<HistoricActivityInstance> list = historicActivityInstanceQuery
                .orderByHistoricActivityInstanceStartTime().asc().list();//排序StartTime
        //5.遍历查询结果
        for (HistoricActivityInstance instance :list){
            System.out.println(instance.getActivityId());
            System.out.println(instance.getActivityName());
            System.out.println(instance.getProcessDefinitionId());
            System.out.println(instance.getProcessInstanceId());
            System.out.println("=============================");
        }
    }
```

#### 从Activiti的act_ge_bytearray表中读取两个资源文件

```java
public static void main(String[] args) throws IOException {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到查询器:ProcessDefinitionQuery对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置查询条件
        processDefinitionQuery.processDefinitionKey("holiday");//参数是流程定义的key

        //5.执行查询操作,查询出想要的流程定义
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        //6.通过流程定义信息，得到部署ID
        String deploymentId = processDefinition.getDeploymentId();

        //7.通过repositoryService的方法,实现读取图片信息及bpmn文件信息(输入流)
        //getResourceAsStream()方法的参数说明：第一个参数部署id,第二个参数代表资源名称
        //processDefinition.getDiagramResourceName() 代表获取png图片资源的名称
        //processDefinition.getResourceName()代表获取bpmn文件的名称
        InputStream pngIs = repositoryService
                .getResourceAsStream(deploymentId,processDefinition.getDiagramResourceName());
        InputStream bpmnIs = repositoryService
                .getResourceAsStream(deploymentId,processDefinition.getResourceName());

        //8.构建出OutputStream流
        OutputStream pngOs =
                new FileOutputStream("G:\\"+processDefinition.getDiagramResourceName());

        OutputStream bpmnOs =
                new FileOutputStream("G:\\"+processDefinition.getResourceName());

        //9.输入流，输出流的转换  commons-io-xx.jar中的方法
        IOUtils.copy(pngIs,pngOs);
        IOUtils.copy(bpmnIs,bpmnOs);
        //10.关闭流
        pngOs.close();
        bpmnOs.close();
        pngIs.close();
        bpmnIs.close();

    }
```



#### 查询流程定义信息

```java
public static void main(String[] args) {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.得到ProcessDefinitionQuery对象,可以认为它就是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //4.设置条件，并查询出当前的所有流程定义   查询条件：流程定义的key=holiday
        //orderByProcessDefinitionVersion() 设置排序方式,根据流程定义的版本号进行排序
        List<ProcessDefinition> list = processDefinitionQuery.processDefinitionKey("test1")
                .orderByProcessDefinitionVersion()
                .desc().list();
        //5.输出流程定义信息
        for(ProcessDefinition processDefinition :list){
            System.out.println("流程定义ID："+processDefinition.getId());
            System.out.println("流程定义名称："+processDefinition.getName());
            System.out.println("流程定义的Key："+processDefinition.getKey());
            System.out.println("流程定义的版本号："+processDefinition.getVersion());
            System.out.println("流程部署的ID:"+processDefinition.getDeploymentId());

        }
    }
```



####  单个流程实例挂起与激活

```java
public static void main(String[] args) {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.查询流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("2501").singleResult();
        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
        String processInstanceId = processInstance.getId();
        //5.判断
        if(suspended){
            //说明是暂停，就可以激活操作
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"激活");
        }else{
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程定义："+processInstanceId+"挂起");
        }
    }
```



####  全部流程实例挂起与激活

```java
public static void main(String[] args) {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("test1").singleResult();
        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId();
        //5.判断
        if(suspended){
            //说明是暂停，就可以激活操作
            repositoryService.activateProcessDefinitionById(processDefinitionId,true
            ,null);
            System.out.println("流程定义："+processDefinitionId+"激活");
        }else{
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义："+processDefinitionId+"挂起");
        }
    }
```



## 八、网关

排他网关：任务执行之后的分支，经过排他网关分支只有一条有效。
并行网关：任务执行后，可以多条分支，多条分支总会汇聚，汇聚完成，并行网关结束。
包含网关：是排他网关和并行网关结合体。













 