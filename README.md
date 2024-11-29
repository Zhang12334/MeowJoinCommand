# MeowJoinCommand

**MeowJoinCommand** 是一个 Minecraft Bukkit 插件，可以在玩家加入时判断其是否拥有权限或变量是否符合特定值并执行自定义指令。  

支持多语言，默认语言为简体中文。  

**MeowJoinCommand** is a Minecraft Bukkit plugin that can determine whether a player has the permission or placeholders value meets certain conditions and execute custom commands upon joining.  

Supports multiple languages, default language is Simplified Chinese.

If you want to use other languages, please change the language code in the configuration file.

## 支持语言列表 / Supported Languages

| 语言 / Language   | 语言代码 / Language code |支持状态 / Support Status |
|--------------------|---------------------------|---------------------------|
| 简体中文 / Simplified Chinese | zh_cn | ✅ 支持 / Supported        |
| 繁体中文 / Traditional Chinese | zh_tc | ✅ 支持 / Supported |
| 英文 / English      | en | ✅ 支持 / Supported        |
| 日语 / Japanese | ja_jp | ❌ 不支持 / Not Supported |

欢迎联系开发者提交其他语言的翻译，您的名字将会被列入感谢列表！

Feel free to contact the developer to submit translations in other languages, and your name will be included in the thanks list!

> **注意**：要更改语言，请参阅配置文件
> **Note**: To change the language, please refer to the configuration file.

---

## 配置文件 / Configuration File

> **此处仅供展示说明，请勿复制使用！请以插件生成的配置文件为准！**  
> **This section is for display purposes only! Please do not copy it!**
> **Please use the configuration file generated by the plugin!**

```yaml

# 配置文件版本 / Configuration version
version: 1.2

# 是否启用插件 / Whether to enable the plugin
enable_plugin: true
# true 表示启用插件，false 表示禁用插件
# enable plugin, true / false


# 语言（en或zh_cn）
# Language, en / zh_cn
language: zh_cn

# 配置列表
configlist:
  test1:  # 配置名称 / configuration name
    enabled: false  # 启用此配置 / enable this config
    tick_delay: 0 # 延迟几tick后才进行判断 / delay ticks before checking
    execute:  # 执行条件 / execute conditions
      - permission: meowjoincommand.test1  
          # 权限条件：玩家需要拥有 "meowjoincommand.test1" 权限
          # permission execution condition: player need to have "meowjoincommand.test1" permission
      - permission: meowjoincommand.test0
          # 权限条件：玩家需要拥有 "meowjoincommand.test0" 权限
          # permission execution condition: player need to have "meowjoincommand.test0" permission
      - money: ">50"
          # 金钱条件：玩家金钱大于 50 
          # money execution condition: player money greater than 50
      - placeholderapi: "%some_placeholder% = some_value" 
          # 占位符条件：%some_placeholder% 变量的值等于 some_value
          # placeholder execution condition: %some_placeholder% placeholder value equals some_value
      - placeholderapi: "%some_placeholder% != some_value" 
          # 占位符条件：%some_placeholder% 变量的值不等于 some_value
          # placeholder execution condition: %some_placeholder% placeholder value not equals some_value
    commands:  
      # 满足条件后的执行指令
      # commands after execution
      - type: player  
          # 以玩家身份执行 
          # executed as a player
        command: say Hello, %player%! 
          # 玩家执行的指令，%player% 会被替换为玩家名 
          # command executed as a player, %player% will be replaced with player name
        tick_delay: 5 
          # 延迟几tick后执行 
          # executed after delaying for a few ticks
      - type: console  
          # 以服务器身份执行 
          # executed as a server
        command: tell %player%, Welcome to the server! 
          # 服务器执行的指令，%player% 会被替换为玩家名 
          # command executed as a server, %player% will be replaced with player name
        tick_delay: 5 
          # 延迟几tick后执行 
          # executed after delaying for a few ticks

  test2: 
    enabled: false  
    tick_delay: 0 
    execute: 
      - permission: meowjoincommand.test2 
    commands: 
      - type: player 
        command: say Hello, %player%!  
        tick_delay: 10
      - type: console  
        command: money give %player% 100 
        tick_delay: 10
        
# 简体中文
# 执行条件支持 permission 、 placeholderapi 和 money
# money 的判断符号支持>=  > =  <=  <
# placeholderapi 的判断符号支持 = 或 !=
# command type 支持 player 及 console，分别为以玩家及服务器权限执行指令
# %player% 会被自动替换为玩家名
# 顶层的 tick_delay 为延迟几tick后才进行判断
# commands 中的 tick_delay 为延迟几tick后执行指令，在TPS为20时一秒可以执行20tick
# 如果执行条件中存在多项要求，则需全部满足才会执行指令

# English
# Execution conditions support permission, placeholderapi and money
# The money judgment symbol supports >= > = <= <
# The placeholderapi judgment symbol supports = or !=
# Command type supports player and console, which means executing commands with player or server permissions
# %player% will be automatically replaced with the player's name
# The top tick_delay is the delay in ticks before checking
# The tick_delay in commands is the delay in ticks before executing the command, which can be executed 20 ticks per second with TPS=20
# If there are multiple requirements in the execution conditions, they must all be satisfied before the command is executed
# Configuration file English content is translated by TONGYI Lingma

```

