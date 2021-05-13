package com.example.dps;
import android.app.DownloadManager;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;


public interface RetrofitAPI {

    public static final String REGIST_URL = "http://localhost:8000/";
    @FormUrlEncoded
    @POST("api//login/")
    Call<String> getLogin(@Header("_token") String csrftoken ,@Field("user_id") String user_id,@Field("user_pwd") String user_pwd);

    @FormUrlEncoded
    @POST("api/createuser/")
    Call<String> getUserdata( @FieldMap HashMap<String,Object> param);

    @GET("api/userdata/{user_id}")
    Call<ResponseBody> getUser_data(@Path("user_id") String user_id);




}
