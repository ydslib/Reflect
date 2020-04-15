# Reflect
Android reflection framework, convenient for my own use

How to
To get a Git project into your build:

**Step 1**. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```
**Step 2**. Add the dependency
```gradle
dependencies {
    implementation 'com.github.ydslib:Reflect:1.0.0'
}
```
Share this release



### 使用方法
- 反射类
```java
Reflect.from(Person.class).off();
Reflect.from("com.yds.demo.Person").off();

- 反射构造函数
//无参的构造
Reflect.from(Person.class).createConstructor().off();
//Person类有两个参数
Reflect.from("com.yds.demo.Person").createConstructor("yds", 20).off();

//反射方法
Reflect rf = Reflect.from("com.yds.demo.Person").createConstructor();
Object obj = rf.method("getName",String.class).invoke("测试").off();

//反射字段
Reflect rf = Reflect.from("com.yds.demo.Person").createConstructor();
rf.field("name").set("Hello world");//设置字段
Object obj = rf.field("name").get().off()//获取字段


```






