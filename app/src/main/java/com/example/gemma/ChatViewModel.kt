package com.example.gemma
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



data class Chat(val userText: String, val fromUser: Boolean)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val welcomeMessage = Chat(
        userText = "Welcome to Lemma! How can I help you today?",false
    )

    private val _messages : MutableStateFlow<List<Chat>> = MutableStateFlow(listOf(welcomeMessage))
    val messages = _messages.asStateFlow()


    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _status = MutableStateFlow("Preparing model…")
    val status = _status.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private var engine: GemmaEngine? = null

    init {
        viewModelScope.launch {
            try {
                _status.value = "Optimizing model…"
                ModelDownloader.ensureModel(getApplication())

                _status.value = "Waking up Lemma…"
                engine = GemmaEngine.create(getApplication())

                _status.value = "Ready to chat"
                _isReady.value = true
            } catch (e: Exception) {
                Log.d("TAGGGGGGGG", "Exception: ${e::class.simpleName} ${e.printStackTrace()} ")
                _status.value = "Connection Paused"
            }
        }
    }

    fun send(userText: String) {
        val engine = engine ?: return
        if (_isGenerating.value) return

        _messages.value = _messages.value + Chat(userText, fromUser = true)
        _messages.value = _messages.value + Chat("", fromUser = false)

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                engine.reply(userText).collect { chunk ->
                    val current = _messages.value.toMutableList()
                    val last = current.last()
                    current[current.size - 1] = last.copy(userText = last.userText + chunk)
                    _messages.value = current
                }
            } catch (e: Exception) {
                val current = _messages.value.toMutableList()
                current[current.size - 1] = Chat("⚠️ Error: ${e.message}", fromUser = false)
                _messages.value = current
            } finally {
                _isGenerating.value = false
            }
        }
    }

    override fun onCleared() {
        engine?.close()
    }
}