# MeowJoinCommand

**MeowJoinCommand** 是一个 Minecraft Bukkit 插件，可以在玩家加入时判断其是否拥有权限并执行自定义指令。  
支持多语言，默认语言为简体中文。  

**MeowJoinCommand** is a Minecraft Bukkit plugin that allows you to execute custom commands based on whether a player has specific permissions when they join the server.  
It supports multiple languages, with Simplified Chinese as the default language.

## 支持语言 / Supported Languages

| 语言代码 / Language Code | 是否支持 / Supported |
| ------------------------ | ------------------- |
| `en`                     | √                   |
| `zh_cn`                  | √                   |

> **注意**：要更改语言，请参阅配置文件。  
> **Note**: To change the language, please refer to the configuration file.

---

## 配置文件 / Configuration File

```yaml
# 配置文件版本 / Configuration version
version: 1.0

# 是否启用插件 / Whether to enable the plugin
enable_plugin: true
# true 表示启用插件，false 表示禁用插件 / Enable plugin, true / false

# 语言（en或zh_cn） / Language (en or zh_cn)
language: zh_cn
# 默认语言为简体中文 / Default language is Simplified Chinese

# 配置列表 / Configuration list
configlist:
  test1:  # 配置名称 / Configuration name
    enabled: false  # 启用此配置 / Enable this configuration
    execute:  # 执行条件 / Execute conditions
      - permission: meowjoincommand.test1  
        # 权限条件：玩家需要拥有 "meowjoincommand.test1" 权限 / Permission condition: player needs to have "meowjoincommand.test1" permission
      - permission: meowjoincommand.test0
        # 权限条件：玩家需要拥有 "meowjoincommand.test0" 权限 / Permission condition: player needs to have "meowjoincommand.test0" permission
      - money: ">50"
        # 金钱条件：玩家金钱大于 50 / Money condition: player money greater than 50
    commands:  # 满足条件后的执行指令 / Commands to execute when conditions are met
      - type: player  
        # 以玩家身份执行 / Execute as a player
        command: say Hello, %player%! 
        # 玩家执行的指令，%player% 会被替换为玩家名 / Command executed as a player, %player% will be replaced with player name
        tick_delay: 5 
        # 延迟几tick后执行 / Delay in ticks before execution
      - type: console  
        # 以服务器身份执行 / Execute as a server
        command: tell %player%, Welcome to the server! 
        # 服务器执行的指令，%player% 会被替换为玩家名 / Command executed as a server, %player% will be replaced with player name
        tick_delay: 5 
        # 延迟几tick后执行 / Delay in ticks before execution

  test2:  # 另一个配置 / Another configuration
    enabled: false  # 启用此配置 / Enable this configuration
    execute: 
      - permission: meowjoincommand.test2  # 玩家需要拥有 "meowjoincommand.test2" 权限 / Permission condition: player needs to have "meowjoincommand.test2" permission
    commands: 
      - type: player 
        command: say Hello, %player%!  
        tick_delay: 10
      - type: console  
        command: money give %player% 100 
        tick_delay: 10

---

## 说明 / Explanation

### 简体中文 / Simplified Chinese

- **执行条件**：支持 `permission` 和 `money`，`money` 的判断符号支持 `>=`、`>`、`<=`、`<`、`=`。
- **命令类型**：支持 `player`（以玩家身份执行）和 `console`（以服务器身份执行）。
- **%player%**：将在执行指令时被替换为玩家的名字。
- **tick_delay**：表示延迟多少 tick 后执行指令。在 TPS 为 20 时，一秒可以执行 20 个 tick。

如果存在多个执行条件，所有条件都需要满足才能执行指令。

---

### English

- **Execution Conditions**: Supports `permission` and `money`, where the judgment symbols for money are `>=`, `>`, `<=`, `<`, `=`.
- **Command Type**: Supports `player` (executed as a player) and `console` (executed as a server).
- **%player%**: Will be automatically replaced with the player's name when executing commands.
- **tick_delay**: Represents the delay in ticks before executing the command. At a TPS of 20, one second equals 20 ticks.

If there are multiple execution conditions, all conditions must be satisfied before the command is executed.

---

> **Note**: This English content is translated by TONGYI Lingma & ChatGPT.
