# ![Table of Contents](./toc.png)


<link rel="stylesheet" href="http://yandex.st/highlightjs/6.2/styles/googlecode.min.css">
 
<script src="http://code.jquery.com/jquery-1.7.2.min.js"></script>
<script src="http://yandex.st/highlightjs/6.2/highlight.min.js"></script>
 
<script>hljs.initHighlightingOnLoad();</script>

<script type="text/javascript">
    $(document).ready(function () {
        $("h2,h3,h4,h5,h6").each(function (i, item) {
            var tag = $(item).get(0).localName;
            // 设置每个Item的ID
            $(item).attr("id", "wow" + i);

            // 添加锚点，同时设置class值
            var result = calLevel(tag);
            $("#category").append('<a class="new' + tag + '" href="#wow' + i + '">' + result + ' ' + $(this).text() + '</a></br>');
            var newName = result + ' ' + $(this).text();
            $(this).text(newName);
            
            // 设置样式
            $(".newh2").css("margin-left", 0);
            $(".newh3").css("margin-left", 20);
            $(".newh4").css("margin-left", 40);
            $(".newh5").css("margin-left", 60);
            $(".newh6").css("margin-left", 80);
        });
    });

    var lastLevel = 'h2'; // 记录上一个Level
    var level = 1; // 层级 假设H2为第1级
    var objLevel = {}; // 记录所有层级数量，二维数组

    // 计算标签层级
    function calLevel(currLevel) {
        // 跨层级Level 如：h2 > h4
        var l = Number(lastLevel[1]);
        var c = Number(currLevel[1]);
        level = level - (l - c);
        if (lastLevel < currLevel) {
            // 往下走要清零一次,否则会拿以前的数字进行累加
            objLevel[level] = 0;
        }

        lastLevel = currLevel;
        return calCount();
    }

    // 计算每个层级的数量
    function calCount() {
        var tempCount = (objLevel[level] == null ? 0 : objLevel[level]);
        objLevel[level] = tempCount + 1;
        return getLevel(level);
    }

    // 获取每个item在当前的位置
    function getLevel(level) {
        if (objLevel[level] == undefined)
            return null;
        var temp = getLevel(level - 1);
        if (temp == null)
            return objLevel[level];
        return temp + "." + objLevel[level];
    }
</script>
<!-- html标签-->
<div id="category"></div>

<br>

# 基础框架

基础框架说明文档

与业务隔离的基础框架，所以可以在每个应用中直接引用。主要提供了所有基础库的依赖，以及相关工具类的封装。需要实现以下功能

* 数据存储 AptSharedPreference/GreenDao 3.0
* 网络请求 Retrofit + okhttp + rxjava
* 图片库 Glide
* 事件总线 Rxbus
* 依赖注入 Dagge2.0
* 其他 

## 数据存储

### 数据保存加密部分

对于需要加密存储的数据，遵循以下规则：

加密方法大概分为三类：对称加密（3DES、AES），非对称加密(RSA)，单向加密（SHA1、SHA256）

本地保存敏感数据，最简单的方式，就是使用单向加密，加密用户输入的密码得到结果A；然后使用结果A和对称加密方法，加密敏感数据，保存在app私有目录下。

### AptSharedPreference

