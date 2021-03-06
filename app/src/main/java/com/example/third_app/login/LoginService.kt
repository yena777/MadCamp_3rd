package com.example.third_app.login

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT

interface LoginService { //INPUT 서비스
    @FormUrlEncoded //인코딩 옵션
    @PUT("/user") //어떤 형태로 데이터를 전송할 것인가
    fun requestLogin(
        @Field("id") id:String, //req
        @Field("password") password:String?,
        @Field("name") name:String,
        @Field("isKakaoLogin") isKakaoLogin: Boolean

    ) : Call<Login> // 어떤 형태로 데이터를 받을 것인가 res


}
