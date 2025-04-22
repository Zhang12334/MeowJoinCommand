package com.meowjoincommand;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LanguageManager {
    private Map<String, String> messages = new HashMap<>();
    private FileConfiguration config;

    public LanguageManager(FileConfiguration config) {
        this.config = config;
        loadLanguage();
    }

    public LanguageManager(JavaPlugin plugin) {
        //TODO Auto-generated constructor stub
    }

    public void loadLanguage() {
        // 有效的语言列表
        Set<String> validLanguages = new HashSet<>(Arrays.asList("zh_hans", "zh_hant", "en_us", "ja_jp"));

        // 读取配置中的语言设置，默认为zh_hans
        String language = config.getString("language", "zh_hans");

        // 如果读取的语言不在有效列表中，则设为默认值
        if (!validLanguages.contains(language.toLowerCase())) {
            language = "zh_hans";
        }
        messages.clear();

        if ("zh_hans".equalsIgnoreCase(language)) {
            // 中文消息
            messages.put("TranslationContributors", "当前语言: 简体中文 (贡献者: Zhang1233)");
            messages.put("CanNotFoundMeowLibs", "未找到 MeowLibs, 请安装前置依赖 MeowLibs!");            
            messages.put("startup", "MeowJoinCommand 已加载!");
            messages.put("shutdown", "MeowJoinCommand 已卸载!");
            messages.put("nowusingversion", "当前使用版本:");
            messages.put("checkingupdate", "正在检查更新...");
            messages.put("checkfailed", "检查更新失败，请检查你的网络状况!");
            messages.put("updateavailable", "发现新版本:");
            messages.put("updatemessage", "更新内容如下:");
            messages.put("updateurl", "新版本下载地址:");
            messages.put("oldversionmaycauseproblem", "旧版本可能会导致问题，请尽快更新!");
            messages.put("nowusinglatestversion", "您正在使用最新版本!");
            messages.put("reloaded", "配置文件已重载!");
            messages.put("nopermission", "你没有权限执行此命令!");
            messages.put("pluginNotEnabled", "插件未启用!");
            messages.put("no-vault-plugin", "没有找到 Vault 插件或经济服务无法正常工作，将无法使用经济系统相关功能!");
            messages.put("no-papi-plugin", "没有找到 PlaceHolderAPI 插件或其无法正常工作，将无法使用变量相关功能!");
            messages.put("unknownConditionType", "未知的条件类型: %s");
            messages.put("incorrectConditionFormat", "条件配置格式不正确: %s");
            messages.put("tickDelayMustBeInt", "tick_delay 必须是整数, 当前值: %s");
            messages.put("unknownCommandType", "未知的命令类型: %s");
            messages.put("commandConfigFormatIncorrect", "命令配置格式不正确: %s");

        } else if ("zh_hant".equalsIgnoreCase(language)) {
            // 繁體中文消息
            messages.put("TranslationContributors", "當前語言: 繁體中文 (貢獻者: Zhang1233 & TongYi-Lingma LLM)");
            messages.put("CanNotFoundMeowLibs", "未找到 MeowLibs, 请安装前置依赖 MeowLibs!");
            messages.put("startup", "MeowJoinCommand 已載入!");
            messages.put("shutdown", "MeowJoinCommand 已卸載!");
            messages.put("nowusingversion", "當前使用版本:");
            messages.put("checkingupdate", "正在檢查更新...");
            messages.put("checkfailed", "檢查更新失敗，請檢查你的網絡狀態!");
            messages.put("updateavailable", "發現新版本:");
            messages.put("updatemessage", "更新內容如下:");
            messages.put("updateurl", "新版本下載地址:");
            messages.put("oldversionmaycauseproblem", "舊版本可能會導致問題，請尽快更新!");
            messages.put("nowusinglatestversion", "您正在使用最新版本!");
            messages.put("reloaded", "配置文件已重載!");
            messages.put("nopermission", "你沒有權限執行此命令!");
            messages.put("pluginNotEnabled", "插件未啟用!");
            messages.put("no-vault-plugin", "沒有找到 Vault 插件或經濟服務無法正常工作，將無法使用經濟系統相關功能!");
            messages.put("no-papi-plugin", "沒有找到 PlaceHolderAPI 插件或其無法正常工作，將無法使用變量相關功能!");
            messages.put("unknownConditionType", "未知的條件類型: %s");
            messages.put("incorrectConditionFormat", "條件配置格式不正確: %s");
            messages.put("tickDelayMustBeInt", "tick_delay 必須是整數, 當前值: %s");
            messages.put("unknownCommandType", "未知的命令類型: %s");
            messages.put("commandConfigFormatIncorrect", "命令配置格式不正確: %s");            

        } else if ("en_us".equalsIgnoreCase(language)) {
            // English messages
            messages.put("TranslationContributors", "Current Language: English (Contributors: Zhang1233)");
            messages.put("CanNotFoundMeowLibs", "MeowLibs not found, please install the dependency MeowLibs!");
            messages.put("startup", "MeowJoinCommand has been loaded!");
            messages.put("shutdown", "MeowJoinCommand has been disabled!");
            messages.put("nowusingversion", "Currently using version:");
            messages.put("checkingupdate", "Checking for updates...");
            messages.put("checkfailed", "Update check failed, please check your network!");
            messages.put("updateavailable", "A new version is available:");
            messages.put("updatemessage", "Update content:");
            messages.put("updateurl", "Download update at:");
            messages.put("oldversionmaycauseproblem", "Old versions may cause problems!");
            messages.put("nowusinglatestversion", "You are using the latest version!");
            messages.put("reloaded", "Configuration file has been reloaded!");
            messages.put("nopermission", "You do not have permission to execute this command!");
            messages.put("pluginNotEnabled", "Plugin not enabled!");
            messages.put("no-vault-plugin", "No Vault plugin found or economy service is not working properly, economy system related functions will not work!");
            messages.put("no-papi-plugin", "No PlaceHolderAPI plugin found or it is not working properly, variable related functions will not work!");
            messages.put("unknownConditionType", "Unknown condition type: %s");
            messages.put("incorrectConditionFormat", "Condition configuration format is incorrect: %s");
            messages.put("tickDelayMustBeInt", "tick_delay must be an integer, current value: %s");
            messages.put("unknownCommandType", "Unknown command type: %s");
            messages.put("commandConfigFormatIncorrect", "Command configuration format is incorrect: %s");

        } else if ("ja_jp".equalsIgnoreCase(language)) {
            // 日本语消息
            messages.put("TranslationContributors", "現在の言語: 日本語 (寄稿者: Zhang1233 & TongYi-Lingma LLM)");
            messages.put("CanNotFoundMeowLibs", "MeowLibsが見つかりません。プレフィックス依存をインストールしてください!");
            messages.put("startup", "MeowJoinCommandがロードされました!");
            messages.put("shutdown", "MeowJoinCommandが無効化されました!");
            messages.put("nowusingversion", "現在使用中のバージョン:");
            messages.put("checkingupdate", "更新を確認中...");
            messages.put("checkfailed", "更新チェックに失敗しました。ネットワークを確認してください!");
            messages.put("updateavailable", "新しいバージョンが利用できます:");
            messages.put("updatemessage", "アップデート内容:");
            messages.put("updateurl", "更新をダウンロードするURL:");
            messages.put("oldversionmaycauseproblem", "古いバージョンは問題を引き起こす可能性があります!");
            messages.put("nowusinglatestversion", "現在最新バージョンを使用しています!");
            messages.put("reloaded", "設定ファイルがリロードされました!");
            messages.put("nopermission", "このコマンドの実行に権限がありません!");
            messages.put("pluginNotEnabled", "プラグインが有効化されていません!");
            messages.put("no-vault-plugin", "Vaultプラグインが見つかりませんか、経済サービスが正常に動作していないため、経済システム関連の機能は動作しません!");
            messages.put("no-papi-plugin", "PlaceHolderAPIプラグインが見つかりませんか、正常に動作していないため、変数関連の機能は動作しません!");
            messages.put("unknownConditionType", "未知の条件タイプ: %s");
            messages.put("incorrectConditionFormat", "条件構成フォーマットが正しくありません: %s");
            messages.put("tickDelayMustBeInt", "tick_delayは整数でなければなりません。現在値: %s");
            messages.put("unknownCommandType", "未知のコマンドタイプ: %s");
            messages.put("commandConfigFormatIncorrect", "コマンド構成フォーマットが正しくありません: %s");

        }
    }

    /**
     * 获取语言消息
     * @param key 消息键名
     * @return 对应的语言消息，如果不存在则返回键名
     */
    public String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }
}
