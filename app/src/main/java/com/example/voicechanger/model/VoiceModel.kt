package com.example.voicechanger.model

import com.google.gson.annotations.SerializedName

data class VoiceModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("state") val state: VoiceState
)

data class VoiceState(
    @SerializedName("job_token") val jobToken: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("attempt_count") val attemptCount: Int?,
    @SerializedName("maybe_result_token") val maybeResultToken: String?,
    @SerializedName("maybe_public_bucket_wav_audio_path") val maybePublicBucketWavAudioPath: String?,
    @SerializedName("model_token") val modelToken: String?,
    @SerializedName("tts_model_type") val ttsModelType: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("raw_inference_text") val rawInferenceText: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)