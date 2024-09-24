package com.example.voicechanger.network

import com.example.voicechanger.model.ResponseModel
import com.example.voicechanger.model.TokenModel
import com.example.voicechanger.model.VoiceModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @POST("inference")
    suspend fun postVoice(@Body tokenModel: TokenModel): ResponseModel

    @GET("job/{jobToken}")
    suspend fun getVoice(@Path("jobToken") jobToken: String): VoiceModel
}