[AptPreferences GitHub传送门](https://github.com/joyrun/AptPreferences)

选择这个库是因为实在编译时生成代码，一个是学习。一个是因为这个库使用上面简化了很多工作。避免了繁琐的sharedpreference的API操作。

### GreenDao3.0

[greenDAO GitHub传送门](https://github.com/greenrobot/greenDAO)

选这个还是因为库非常小。

### 使用记录

`@ToOne`
使用一对一和一对多的关系保存时好蛋疼。


```

```




## 网络请求

[retrofit GitHub传送门](https://github.com/square/retrofit)

不多说，用着很爽


## 图片库

[glide GitHub传送门](https://github.com/bumptech/glide)

用官方的吧

## 事件总线

RxBus自己实现就够用了。

没有用EventBus是因为Rxjava就可以满足需求，不需要重复的导入包了。



## 依赖注入

[Dagger2.0 GitHub传送门](https://github.com/google/dagger)

依赖注入Dagger2.0由官方维护。主要做对象统一管理。暂时不清楚为什么要用，还需要慢慢体会他的好处。

### 使用说明

> 摘自百度百科
> 
> 控制反转(Inversion of Control)。控制反转一般分为两种类型，依赖注入（Dependency Injection，简称DI）和依赖查找（Dependency Lookup）。依赖注入应用比较广泛。

> 早在2004年，Martin Fowler就提出了“哪些方面的控制被反转了？”这个问题。他总结出是依赖对象的获得被反转了。基于这个结论，他为控制反转创造了一个更好的名字：依赖注入。许多非凡的应用（比HelloWorld.java更加优美，更加复杂）都是由两个或是更多的类通过彼此的合作来实现业务逻辑，这使得每个对象都需要与其合作的对象（也就是它所依赖的对象）的引用。如果这个获取过程要靠自身实现，那么如你所见，这将导致代码高度耦合并且难以测试。

dagger 用于管理依赖对象。dagger中几个需要了解两个重要的概念

* `Module`负责生产这些Dependency的工厂
* 依赖不是直接从module中直接去要，而是通过一个工厂管理员，他就是`Component`

## Android-job

后台任务框架

[Android-Job GitHub传送门](https://github.com/evernote/android-job)

## 工具类

### AndroidUtilCode

android 工具类大集合

[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)

### StatusBarUtil

状态栏工具

[StatusBarUtil](https://github.com/laobie/StatusBarUtil)

### LogUtils

日志工具类，以后觉得不够用了可以参考

[LogUtils](https://github.com/pengwei1024/LogUtils)

[logger](https://github.com/orhanobut/logger)

## 打包相关

### AndResGuard

资源混淆工具

[AndResGuard](https://github.com/shwenzhang/AndResGuard)

## UI相关库

### BottomBar

导航栏

[BottomBar](https://github.com/roughike/BottomBar)

### NodeFlow

NodeFlow是一个实现树形内容展示的库。非常适合展示按类与子类组织的item。

![](http://www.jcodecraeer.com/uploads/160315/1-160315125249304.gif)

[NodeFlow](https://github.com/Telenav/NodeFlow)

### NotifyUtil

通知栏工具

[NotifyUtil](https://github.com/wenmingvs/NotifyUtil/tree/master/library/src)

### EasyDialog

简单的弹窗提示dialog，用于新增功能提示也不错。

[EasyDialog](https://github.com/michaelye/EasyDialog)

### XhsEmoticonsKeyboard

开源表情键盘解决方案。API > 9

[XhsEmoticonsKeyboard](https://github.com/w446108264/XhsEmoticonsKeyboard)

### SlyceMessaging

[SlyceMessaging](https://github.com/Slyce-Inc/SlyceMessaging)
消息列表窗口

![](https://github.com/Slyce-Inc/SlyceMessaging/raw/master/sample-photos/example.png?raw=true)

### MaterialEditText

[MaterialEditText](https://github.com/rengwuxian/MaterialEditText)

### wechat

wechat 高仿

[wechat](https://github.com/motianhuo/wechat)

### MIX

[动手实现 Twitter 的启动动画](http://www.jianshu.com/p/d0cd21b44ec7) 启动页动画案例

### RippleEffect

[RippleEffect](https://github.com/traex/RippleEffect) 给任意View包一层就会有ripple的效果，同时还可以监听ripple动画结束。

![](https://github.com/traex/RippleEffect/raw/master/demo.gif)

## 其他

### ButterKnife

[ButterKnife GitHub传送门](https://github.com/JakeWharton/butterknife)

这个少不了

### EasyPermissions 

[EasyPermissions GitHub传送门](https://github.com/googlesamples/easypermissions)

### Debug调试

#### stetho
 [stetho GitHub传送门](https://github.com/facebook/stetho) Chrome调试工具

* [timber](https://github.com/JakeWharton/timber) Debug日志工具

### AptPreferences

[AptPreferences](https://github.com/joyrun/AptPreferences) 暂时没用，但是简化了操作，有时间可以按照这个思想实现一个。

### auto-parcel

[auto-parcel](https://github.com/aitorvs/auto-parcel) A fast annotation processor to make your objects `Parcelable` without writing any of the boilerplate.

