package com.agent.ultra.data

import android.content.Context

object QuickCommands {
    private const val P = "quick_cmds"
    private fun sp(c: Context) = c.getSharedPreferences(P, Context.MODE_PRIVATE)

    private val DEFAULTS = listOf(
        "打开 WiFi 设置", "打开蓝牙", "查看电池电量",
        "打开飞行模式", "截屏", "返回桌面", "打开通知栏"
    )

    fun list(c: Context): List<String> {
        val s = sp(c).getStringSet("cmds", null)
        return if (s.isNullOrEmpty()) DEFAULTS else s.toList()
    }

    fun add(c: Context, cmd: String) {
        val l = list(c).toMutableList()
        if (!l.contains(cmd)) l.add(cmd)
        sp(c).edit().putStringSet("cmds", l.toSet()).apply()
    }

    fun remove(c: Context, cmd: String) {
        val l = list(c).toMutableList()
        l.remove(cmd)
        sp(c).edit().putStringSet("cmds", l.toSet()).apply()
    }
}
