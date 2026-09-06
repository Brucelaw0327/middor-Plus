# middor-Plus

[English](./README.md) | [简体中文](./README.zh-CN.md)

> 基于 [nktnet1/middor](https://github.com/nktnet1/middor) 的分支版本,新增快捷悬浮球、首次启动权限引导与简体中文界面。

<div align="center">

[<img src="./misc/badges/github.png" alt="Get it on GitHub" width="260px" />](https://github.com/nktnet1/middor/releases)
[<img src="./misc/badges/obtainium.png" alt="Get it on Obtainium" width="260px" />](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/nktnet1/middor)
[<img src="./misc/badges/openapk.png" alt="Get it on OpenAPK" width="260px" />](https://www.openapk.net/middor/org.nktnet.middor/)
[<img src="./misc/badges/f-droid.svg" alt="Get it on F-Droid" width="260px" />](https://f-droid.org/packages/org.nktnet.middor)
[<img src="./misc/badges/izzy-on-droid.svg" alt="Get it on IzzyOnDroid" width="260px" />](https://apt.izzysoft.de/fdroid/index/apk/org.nktnet.middor)

</div>

middor-Plus 是一款自由开源的 Android 应用,用于镜像你设备上的其他应用。

借助内置的水平镜像与 180° 旋转,它可以:

- 将应用画面投射到 HUD 抬头显示上
- 将内容投射到汽车挡风玻璃(例如 Google 地图导航)
- 查看镜像后的图片与视频

它仅支持 Android 14 QPR2 及以上的设备,因为依赖系统的"单个应用屏幕共享"特性。

详见
[Android 14 - App Screen Sharing](https://developer.android.com/about/versions/14/features/app-screen-sharing)。

使用说明见 [Discussion #2](https://github.com/nktnet1/middor/discussions/2)。

## 与原版的差异

本 fork 基于 [nktnet1/middor](https://github.com/nktnet1/middor),保留了原版的全部功能,并新增了
快捷悬浮球、首次启动权限引导,以及简体中文界面。

| 功能 | 原版 Middor | middor-Plus |
|:---|:---:|:---:|
| 单应用镜像悬浮层 | ✓ | ✓ |
| 水平镜像 / 180° 旋转 | ✓ | ✓ |
| 移除系统栏 | ✓ | ✓ |
| 启动延时(秒) | ✓ | ✓ |
| 常驻可拖动悬浮球 | ✗ | ✓(默认开启) |
| 无需打开应用即可发起镜像 | ✗ | ✓ |
| 镜像时自动隐藏悬浮球,停止后恢复 | ✗ | ✓ |
| 首次启动权限引导 | ✗ | ✓ |
| 简体中文界面 | ✗ | ✓ |
| 逐应用语言切换(Android 13+) | ✗ | ✓ |

### 快捷悬浮球

- 开启设置后,屏幕边缘会常驻一个可拖动的小悬浮球
- 点按它直接进入系统的应用选择器,完全跳过主界面
- 镜像悬浮层运行期间悬浮球会自动隐藏,镜像停止后自动恢复
- 需要悬浮窗权限,应用会在首次启动时主动申请
- 可在<b>设置</b>中的<b>快捷悬浮球</b>里开关

注意:悬浮球不会替你识别当前前台应用,系统选择器仍会询问要捕获哪个应用。它省掉的是
"先打开应用"这一步。

### 本地化

- 内置英文(默认)与简体中文
- 两种语言都在 `res/xml/locales_config.xml` 中声明,因此会出现在系统的"应用语言"设置里

## 权限

- <b>SYSTEM_ALERT_WINDOW</b>:用于在其他应用上方绘制镜像悬浮层和快捷悬浮球
- <b>FOREGROUND_SERVICE</b>:让镜像服务在悬浮层活动期间持续运行
- <b>FOREGROUND_SERVICE_MEDIA_PROJECTION</b>:用于捕获被镜像应用的屏幕内容
- <b>FOREGROUND_SERVICE_SPECIAL_USE</b>:用于让快捷悬浮球服务在后台保持运行
- <b>POST_NOTIFICATIONS</b>:(可选)显示带有额外操作按钮的镜像服务通知

## 联系

- support@middor.nktnet.org

## 许可

本项目采用 GNU Affero 通用公共许可证 v3.0 或更高版本。

详见 [LICENSE](./LICENSE) 文件。

## 署名

middor-Plus 是 [Middor](https://github.com/nktnet1/middor)(作者 nktnet1)的分支版本,
以相同的 GNU AGPL v3.0 或更高版本协议发布。

## 截图

<div align="center">
  <img src="./metadata/en-US/images/phoneScreenshots/001.landing-screen.png" width="275px" alt="Phone Screenshot 1" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/002.landing-screen-start.png" width="275px" alt="Phone Screenshot 2"/>&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/003.landing-screen-app-selection.png" width="275px" alt="Phone Screenshot 3" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/004.clock-app-flipped.png" width="275px" alt="Phone Screenshot 4" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/005.settings-screen.png" width="275px" alt="Phone Screenshot 5" />&nbsp;
  <img src="./metadata/en-US/images/phoneScreenshots/006.help-screen.png" width="275px" alt="Phone Screenshot 6" />
</div>
