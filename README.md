# mirai-console-lolicon
![GitHub top language](https://img.shields.io/github/languages/top/Samarium150/mirai-console-lolicon?style=flat)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Samarium150/mirai-console-lolicon/CI?style=flat)
![GitHub](https://img.shields.io/github/license/Samarium150/mirai-console-lolicon?style=flat)
<br>
受启发 [ACGPro](https://github.com/ShrBox/ACGPro) 而写
<br>
在群内随机发送图片(30s自动撤回)，支持关键词检索
<br>
适配Mirai-console ![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/mamoe/mirai-console)
<br>
可以在 [Lolicon API](https://api.lolicon.app/#/setu) 申请apikey来增加调用额度
## Configuration and Plugin Data
Config.yml
```yaml
# 是否启用插件, 默认为true
enabled: true
# 默认的apikey, 默认为空字符串
key: ''
```
Data.yml
```yaml
# 启用R18的群
r18Groups: []
# 自定义了apikey的用户和对应的apikey
customAPIKeyUsers: {}
# 自定义了apikey的群和对应的apikey
customAPIKeyGroups: {}
```
## Usage
```text
/lolicon help    # 获取帮助信息
/lolicon get [keyword]    # 根据关键字发送涩图, 不提供关键字则随机发送一张
/lolicon set <property> <value>
    # 设置属性
    # 可选属性:
    # r18, 对应值: true/false, 效果: 将群设置为可以发送R18图片, 私聊模式该属性强制为false
    # apikey, 对应值: default/正确的apikey, 效果: 重置apikey或设置apikey的值，私聊模式也可以更改
        # apikey设置后, 调用get时将使用设置的值, 而不是bot的默认值
```
