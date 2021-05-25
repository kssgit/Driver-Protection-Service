package com.example.dps;
import com.example.dps.vo.JoinVo;
import com.example.dps.vo.LoginVo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
// GET : csrtoken 필요 X
// POST : csrtoken 필요
public interface RetrofitAPI {

    public static final String REGIST_URL = "https://13.208.255.135:8000/";
//    public static final String REGIST_URL = "https://10.0.2.2:8000/";

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
