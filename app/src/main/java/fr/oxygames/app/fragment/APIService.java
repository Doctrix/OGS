package fr.oxygames.app.fragment;

import fr.oxygames.app.notifications.MyResponse;
import fr.oxygames.app.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAMxVtUqI:APA91bFCGAwdMRSrAvFPX9LEMU-Q6P24hnIz3ozbsN3ipBFSIXExsIWZg1PDBmwxMd_hrUzCg6CFo1ciPbDuhis7iYtTDcn5g8OeGkB46mbDGnHeBCChhF2viTF_LQOA7UoWMjpQpFHH"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}