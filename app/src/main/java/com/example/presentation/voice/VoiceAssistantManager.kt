package com.example.presentation.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceCommandResult {
    data class Success(val message: String, val actionType: VoiceActionType) : VoiceCommandResult()
    data class Error(val message: String) : VoiceCommandResult()
}

enum class VoiceActionType {
    TAKE_MEDICATION,
    CHECK_ACTIVITY,
    CHECK_VITALS,
    EMERGENCY_SOS,
    BOOK_APPOINTMENT,
    GENERAL_HELP
}

/**
 * On-device Voice Assistant Manager.
 * Provides on-device speech synthesis (TextToSpeech) and speech recognition parsing.
 * Adheres strictly to the Safety Rule: Emergency SOS voice commands NEVER bypass the hold-to-confirm barrier.
 */
class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenTranscript = MutableStateFlow("")
    val spokenTranscript: StateFlow<String> = _spokenTranscript.asStateFlow()

    private val _assistantResponse = MutableStateFlow<String?>(null)
    val assistantResponse: StateFlow<String?> = _assistantResponse.asStateFlow()

    private val _lastActionType = MutableStateFlow<VoiceActionType?>(null)
    val lastActionType: StateFlow<VoiceActionType?> = _lastActionType.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            // Handled gracefully if TTS engine is unavailable
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(0.9f) // Slightly slower, clearer cadence for elderly users
            }
        }
    }

    fun speak(text: String) {
        _assistantResponse.value = text
        if (isTtsReady) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "manaaki_voice_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                // Handled
            }
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Handled
        }
    }

    fun startListening(onResult: (String) -> Unit) {
        _assistantResponse.value = null
        _spokenTranscript.value = ""
        _lastActionType.value = null

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _isListening.value = false
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _spokenTranscript.value = text
                            onResult(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { _spokenTranscript.value = it }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Handled
        }
        _isListening.value = false
    }

    /**
     * Parses spoken text into one of the 5 supported elderly-focused domain commands.
     */
    fun processVoiceCommand(
        query: String,
        onTakeMedication: () -> Unit,
        getStepCountText: () -> String,
        getVitalsText: () -> String,
        onArmSOS: () -> Unit,
        onOpenFacilities: () -> Unit
    ): VoiceCommandResult {
        val clean = query.lowercase(Locale.ROOT).trim()

        _spokenTranscript.value = query

        // 1. Medication Management
        if (clean.contains("medication") || clean.contains("meds") || clean.contains("pill") || clean.contains("dose") || clean.contains("taken")) {
            _lastActionType.value = VoiceActionType.TAKE_MEDICATION
            onTakeMedication()
            val speech = "I've recorded your medication as taken. Excellent work staying on track with your routine."
            speak(speech)
            return VoiceCommandResult.Success(speech, VoiceActionType.TAKE_MEDICATION)
        }

        // 2. Activity & Steps
        if (clean.contains("step") || clean.contains("walk") || clean.contains("activity") || clean.contains("routine")) {
            _lastActionType.value = VoiceActionType.CHECK_ACTIVITY
            val stepSummary = getStepCountText()
            val speech = "Here is your activity summary: $stepSummary."
            speak(speech)
            return VoiceCommandResult.Success(speech, VoiceActionType.CHECK_ACTIVITY)
        }

        // 3. Vitals & Blood Pressure
        if (clean.contains("vital") || clean.contains("pressure") || clean.contains("blood") || clean.contains("heart") || clean.contains("pulse")) {
            _lastActionType.value = VoiceActionType.CHECK_VITALS
            val vitalsSummary = getVitalsText()
            val speech = "Here is your latest health reading: $vitalsSummary."
            speak(speech)
            return VoiceCommandResult.Success(speech, VoiceActionType.CHECK_VITALS)
        }

        // 4. Emergency / SOS Safety
        // STRICT SAFETY MANDATE: A voice command must NEVER bypass the existing SOS hold-to-confirm step!
        if (clean.contains("help") || clean.contains("emergency") || clean.contains("sos") || clean.contains("ambulance") || clean.contains("call 111")) {
            _lastActionType.value = VoiceActionType.EMERGENCY_SOS
            onArmSOS()
            val speech = "Opening Emergency SOS screen. Please press and hold the red button for 3 seconds to confirm your emergency call."
            speak(speech)
            return VoiceCommandResult.Success(speech, VoiceActionType.EMERGENCY_SOS)
        }

        // 5. Care Facility & Appointment Booking
        if (clean.contains("appointment") || clean.contains("book") || clean.contains("doctor") || clean.contains("clinic") || clean.contains("gp") || clean.contains("hospital")) {
            _lastActionType.value = VoiceActionType.BOOK_APPOINTMENT
            onOpenFacilities()
            val speech = "Opening your local care facilities and GP appointment booking."
            speak(speech)
            return VoiceCommandResult.Success(speech, VoiceActionType.BOOK_APPOINTMENT)
        }

        // Fallback / Guidance
        val fallback = "I didn't quite catch that. You can say: 'Log my medication as taken', 'What's my step count today?', 'What are my vitals?', or 'Call for help'."
        speak(fallback)
        return VoiceCommandResult.Error(fallback)
    }

    fun shutdown() {
        stopSpeaking()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
