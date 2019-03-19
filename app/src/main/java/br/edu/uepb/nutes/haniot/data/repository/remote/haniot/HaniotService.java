package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.UserAccess;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface HaniotService {
    String BASE_URL_HANIOT = "https://192.168.0.120/"; // API GATEWAY LOCAL

    // auth
    @POST("auth")
    Single<UserAccess> auth(@Body User user);

    // user
    @DELETE("users/{user_id}")
    Observable<Void> deleteUserById(@Path("user_id") String userId);

    @PATCH("users/{user_id}/password")
    Observable<Void> changePassword(@Path("user_id") String userId);

    // users.admin

}
