Ingress Intel Total Conversion (IITC)
=====================================
[![Version](https://img.shields.io/github/release/paizi/IITC-CN.svg)](https://github.com/paizi/IITC-CN/releases)

> IITC是一个浏览器插件，可以修改Ingress intel地图。它比标准站点更快，并提供更多功能。它适用于桌面浏览器，如Firefox和Chrome，并提供移动应用程序。

修改自https://github.com/IITC-CE/ingress-intel-total-conversion

由于精力有限，目前只有Android版本的汉化，如有意协助汉化，请提交pull requests，感激不尽！
## 用户

只是想下载/安装 IITC? 前往
https://iitc.modos189.ru/

关注最新新闻,版本发布等,请关注 IITC_CN Telegram channel
https://t.me/s/cniitc

想要报告一个bug？请提交到issues页面
https://github.com/paizi/IITC-CN/issues

## 开发者

下面的适合那些有兴趣进一步开发IITC的人。

### 快速开始

要使用源代码构建浏览器脚本，您需要使用Python（最新版本2.x或3.0+）。
这应该可以在Linux和Windows上正确构建（可能还有Mac，FreeBSD等）

请fork此项目，并clone到本地计算机。

运行 `build.py local` 来构建代码。

如果一切顺利，构建的输出将出现在`build/local`子文件夹中。

您还可以创建一个自定义构建配置文件：`localbuildsettings.py`  - 查看提供的内容
`buildsettings.py`了解详情。

### 移动应用程序

要构建移动应用程序，您将需要

 -  Java JDK（开发工具包 - JRE是不够的）
 -  Android SDK

运行`build.py mobile`，在debug模式下构建IITC Mobile。

提示：也可使用Android Studio构建
