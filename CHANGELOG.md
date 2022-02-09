<a name="5.2.0"></a>

## 5.2.0 (2022/02/09)

## Features

* notification message after get-command
  received ([57cc19d3](https://github.com/Samarium150/mirai-console-lolicon/commits/57cc19d3),
  closes [#76](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/76))

<a name="5.1.0"></a>

## 5.1.0 (2022/02/06)

## Features

* send image in forward messages ([fc0d98e6](https://github.com/Samarium150/mirai-console-lolicon/commits/fc0d98e6),
  closes [#75](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/75))

<a name="5.0.0"></a>

## 5.0.0 (2022/01/31)

## Refactor

* simplify caching

<a name="5.0.0-beta.7"></a>
# 5.0.0-beta.7 (2022/01/02)

## Bug Fixes

* create download folder correctly ([bf873fd6](https://github.com/Samarium150/mirai-console-lolicon/commits/bf873fd6), closes [#71](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/71), [#70](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/70))

## Refactor

* rename package

<a name="5.0.0-beta.6"></a>
# 5.0.0-beta.6 (2021/12/22)

## Refactor

* use top-level functions instead of util classes
<a name="5.0.0-beta.5"></a>
# 5.0.0-beta.5 (2021/12/21)

## Bug Fixes

* correctly close stream ([f88e4bb6](https://github.com/Samarium150/mirai-console-lolicon/commits/f88e4bb6))

<a name="5.0.0-beta.4"></a>
# 5.0.0-beta.4 (2021/12/18)

## Bug Fixes

* resolve dead locks ([7d602d4e](https://github.com/Samarium150/mirai-console-lolicon/commits/7d602d4e), closes [#67](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/67))

<a name="5.0.0-beta.3"></a>
# 5.0.0-beta.3 (2021/12/18)

## Bug Fixes

* correct cooldown ([31fac3c0](https://github.com/Samarium150/mirai-console-lolicon/commits/31fac3c0), closes [#66](https://github.com/Samarium150/mirai-console-lolicon/issues/issues/66))

<a name="5.0.0-beta.2"></a>
# 5.0.0-beta.2 (2021/12/18)

## Refactor

* redesign cooldown feature also fix cache path closes [#64](https://github.com/Samarium150/mirai-console-lolicon/issues/64)

<a name="5.0.0-beta.1"></a>
# 5.0.0-beta.1 (2021/12/13)

## Refactor

* relocate files into packages

<a name="4.2.1"></a>

# 4.2.1 (2021/12/12)

## Bug Fixes

* add https to default proxy ([5fb2c23a](https://github.com/Samarium150/mirai-console-lolicon/commits/5fb2c23a), closes [#62](https://github.com/Samarium150/mirai-console-lolicon/issues/62))

<a name="4.2.0"></a>

## 4.2.0 (2021/12/06)

## Features

* support spaced tags ([6b339d11](https://github.com/Samarium150/mirai-console-lolicon/commits/6b339d11), closes [#60](https://github.com/Samarium150/mirai-console-lolicon/issues/60))
* support tags filtering ([a44483c5](https://github.com/Samarium150/mirai-console-lolicon/commits/a44483c5), closes [#55](https://github.com/Samarium150/mirai-console-lolicon/issues/55))

<a name="4.1.4"></a>

# 4.1.4 (2021/12/02)

## Bug Fixes

* blacklisted users can use commands in normal groups ([d2fbdc7a](https://github.com/Samarium150/mirai-console-lolicon/commits/d2fbdc7a),
  closes [#59](https://github.com/Samarium150/mirai-console-lolicon/issues/59))

<a name="4.1.3"></a>

# 4.1.3 (2021/11/24)

## Bug Fixes

* resolve permission-granting conflict with LuckPerms ([75a83f20](https://github.com/Samarium150/mirai-console-lolicon/commits/75a83f20))

<a name="4.1.2"></a>
# 4.1.2 (2021/10/24)

## Bug Fixes

* correct blacklist logic ([5c4082c6](https://github.com/Samarium150/mirai-console-lolicon/commits/5c4082c6), closes [#56](https://github.com/Samarium150/mirai-console-lolicon/issues/56))

<a name="4.1.1"></a>
# 4.1.1 (2021/09/13)

## Bug Fixes

* correctly add bot master to userSet in whitelist mode ([95ff6a8e](https://github.com/Samarium150/mirai-console-lolicon/commits/95ff6a8e), closes [#53](https://github.com/Samarium150/mirai-console-lolicon/issues/53))

<a name="4.1.0"></a>
## 4.1.0 (2021/09/10)

## Features

* add white/blacklist and verbose mode ([3567f16c](https://github.com/Samarium150/mirai-console-lolicon/commits/3567f16c), closes [#50](https://github.com/Samarium150/mirai-console-lolicon/issues/50), [#47](https://github.com/Samarium150/mirai-console-lolicon/issues/47))

<a name="4.0.0"></a>
# 4.0.0 (2021/08/31)

## Features

* implement advanced get ([e8b03171](https://github.com/Samarium150/mirai-console-lolicon/commits/e8b03171))

<a name="4.0.0-beta.1"></a>
## 4.0.0-beta.1 (2021/07/18)

## Features

* adapt to Lolicon API v2 ([31b99a82](https://github.com/Samarium150/mirai-console-lolicon/commits/31b99a82))

## Breaking Changes

* due to [31b99a82](https://github.com/Samarium150/mirai-console-lolicon/commits/31b99a82eb8ff90f91c44f1a75a29102468ec3a2),
  Simple getting is now adapted to Lolicon API v2. Upgrade mirai-console to 2.7-M2

<a name="3.4.1"></a>
# 3.4.1 (2021/06/01)

## Bug Fixes

* replace deprecated functions ([d4aa0fb6](https://github.com/Samarium150/mirai-console-lolicon/commits/d4aa0fb6))

<a name="3.4.0"></a>
## 3.4.0 (2021/05/08)

## Features

* add recalling img info option ([5c0a828e](https://github.com/Samarium150/mirai-console-lolicon/commits/5c0a828e))

<a name="3.3.1"></a>
## 3.3.1 (2021/05/01)

## Bug Fixes

* re-implement proxy ([09523e14](https://github.com/Samarium150/mirai-console-lolicon/commits/09523e14))

<a name="3.3.0"></a>
## 3.3.0 (2021/04/26)

## Features

* add custom proxy settings; fix #37 ([5e837eb8](https://github.com/Samarium150/mirai-console-lolicon/commits/5e837eb8))

<a name="3.2.1"></a>
## 3.2.1 (2021/04/15)

* Unimportant changes

<a name="3.2.0"></a>
## 3.2.0 (2021/03/30)

## Features

* add custom caching and proxy options;  ([7d8d123a](https://github.com/Samarium150/mirai-console-lolicon/commits/7d8d123a), closes [#34](https://github.com/Samarium150/mirai-console-lolicon/issues/34))

<a name="3.1.0"></a>
## 3.1.0 (2021/02/15)

## Features

* enable custom reply messages, add experimental custom command name. ([7325face](https://github.com/Samarium150/mirai-console-lolicon/commits/7325face))

<a name="3.0.1"></a>
## 3.0.1 (2021/02/11)

* Unimportant changes

<a name="3.0"></a>
# 3.0 (2021/02/03)

## Bug Fixes

* resolve permission issue ([12abe1e9](https://github.com/Samarium150/mirai-console-lolicon/commits/12abe1e9))
* change default settings ([36f61107](https://github.com/Samarium150/mirai-console-lolicon/commits/36f61107))

## Features

* merge #27 ([0aa8a89c](https://github.com/Samarium150/mirai-console-lolicon/commits/0aa8a89c))

## Breaking Changes

* due to [0de677c3](https://github.com/Samarium150/mirai-console-lolicon/commits/0de677c38ce09e26202c4b6f2cd2874e8a6f1a27),
  Fixes #20; change downloading image logic and deprecate timeout settings; add reload command; update mirai-console to
  2.3.2

<a name="2.2"></a>
## 2.2 (2021/01/26)

## Features

* add options for timeout ([8c5c93cb](https://github.com/Samarium150/mirai-console-lolicon/commits/8c5c93cb), closes [#26](https://github.com/Samarium150/mirai-console-lolicon/issues/26))

<a name="2.1"></a>
## 2.1 (2021/01/25)

## Bug Fixes

* resolve the conflict with chat-command ([4a259046](https://github.com/Samarium150/mirai-console-lolicon/commits/4a259046), closes [#22](https://github.com/Samarium150/mirai-console-lolicon/issues/22))
* merge #23 ([9f5a16c7](https://github.com/Samarium150/mirai-console-lolicon/commits/9f5a16c7))

<a name="2.0"></a>
# 2.0 (2021/01/20)

## Breaking Changes

* due to [180ad2cf](https://github.com/Samarium150/mirai-console-lolicon/commits/180ad2cf80c4fbc88e3bbd9a3b5591a909b25240),
  Add more configurable properties

<a name="1.6"></a>
# 1.6 (2021/01/19)

## Refactor

* simplify recall method

## Breaking Changes

* due to [9ed8ba03](https://github.com/Samarium150/mirai-console-lolicon/commits/9ed8ba03260f322e702bde02455798a89f0f5627),
  only group owner and admins can use set command in group chats

<a name="1.4.4-1.5.2"></a>
## 1.4.4-1.5.2 (2021/01/11-2021/01/15)

* Unimportant changes

<a name="1.4.3"></a>
## 1.4.3 (2021/01/07)

## Bug Fixes

* **#10:** change r18 setting logic ([6799000e](https://github.com/Samarium150/mirai-console-lolicon/commits/6799000e))

<a name="1.4.2"></a>
## 1.4.2 (2021/01/07)

## Bug Fixes

* **#9:** add one more condition in exceptions handling ([84eecbd4](https://github.com/Samarium150/mirai-console-lolicon/commits/84eecbd4))

<a name="1.4.1"></a>
## 1.4.1 (2021/01/06)

## Bug Fixes

* **#4:** detail exceptions handling ([69508d30](https://github.com/Samarium150/mirai-console-lolicon/commits/69508d30))

<a name="1.4"></a>
# 1.4 (2021/01/04)

## Features

* implement cooldown time customization ([9de06dc9](https://github.com/Samarium150/mirai-console-lolicon/commits/9de06dc9))

<a name="1.3"></a>
# 1.3 (2020/12/31)

## Bug Fixes

* add error checking ([8775247b](https://github.com/Samarium150/mirai-console-lolicon/commits/8775247b))

<a name="1.2"></a>
# 1.2 (2020/12/31)

## Bug Fixes

* make the r18 property same as the Lolicon API ([9ed7f647](https://github.com/Samarium150/mirai-console-lolicon/commits/9ed7f647))

<a name="1.1"></a>
# 1.1 (2020/12/28)

## Features

* update to mirai-console 2.0-M2 ([834cc213](https://github.com/Samarium150/mirai-console-lolicon/commits/834cc213))

<a name="1.0"></a>
# 1.0 (2020/12/25)

## Features

* implement basic functionalities ([9356f33e](https://github.com/Samarium150/mirai-console-lolicon/commits/9356f33e))
* implement the functionality of auto-recall ([e87033e4](https://github.com/Samarium150/mirai-console-lolicon/commits/e87033e4))
* implement the functionality of cool down ([df6e1b35](https://github.com/Samarium150/mirai-console-lolicon/commits/df6e1b35))
