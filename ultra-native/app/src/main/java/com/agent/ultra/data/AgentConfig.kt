package com.agent.ultra.data

import android.content.Context

object AgentConfig {
    private const val P = "agent_cfg"
    private fun sp(c: Context) = c.getSharedPreferences(P, Context.MODE_PRIVATE)

    fun getMaxTaskMs(c: Context) = sp(c).getLong("max_task_ms", 5 * 60_000L)
    fun setMaxTaskMs(c: Context, v: Long) = sp(c).edit().putLong("max_task_ms", v).apply()

    fun getMaxSteps(c: Context) = sp(c).getInt("max_steps", 15)
    fun setMaxSteps(c: Context, v: Int) = sp(c).edit().putInt("max_steps", v).apply()

    fun getModelSource(c: Context) = sp(c).getString("model_src", "default") ?: "default"
    fun setModelSource(c: Context, v: String) = sp(c).edit().putString("model_src", v).apply()

    fun getLogEnabled(c: Context) = sp(c).getBoolean("log", true)
    fun setLogEnabled(c: Context, v: Boolean) = sp(c).edit().putBoolean("log", v).apply()
}
