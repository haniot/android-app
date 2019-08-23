package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import android.content.Context;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;
import retrofit2.HttpException;

public class ErrorHandler {
    private static final int INVALID_AUTH = 401;
    private static final int NOT_PERMISSION = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUESTS_EXCEEDED = 429;
    private static final int INTERNAL_ERROR = 500;

    public static void showMessage(Context context, Throwable e) {
        if (e instanceof HttpException) {
            int message;
            HttpException httpEx = ((HttpException) e);
            switch (httpEx.code()) {
                case INVALID_AUTH:
                    message = R.string.invalid_auth;
                    break;
                case NOT_PERMISSION:
                    message = R.string.not_permission;
                    break;
                case NOT_FOUND:
                    message = R.string.error_recover_data;
                    break;
                case REQUESTS_EXCEEDED:
                    message = R.string.request_exceeded;
                    break;
                case INTERNAL_ERROR:
                    message = R.string.error_500;
                    break;
                default:
                    message = R.string.error_500;
            }
            Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show();
        }
    }
}
