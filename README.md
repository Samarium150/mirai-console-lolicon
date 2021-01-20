# mirai-console-lolicon
[![GitHub top language](https://img.shields.io/github/languages/top/Samarium150/mirai-console-lolicon?style=flat)](https://kotlinlang.org/)
[![Gradle CI](https://github.com/Samarium150/mirai-console-lolicon/workflows/Gradle%20CI/badge.svg?branch=master)](https://github.com/Samarium150/mirai-console-lolicon/actions?query=workflow%3A%22Gradle+CI%22)
[![GitHub](https://img.shields.io/github/license/Samarium150/mirai-console-lolicon?style=flat)](https://github.com/Samarium150/mirai-console-lolicon/blob/master/LICENSE)
<br>
受 [ACGPro](https://github.com/ShrBox/ACGPro) 启发而写
<br>
在群内随机发送图片(30s自动撤回+60s冷却)，支持关键词检索
<br>
适配 [mirai-console](https://github.com/mamoe/mirai-console) 
[![Version](https://img.shields.io/badge/version-2.0.0-blue)](https://github.com/mamoe/mirai-console/releases/tag/2.0.0)
<br>
可以在 [Lolicon API](https://api.lolicon.app/#/setu) 申请apikey来增加调用额度
## To-Do List
- [ ] 使用说明迁移到Repository Wiki
## Configuration and Plugin Data
Config.yml
```yaml
# Bot所有者账号
master: 0
# 是否启用插件
enabled: true
# 默认的apikey
apikey: ''
# 默认的撤回时间(单位: s)
recall: 30
# 默认的冷却时间(单位: s)
cooldown: 60
```
Data.yml
```yaml
# 受信任的用户
trustedUsers: []
# 自定义了apikey的用户和对应的apikey
customAPIKeyUsers: {}
# 自定义了apikey的群和对应的apikey
customAPIKeyGroups: {}
# 自定义了R18属性的用户
customR18Users: {}
# 自定义了R18属性的群
customR18Groups: {}
# 自定义了撤回时间的用户和对应的值
customRecallUsers: {}
# 自定义了撤回时间的用户和对应的值
customRecallGroups: {}
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
    # 设置属性, 群聊模式仅能由群主和管理员设置
    # 可选属性:
    # apikey, 对应值: default/正确的apikey, 效果: 重置apikey或设置apikey的值，私聊模式也可以更改
        # apikey设置后, 调用get时将使用设置的值, 而不是bot的默认值
    # r18, 对应值: 0/1/2, 效果: 将模式设置为non-R18/R18/mixed
        # 私聊模式仅限受信任清单中的用户设置
	# recall, 对应值: 以秒为单位的小于120的非负整数, 效果: 设置自动撤回的时间, 默认为30s, 0则不撤回
	    # 私聊模式仅限受信任清单中的用户设置
	# cooldown, 对应值: 以秒为单位的整数, 效果: 设置get命令的冷却时间, 默认为60s, 0则无冷却
	    # 私聊模式仅限受信任清单中的用户设置
/lolicon trust <id>    # 将<id>对应的用户添加到受信任清单, 仅限Bot所有者使用
/lolicon distrust <id>    # 将<id>对应的用户从受信任清单中移除, 仅限Bot所有者使用
```
