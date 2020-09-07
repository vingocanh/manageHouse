package com.example.managehouse.Retrofit;

import com.example.managehouse.Model.Dashboard;
import com.example.managehouse.Model.Donvitinh;
import com.example.managehouse.Model.Hoadon;
import com.example.managehouse.Model.Khoanthu;
import com.example.managehouse.Model.Khutro;
import com.example.managehouse.Model.Message;
import com.example.managehouse.Model.Nguoitro;
import com.example.managehouse.Model.Phongtro;
import com.example.managehouse.Model.Trinhdo;
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
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface API {

    @POST("signup")
    @FormUrlEncoded
    Observable<Message> register(@Field("name") String name,
                                 @Field("username") String username,
                                 @Field("notification_token") String notification_token,
                                 @Field("password") String password,
                                 @Field("password_confirmation") String password_confirmation
    );

    @POST("login")
    @FormUrlEncoded
    Observable<Message> login(@Field("username") String username,
                              @Field("password") String password,
                              @Field("notification_token") String notification_token,
                              @Field("remember_me") int remember
    );

    @GET("logout")
    Observable<Message> logout(@Query("user_id") int user_id,
                               @Query("device_token") String device_token);

    // khu trọ
    @GET
    Observable<List<Khutro>> getKhutroChosen(@Url String url);

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

    @GET("khoanthu/{id}")
    Observable<List<Khoanthu>> getKhoanThuKhuTro(@Path("id") int id);

    @GET("khutro/{id}/edit")
    Observable<Message> thongKeChiTiet(@Path("id") int id,
                                       @Query("year") int year);

    // phòng trọ
    @GET("phongtro?")
    Observable<List<Phongtro>> getPhongtro(@Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("sort") int sort,
                                           @Query("filter") int filter);

    @GET("phongtro/create?")
    Observable<List<Phongtro>> timKiemPhongTro(@Query("search") String search,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset,
                                               @Query("sort") int sort,
                                               @Query("filter") int filter);

    @POST("phongtro")
    @Multipart
    Observable<Message> createPhongTro(@Part("phongtro_id") RequestBody id,
                                       @Part("ten") RequestBody ten,
                                       @Part("khutro_id") RequestBody khutro_id,
                                       @Part("user_id") RequestBody user_id,
                                       @Part("gia") RequestBody gia,
                                       @Part("ngaythanhtoan") RequestBody ngaythanhtoan,
                                       @Part("chotsodien") RequestBody chotsodien,
                                       @Part("chotsonuoc") RequestBody chotsonuoc,
                                       @Part("status") RequestBody trangThai,
                                       @Part("type") RequestBody type,
                                       @Part MultipartBody.Part avatar);

    @DELETE
    Observable<Message> deletePhongTro(@Url String url);

    @GET("phongtro/{id}/edit")
    Observable<Message> thongKeChiTietPhongTro(@Path("id") int id,
                                               @Query("year") int year);

    // người trọ
    @GET("nguoitro?")
    Observable<List<Nguoitro>> getNguoitro(@Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("sort") int sort,
                                           @Query("filter") int filter);

    @GET("nguoitro/create?")
    Observable<List<Nguoitro>> timKiemNguoiTro(@Query("search") String search,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset,
                                               @Query("sort") int sort,
                                               @Query("filter") int filter);

    @POST("nguoitro")
    @Multipart
    Observable<Message> createNguoiTro(@Part("nguoitro_id") RequestBody nguoitro_id,
                                       @Part("ho") RequestBody ho,
                                       @Part("ten") RequestBody ten,
                                       @Part MultipartBody.Part avatar,
                                       @Part("ngaysinh") RequestBody ngaysinh,
                                       @Part("socmnd") RequestBody socmnd,
                                       @Part("quequan") RequestBody quequan,
                                       @Part("nghenghiep") RequestBody nghenhiep,
                                       @Part("noilamviec") RequestBody noilamviec,
                                       @Part("user_id") RequestBody user_id,
                                       @Part("ghichu") RequestBody ghichu,
                                       @Part("status") RequestBody status,
                                       @Part("type") RequestBody type,
                                       @Part("phongtro_id") RequestBody phongtro_id,
                                       @Part("khutro_id") RequestBody khutro_id,
                                       @Part("trinhdo_id") RequestBody trinhdo_id,
                                       @Part("bo_hoten") RequestBody bo_hoten,
                                       @Part("bo_namsinh") RequestBody bo_namsinh,
                                       @Part("bo_nghenghiep") RequestBody bo_nghenghiep,
                                       @Part("bo_noicongtac") RequestBody bo_noicongtac,
                                       @Part("bo_choohiennay") RequestBody bo_choohiennay,
                                       @Part("me_hoten") RequestBody me_hoten,
                                       @Part("me_nghenghiep") RequestBody me_nghenghiep,
                                       @Part("me_noicongtac") RequestBody me_noicongtac,
                                       @Part("me_choohiennay") RequestBody me_choohiennay,
                                       @Part("me_namsinh") RequestBody me_namsinh
    );

    @DELETE
    Observable<Message> deleteNguoiTro(@Url String url);

    // trình độ
    @GET
    Observable<List<Trinhdo>> getTrinhdoChosen(@Url String url);

    // hóa đơn
    @POST("hoadon")
    @FormUrlEncoded
    Observable<Message> createHoadon(@Field("hoadon_id") int hoadon_id,
                                     @Field("ten") String ten,
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
                                     @Field("check_water") int check_water,
                                     @Field("ghichu") String ghichu,
                                     @Field("status") int status,
                                     @Field("type") int type
    );

    @PUT("hoadon/{id}")
    Observable<Message> updateStatus(@Path("id") int id);

    @GET("hoadon?")
    Observable<List<Hoadon>> getHoaDon(@Query("limit") int limit,
                                       @Query("offset") int offset,
                                       @Query("sort") int sort,
                                       @Query("filter") int filter,
                                       @Query("filter_kt") int filterKhuTro);

    @GET("hoadon/create?")
    Observable<List<Hoadon>> timKiemHoaDon(@Query("search") String search,
                                           @Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("sort") int sort,
                                           @Query("filter") int filter,
                                           @Query("filter_kt") int filterKhuTro);

    @DELETE
    Observable<Message> deleteHoaDon(@Url String url);

    // khoản thu
    @GET("khoanthu?")
    Observable<List<Khoanthu>> getKhoanthu(@Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("sort") int sort,
                                           @Query("filter") int filter);

    @GET("khoanthu/create?")
    Observable<List<Khoanthu>> timKiemKhoanThu(@Query("search") String search,
                                               @Query("limit") int limit,
                                               @Query("offset") int offset,
                                               @Query("sort") int sort,
                                               @Query("filter") int filter);

    @POST("khoanthu")
    @Multipart
    Observable<Message> createKhoanThu(@Part("khoanthu_id") RequestBody id,
                                       @Part("ten") RequestBody ten,
                                       @Part("mota") RequestBody mota,
                                       @Part("user_id") RequestBody userId,
                                       @Part("status") RequestBody status,
                                       @Part("type") RequestBody type,
                                       @Part MultipartBody.Part avatar);

    @DELETE
    Observable<Message> deleteKhoanThu(@Url String url);

    @GET
    Observable<List<Donvitinh>> getDonViTinhChon(@Url String url);

    // tổng quan
    @GET("dashboard")
    Observable<Dashboard> getDashboard(@Query("year") int year);

    @GET("dashboard/create")
    Observable<List<Hoadon>> thongKeChiTietDoanhThu(@Query("khutro_id") int khutro_id,
                                                    @Query("month_first") int month_first,
                                                    @Query("year_first") int year_first,
                                                    @Query("month_last") int month_last,
                                                    @Query("year_last") int year_last);

    //user
    @POST("user")
    @Multipart
    Observable<Message> updateUser(@Part("name") RequestBody ten,
                                   @Part("address") RequestBody address,
                                   @Part("email") RequestBody email,
                                   @Part("phone") RequestBody phone,
                                   @Part MultipartBody.Part avatar,
                                   @Part("type") RequestBody type);

    @POST("user")
    @FormUrlEncoded
    Observable<Message> changePassword(@Field("password") String password,
                                       @Field("new_password") String new_password,
                                       @Field("type") int type);

    // notification
    @GET("notification/{checked}/edit")
    Observable<Message> switchNotification(@Path("checked") int checked,
                                           @Query("token") String token);
}
