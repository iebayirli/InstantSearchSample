package com.huawei.instantsearchsample.rest

import com.huawei.instantsearchsample.model.TokenResponse
import com.huawei.instantsearchsample.util.ApplicationConstants
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface QueryService {
    @FormUrlEncoded
    @POST(ApplicationConstants.requestToken)
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): TokenResponse
}