---

## 说明 / Explanation

### 简体中文

- **执行条件**：支持 `permission` 和 `money` 以及 `placeholderapi`，`money` 的判断符号支持 `>=`、`>`、`<=`、`<`、`=`，`placeholderapi` 的判断符号支持 `!=`、`=`
- **命令类型**：支持 `player`（以玩家身份执行）和 `console`（以服务器身份执行）
- **%player%**：将在执行指令时被替换为玩家的名字
- **顶层的 tick_delay**：表示延迟多少 tick 后进行判断
- **commands 中的 tick_delay**：表示延迟多少 tick 后执行指令。在 TPS 为 20 时，一秒可以执行 20 个 tick

如果存在多个执行条件，所有条件都需要满足才能执行指令。

如配置列表存在多个配置，则插件将会从上到下依次执行。

其他内容请查看插件默认配置文件的介绍

---

### English

- **Execution Conditions**: Supports `permission`, `money`, and `placeholderapi`. The comparison symbols supported by `money` are `>=`, `>`, `<=`, `<`, and `=`. The comparison symbols supported by `placeholderapi` are `!=` and `=`.
- **Command Type**: Supports `player` (executed as a player) and `console` (executed as a server).
- **%player%**: Will be automatically replaced with the player's name when executing commands.
- **Top tick_delay**: Indicates the delay in ticks before checking.
- **The tick_delay in commands**: Indicates the delay in ticks before executing the command. One tick can be executed 20 times per second with TPS=20.

If there are multiple execution conditions, all conditions must be satisfied before the command is executed.

If there are multiple configurations in the configuration list, the plugin will execute them from top to bottom.

Other content can be found in the plugin's default configuration file.

## 命令 / Commands

- 重载插件：/meowjoincommand reload
- Reload plugin: /meowjoincommand reload

## 下载 / Download
本插件为开源项目并免费提供下载使用！

您可以自行编译开发中的源代码或下载 Release 版本使用，出现问题可以提出 Issue！

同时您也可以在爱发电平台上赞助我，并通过加入QQ交流群以获得及时、迅速的技术支持与安装指导！赞助链接：https://afdian.com/a/NachoNeko_/

This plugin is an open-source project and is available for free download and use!

You can compile the source code being developed yourself or download the Release version. If you encounter any issues, feel free to raise an Issue!

You can also support me on the Afdian platform and join the QQ group for timely and efficient technical support and installation guidance. Link (Simplified Chinese only): https://afdian.com/a/NachoNeko_/

## 赞助价格表 / Pricing Table

- ¥25 元：获取插件技术支持 

  ¥25 CNY: Get technical support for the plugin.

- ¥200 元：获取插件技术支持 + 一次定制功能的机会

  ¥200 CNY: Get technical support + one opportunity for a custom feature.

## 兼容性测试 / Compatibility Testing

|        | 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.20.1 | 1.21 |
|--------|------|------|------|------|------|------|------|
| Purpur | ❓   | ❓   | ❓   | ❓   | ✅   | ✅   | ❓   |
| Paper  | ❓   | ❓   | ❓   | ❓   | ✅   | ✅   | ❓   |
| Spigot | ❓   | ❓   | ❓   | ❓   | ✅   | ✅   | ❓   |
| Bukkit | ❓   | ❓   | ❓   | ❓   | ✅   | ✅   | ❓   |

欢迎辅助开发者完善兼容性测试列表，您的名字将会被列入感谢列表！

Welcome to assist developers in improving the compatibility testing list, and your name will be included in the thanks list!

## 致谢 / Acknowledgments

| 名称 / Name | 贡献 / Contribution |
|-------------|---------------------|
| Zhang1233   | 开发 / Development   |

This plugin part of the English content is translated by ChatGPT and TONGYI Lingma.

## 许可 / License

请查看LICENSE.md

对于本项目的追加内容：
 - 允许在商业服务器中使用本插件
 - 禁止直接通过本插件及其衍生版本进行盈利（如出售插件本体等）

Please refer to LICENSE.md (Simplified Chinese only).

For additional content regarding this project:
 - Using this plugin on commercial servers is allowed.
 - Directly profiting from this plugin and its derivative versions is prohibited (e.g., selling the plugin itself).