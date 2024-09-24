package com.example.voicechanger.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("inference_job_token")
    val inferenceJobToken: String?,
    @SerializedName("inference_job_token_type")
    val inferenceJobTokenType: String?
)