package com.example.dps;
import android.app.DownloadManager;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import com.example.dps.vo.JoinVo;
import com.example.dps.vo.LoginVo;

public interface RetrofitAPI {

    public static final String REGIST_URL = "https://10.0.2.2:8000/";

    @GET("api/userdata/{userid}")
    Call<ResponseBody> getUserdata(@Path("userid") String userid);



    @POST("api/login/")
    Call<ResponseBody> getLogin(
            @Header("X-CSRFTOKEN") String csrftoken,
            @Body LoginVo loginVo
    );

    @POST("api/createuser/")
    Call<ResponseBody> getJoin(
            @Header("X-CSRFTOKEN") String csrftoken,
            @Body JoinVo joinVo
            );



}
