package com.agent.ultra.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class ChatMsg(val role: String, val content: String, val ts: Long = System.currentTimeMillis())
data class Conversation(
    val id: String,
    val title: String,
    val messages: List<ChatMsg> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

class ConversationManager(ctx: Context) {
    private val dir = File(ctx.filesDir, "convs").apply { mkdirs() }
    private val _all = MutableStateFlow<List<Conversation>>(emptyList())
    val all: StateFlow<List<Conversation>> = _all
    private val _cur = MutableStateFlow<String?>(null)
    val current: StateFlow<String?> = _cur

    init { load(); _cur.value = _all.value.firstOrNull()?.id }

    fun create(title: String = "新对话"): Conversation {
        val c = Conversation(UUID.randomUUID().toString(), title)
        _all.value = _all.value + c
        _cur.value = c.id
        save(c)
        return c
    }

    fun switch(id: String) {
        if (_all.value.any { it.id == id }) _cur.value = id
    }

    fun append(id: String, msg: ChatMsg) {
        val c = _all.value.find { it.id == id } ?: return
        val u = c.copy(messages = c.messages + msg, updatedAt = System.currentTimeMillis())
        _all.value = _all.value.map { if (it.id == id) u else it }
        save(u)
    }

    fun delete(id: String) {
        _all.value = _all.value.filter { it.id != id }
        File(dir, "$id.json").delete()
        if (_cur.value == id) _cur.value = _all.value.firstOrNull()?.id
    }

    private fun save(c: Conversation) {
        val msgs = JSONArray()
        c.messages.forEach { m ->
            msgs.put(JSONObject().apply {
                put("r", m.role); put("c", m.content); put("t", m.ts)
            })
        }
        val j = JSONObject().apply {
            put("id", c.id); put("title", c.title)
            put("created", c.createdAt); put("updated", c.updatedAt)
            put("msgs", msgs)
        }
        File(dir, "${c.id}.json").writeText(j.toString())
    }

    private fun load() {
        val out = mutableListOf<Conversation>()
        dir.listFiles()?.forEach { f ->
            try {
                val j = JSONObject(f.readText())
                val arr = j.getJSONArray("msgs")
                val ms = mutableListOf<ChatMsg>()
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    ms.add(ChatMsg(m.optString("r"), m.optString("c"), m.optLong("t")))
                }
                out.add(Conversation(
                    j.getString("id"), j.getString("title"), ms,
                    j.optLong("created"), j.optLong("updated")
                ))
            } catch (_: Exception) {}
        }
        _all.value = out.sortedByDescending { it.updatedAt }
    }
}
