package com.example.tent1.api;

import com.example.tent1.Model.NotificacaoDados;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=AAAAWKJMhyc:APA91bFtVCk8IfixM_J0Kmbam3QSk0Tl-JHlsMKZ4HqsphLyaWj-afmiICmAO7eEe7IQmtUV1AFz6DQI631Ttt3ASIqFqjGD-U9E1MpGm43WPmlMW9CYAaQozflz_YEKjmDqTyzDG5rM",
            "Content-Type:application/json"
    })

    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);

}
