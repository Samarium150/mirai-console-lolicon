# mirai-console-lolicon
[![GitHub top language](https://img.shields.io/github/languages/top/Samarium150/mirai-console-lolicon?style=flat)](https://kotlinlang.org/)
[![Gradle CI](https://github.com/Samarium150/mirai-console-lolicon/workflows/Gradle%20CI/badge.svg?branch=master)](https://github.com/Samarium150/mirai-console-lolicon/actions?query=workflow%3A%22Gradle+CI%22)
[![GitHub](https://img.shields.io/github/license/Samarium150/mirai-console-lolicon?style=flat)](https://github.com/Samarium150/mirai-console-lolicon/blob/master/LICENSE)
<br>
受 [ACGPro](https://github.com/ShrBox/ACGPro) 启发而写
<br>
在群内随机发送图片(30s自动撤回+60s冷却)，支持关键词检索
<br>
适配[mirai-console](https://github.com/mamoe/mirai-console) 
[![Version](https://img.shields.io/badge/version-2.0.0-blue)](https://github.com/mamoe/mirai-console/releases/tag/2.0.0)
<br>
可以在 [Lolicon API](https://api.lolicon.app/#/setu) 申请apikey来增加调用额度
## Configuration and Plugin Data
Config.yml
```yaml
# 是否启用插件, 默认为true
enabled: true
# apikey, 默认为空字符串
apikey: ''
# 冷却时间(单位: s), 默认为60
cooldown: 60
```
Data.yml
```yaml
# 自定义了R18属性的群
customR18Groups: {}
# 自定义了apikey的用户和对应的apikey
customAPIKeyUsers: {}
# 自定义了apikey的群和对应的apikey
customAPIKeyGroups: {}
# 自定义了冷却时间的用户和对应的值
customCooldownUsers: {}
# 自定义了冷却时间的群和对应的值
customCooldownGroups: {}
```
## Usage
```text
/lolicon help    # 获取帮助信息
/lolicon get [keyword]    # (冷却时间60s)根据关键字发送涩图, 不提供关键字则随机发送一张
/lolicon set <property> <value>
    # 设置属性
    # 可选属性:
    # r18, 对应值: 0/1/2, 效果: 将群设置为non-R18/R18/mixed, 私聊模式该属性强制为0
    # apikey, 对应值: default/正确的apikey, 效果: 重置apikey或设置apikey的值，私聊模式也可以更改
        # apikey设置后, 调用get时将使用设置的值, 而不是bot的默认值
    # cooldown, 对应值: 以秒为单位的整数, 效果: 设置get命令的冷却时间, 该值默认为60s
```
