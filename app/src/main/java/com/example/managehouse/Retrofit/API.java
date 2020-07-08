package com.example.managehouse.Retrofit;

import com.example.managehouse.Model.Donvitinh;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.Model.User;

import java.util.List;

import io.reactivex.Observable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface API {

    @POST("signup")
    @FormUrlEncoded
    Observable<Message> register(@Field("name") String name,
                                 @Field("username") String username,
                                 @Field("password") String password,
                                 @Field("password_confirmation") String password_confirmation
    );

    @POST("login")
    @FormUrlEncoded
    Observable<Message> login(@Field("username") String username,
                              @Field("password") String password,
                              @Field("remember_me") int remember
    );

    @GET
    Observable<List<Khutro>> getKhutroBill(@Url String url);

    @GET("khutro?")
    Observable<List<Khutro>> getKhutro(@Query("limit") int limit,
                                       @Query("offset") int offset,
                                       @Query("sort") int sort,
                                       @Query("filter") int filter);

    @GET("khutro/create?")
    Observable<List<Khutro>> timKiemKhuTro(@Query("search") String search,
                                           @Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("sort") int sort,
                                           @Query("filter") int filter);

    @POST("khutro")
    @Multipart
    Observable<Message> createKhuTro(@Part("khutro_id") RequestBody id,
                                     @Part("ten") RequestBody ten,
                                     @Part("diachi") RequestBody diaChi,
                                     @Part("nam_xd") RequestBody nam_xd,
                                     @Part("status") RequestBody trangThai,
                                     @Part("user_id") RequestBody userId,
                                     @Part("type") RequestBody type,
                                     @Part MultipartBody.Part avatar,
                                     @Part("id_khoanthu") RequestBody idKhoanThu,
                                     @Part("gia_khoanthu") RequestBody giaKhoanThu,
                                     @Part("dvt_khoanthu") RequestBody dvtKhoanThu);

    @DELETE
    Observable<Message> deleteKhuTro(@Url String url);

    @GET("khoanthu/create")
    Observable<List<Khoanthu>> getKhoanThuKhuTro();

    @GET("phongtro?")
    Observable<List<Phongtro>> getPhongtro(@Query("id") String id);

    @POST("hoadon")
    @FormUrlEncoded
    Observable<Message> createHoadon(@Field("ten") String ten,
                                     @Field("thang") int thang,
                                     @Field("nam") int nam,
                                     @Field("phongtro_id") int phongtro_id,
                                     @Field("khutro_id") int khutro_id,
                                     @Field("sodiencu") int sodiencu,
                                     @Field("sodienmoi") int sodienmoi,
                                     @Field("sonuoccu") int sonuoccu,
                                     @Field("sonuocmoi") int sonuocmoi,
                                     @Field("khoanthu") String khoanthu,
                                     @Field("tongtien") int tongtien,
                                     @Field("ghichu") String ghichu
    );
    @GET
    Observable<List<Donvitinh>> getDonViTinhChon(@Url String url);
}
