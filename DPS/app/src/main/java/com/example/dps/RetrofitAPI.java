package com.example.dps;
import com.example.dps.vo.ChangSerialVo;
import com.example.dps.vo.JoinVo;
import com.example.dps.vo.LoginVo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
// GET : csrtoken 필요 X
// POST : csrtoken 필요
public interface RetrofitAPI {

    public static final String REGIST_URL = "https://13.208.255.135:8000/";
//    public static final String REGIST_URL = "https://10.0.2.2:8000/";

    // 사용자 데이타 가져오기
    @GET("api/userdata/{userid}")
    Call<ResponseBody> getUserdata(@Path("userid") String userid);

    // 테스트 데이터 가져오기
    @GET("api/testdata/{userid}")
    Call<ResponseBody> getTestdata(@Path("userid") String userid);

    //아이디 중복 check
    @GET("api/userIDcheck/{userid}")
    Call<ResponseBody> getUserIdCheck(@Path("userid") String userid);

    //시리얼 중복 check
    @GET("api/userSerialcheck/{serial_no1}")
    Call<ResponseBody> getUserSerialCheck(@Path("serial_no1") String serial_no1);

    //로그인
    @POST("api/login/")
    Call<ResponseBody> getLogin(
            @Header("X-CSRFTOKEN") String csrftoken,
            @Body LoginVo loginVo
    );

    //시리얼 번호 변경
    @PUT("api/userSerialChange/")
    Call<ResponseBody> changeSerial(
            @Header("X-CSRFTOKEN") String csrftoken,
            @Body ChangSerialVo changSerialVo
            );

    //새로운 사용자 생성 
    @POST("api/createuser/")
    Call<ResponseBody> getJoin(
            @Header("X-CSRFTOKEN") String csrftoken,
            @Body JoinVo joinVo
            );

    // 어제 하루 데이터 가져오기
    @GET("api/yesterdaydata/{userid}")
    Call<ResponseBody> getYesterdaydata(@Path("userid") String userid);

    // 어제 하루 데이터 가져오기
    @GET("api/co2MeanData/{userid}")
    Call<ResponseBody> getCo2Meandata(@Path("userid") String userid);

}
