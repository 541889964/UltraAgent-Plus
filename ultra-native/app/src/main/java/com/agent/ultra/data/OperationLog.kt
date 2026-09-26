package com.agent.ultra.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OperationLog {
    private val fmt = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())

    fun write(ctx: Context, action: String, result: String) {
        if (!AgentConfig.getLogEnabled(ctx)) return
        try {
            val f = File(ctx.filesDir, "oplog.txt")
            f.appendText("[${fmt.format(Date())}] $action → $result\n")
            if (f.length() > 200_000) {
                f.writeText(f.readLines().takeLast(1000).joinToString("\n"))
            }
        } catch (_: Exception) {}
    }

    fun tail(ctx: Context, n: Int = 100): List<String> {
        val f = File(ctx.filesDir, "oplog.txt")
        if (!f.exists()) return emptyList()
        return f.readLines().takeLast(n)
    }

    fun clear(ctx: Context) {
        File(ctx.filesDir, "oplog.txt").delete()
    }
}
