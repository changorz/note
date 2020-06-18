### Github项目地址：[https://github.com/changorz/work.git](https://github.com/changorz/work.git)
<br>

## PSP
| PSP2.1            | Personal Software Process Stages | 预估耗时（分钟） | 预估耗时（分钟） |
| :-----------------| :-------------------------------:|:----------------:|:----------------:|
|Planning           | 计划                             | 10              | 5              |
|Estimate           | 估计这个任务需要多少时间         | 10              | 5              |
|Development        | 开发                             | 10              | 5              |
|Analysis           | 需求分析 (包括学习新技术)        | 30              | 10              |
|Design Spec        | 生成设计文档                     | 10              | 5              |
|Design Review      | 设计复审                         | 5              | 5              |
|Coding Standard    | 代码规范 (为目前的开发制定合适的规范)| 5          | 5              |
|Design             | 具体设计                         | 20              | 10              |
|Coding             | 具体编码                         | 60              | 80              |
|Code Review        | 代码复审                         | 20              | 20              |
|Test               | 测试（自我测试，修改代码，提交修改）| 60             | 20              |
|Reporting          | 报告                             | 10              | 10              |
|Test Repor         | 测试报告                         | 10              | 10              |
|Size Measurement   | 计算工作量                       | 10              | 5              |
|Postmortem & Process Improvement Plan| 事后总结, 并提出过程改进计划 | 10| 10              |
|合计               |                                  |    270          |205              |


## 思路描述
> 数独问题，第一感觉就是dfs暴力搜索，通过搜索回溯来确定每一个格子的数字，如果满足行，列，快就将数字填入。
  
## 函数与类
#### 主要设计： 
**主要算法**：dfs，通过暴力试探来确定每一个的值
（递归的流程图不会......只可意会）
**方法设计：**
```java
public static boolean isColAndRow(int i,int j,int k)  //i行j列填入k是否满足条件（仅判断行与列）
public static boolean isGrid(int i,int j,int k) //判断在当前块是否满足条件
public static void print_arr()  //打印写入文件
```
```java
public static boolean isGrid(int i,int j,int k) {//判断在当前块是否满足条件
		int n = N +1;
		if(105%n==0) {        //题目已知1-9的方块内，3,5,7不需要判断块，所以直接返回true
			return true;
		}
		//方块的c行与r列
		int c = 0,r = 0;
		if(n%3==0) {          //经观察，剩下的方块是先满足是否有三行的，不满足就是两行
			 c = 3;
			 r = n/3;
		}else if(n%2==0) {
			 c = 2;
			 r = n/2;
		}
		
		//算出具体的格子大小
		c = n / c;
		r = n / r;
		//通过i,j知道在那一个方格中
		int in_c = (i/c)*c;
		int in_r = (j/r)*r;
		for(int p =in_c;p < in_c + c;p++) {
			for(int q = in_r;q < in_r + r;q++) {
				if(arr[p][q]==k) {
					if(p==i&&q==j) {
						continue;
					}
					return false;
				}
			}
			
		}
		return true;
	}

```

####主要代码设计
```java

	public static void dfs(int cut) {
		int n = N + 1 ;
		if(cut==Math.pow(n, 2)) {
			print_arr();
			return;
		}
		//通过cut求出行与列（i,j）
		int i = cut / n;
		int j = cut % n;
		
		//已知条件以读入的数据
		if(arr[i][j] != 0) {
			dfs(cut + 1);
			return;
		}
		
		//arr填入1-M试探
		for(int t = 1;t <= M; t++) {
			if(isColAndRow(i,j,t)&&isGrid(i,j,t)) {
				arr[i][j] = t;
				dfs(cut + 1);
				//回溯
				arr[i][j] = 0;
			}
		}
	}

```
#### 方法的测试
> 这个项目的两个方法主要是判断填入的数据是否满足要求，用对数器跑即可。

###改进程序性能
> 简单的优化：1.减少变量的使用 2.减少循环      耗时：10min
> 不过我觉得这里唯一能够改进的就是dfs算法了，尝试是否能够剪枝，其他优化见不到明显的提升。

##心路历程与收获
> 先是啃完了构建之法的1-3章（构建之法真的太难啃了...越看越迷了），然后开始看题写代码。
> 读题，嗯，数独，暴力搜索
> 认真的看完题，要求就是填数，只不过矩阵的大小是用户输入的
> 又回到了以前刷算法题的感觉，不过这次是先分析了一下（psp），再开始写的
> 看着PSP表格，我麻了！
> 老师，这不是一个算法题吗，我怎么写接口，设计类...
> 不过 关于算法是运用，方法的设计我还是有好好的设计一下的
> en,设计完成，于是乎我很快就填完了表格...
> 很快的代码就写完了，先肉眼分析一下，还行
> 上数据
> 没结果------------------------------------
> debug才发现一个方法的一个参数打错了，
> 以为是一个简单的方法，没测试
> 看--->构建之法
> 学到：以后写的方法，类，写test的时候要做到全覆盖（包括分支，每一条语句，Boolean值等）
> 还看到开发-封闭原则，记得在head first设计模式中也多次提到，不过到自己设计的时候全都抛之脑后了，哎
> 邹老师可太懂当代大学生了，读了一些技术博客，豪情万丈，想写这个那个...,先把所有东西都整合在一起，想的时候豪情万丈，写的时候技术0，阉割版问世，最后啥也不是
> 书中的魔方例子，像极了我现在学习的spring框架，要是没有百度，IDE，什么也写不出来...
> 以前看到什么新技术就想学，最后每次都学得浅，可能当时会，过段时间就忘了
> 看完大一写的博客，大一时会C，java，python。到大三只会java了，现在我是只打算学java了，语言是互通的

-----------------------------------------------------------------------
##结尾
> 可能我一开始就把这个题当成了一个算法题在做，在编程的时候并没有考虑太多这是一个工程，导致填写PSP的时候有些乱，有些也不知道怎么填，我准备在后面几天用这个方法写一个web项目

