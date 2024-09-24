package com.example.voicechanger.model

import com.google.gson.annotations.SerializedName

data class TokenModel(
    @SerializedName("tts_model_token")
    val ttsModelToken: String?,
    @SerializedName("uuid_idempotency_token")
    val uuidIdempotencyToken: String?,
    @SerializedName("inference_text")
    val inferenceText: String?
)