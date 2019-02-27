package br.edu.uepb.nutes.haniot.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLSocketFactory;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Server implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.3
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class Server {
    private final String LOG = "Server";
    public static Server instance;
    private static Context mContext;
    private OkHttpClient client;

    /**
     * If you set a url it will be used as default and not entered by the user in the application settings
     */
    /* HEROKU */ private final String URI_DEFAULT = "https://haniot-frankenstein.herokuapp.com";
//    /* HEROKU */ private final String URI_DEFAULT = "https://haniot-api.herokuapp.com";
//    /* PC HOME */ private final String URI_DEFAULT = "https://192.168.31.113/api/v1";
//      /* PC WIFI */ private final String URI_DEFAULT = "http://192.168.50.175:8000/api/v1";

    private final String MEDIA_TYPE = "application/json; charset=utf-8";

    private JSONObject jsonObject;

    private Server() {
    }

    public static synchronized Server getInstance(Context context) {
        if (instance == null) instance = new Server();

        mContext = context;

        return instance;
    }

    /**
     * Action GET.
     *
     * @param path
     * @param headers
     * @param serverCallback
     */
    public void get(String path, Headers headers, Callback serverCallback) {
        final Request request = new Request.Builder()
                .get()
                .url(urlParser(path))
                .headers(headers)
                .tag(mContext.getClass().getName())
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action GET.
     * The default header will be used {@link #getHeadersDefault()}.
     *
     * @param path
     * @param serverCallback
     */
    public void get(String path, Callback serverCallback) {
        get(path, getHeadersDefault(), serverCallback);
    }

    /**
     * Action POST.
     *
     * @param path           String
     * @param json           String
     * @param headers        Headers
     * @param serverCallback Callback
     */
    public void post(String path, String json, Headers headers, Callback serverCallback) {
        RequestBody body = RequestBody.create(MediaType.parse(MEDIA_TYPE), json);

        Request request = new Request.Builder()
                .post(body)
                .url(urlParser(path))
                .headers(headers)
                .tag(mContext.getClass().getName())
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action POST.
     * The default header will be used {@link #getHeadersDefault()}.
     *
     * @param path           String
     * @param json           String
     * @param serverCallback Callback
     */
    public void post(String path, String json, Callback serverCallback) {
        post(path, json, getHeadersDefault(), serverCallback);
    }

    /**
     * Action PUT.
     *
     * @param path           String
     * @param json           String
     * @param headers        Headers
     * @param serverCallback Callback
     */
    public void put(String path, String json, Headers headers, Callback serverCallback) {
        RequestBody body = RequestBody.create(MediaType.parse(MEDIA_TYPE), json);

        Request request = new Request.Builder()
                .put(body)
                .url(urlParser(path))
                .headers(headers)
                .tag(mContext.getClass().getName())
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action PUT.
     * The default header will be used {@link #getHeadersDefault()}.
     *
     * @param path
     * @param json
     * @param serverCallback
     */
    public void put(String path, String json, Callback serverCallback) {
        put(path, json, getHeadersDefault(), serverCallback);
    }

    /**
     * Action PATCH.
     *
     * @param path           String
     * @param json           String
     * @param headers        Headers
     * @param serverCallback Callback
     */

    public void patch(String path, String json, Headers headers, Callback serverCallback) {
        RequestBody body = RequestBody.create(MediaType.parse(MEDIA_TYPE), json);

        Request request = new Request.Builder()
                .patch(body)
                .url(urlParser(path))
                .headers(headers)
                .tag(mContext.getClass().getName())
                .build();
        sendRequest(request, serverCallback);
    }

    /**
     * Action PATCH.
     * The default header will be used {@link #getHeadersDefault()}.
     *
     * @param path
     * @param json
     * @param serverCallback
     */
    public void patch(String path, String json, Callback serverCallback) {
        patch(path, json, getHeadersDefault(), serverCallback);
    }

    /**
     * Action DELETE.
     *
     * @param path           String
     * @param headers        Headers
     * @param serverCallback Callback
     */
    public void delete(String path, Headers headers, Callback serverCallback) {

        Request request = new Request.Builder()
                .delete()
                .url(urlParser(path))
                .headers(headers)
                .tag(mContext.getClass().getName())
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action DELETE.
     * The default header will be used {@link #getHeadersDefault()}.
     *
     * @param path
     * @param serverCallback
     */
    public void delete(String path, Callback serverCallback) {
        delete(path, getHeadersDefault(), serverCallback);
    }

    /**
     * Request to remote server
     *
     * @param request
     * @param serverCallback
     */
    private void sendRequest(final Request request, final Callback serverCallback) {
        Certificate certificate = getCertificate();

        client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory(certificate), KeyStoresTrustManager.getInstance(certificate))
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketException) { // request canceled
                    Log.d("SERVER - onFailure()", "Request canceled!");
                    return;
                }

                JSONObject result = new JSONObject();
                try {
                    result.put("message", e.getMessage());
                    result.put("code", 500);

                    serverCallback.onError(result);
                    Log.d("SERVER - onFailure()", result.toString());
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONObject result = new JSONObject();
                try {
                    String jsonString = response.body().string();
                    if (response.code() == 401) {
                        result.put("unauthorized", mContext.getString(R.string.validate_unauthorized_access));
                        EventBus.getDefault().post("unauthorized");
                    } else if (!jsonString.isEmpty()) {
                        Object json = new JSONTokener(jsonString).nextValue();
                        if (json instanceof JSONObject)
                            result = new JSONObject(jsonString);
                    }

                    // Adds the HTTP response code to the json object
                    result.put("code", response.code());
                    if (!response.isSuccessful()) serverCallback.onError(result);
                    else serverCallback.onSuccess(result);

                    Log.i("SERVER - onResponse()", result.toString());
                } catch (JSONException err) {
                    serverCallback.onError(null);
                    err.printStackTrace();
                }
            }
        });
    }

    /**
     * Cancel All Requests.
     */
    public void cancelAllResquest() {
        if (client != null) client.dispatcher().cancelAll();
    }

    /**
     * Cancels all as requests with a tag passed as parameter.
     *
     * @param tag
     */
    public void cancelTagRequest(String tag) {
        // go through the queued calls and cancel if the tag matches
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }

        // go through the running calls and cancel if the tag matches
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
    }

    /**
     * Retrieve the SSL certificate.
     *
     * @return Certificate
     */
    private Certificate getCertificate() {
        InputStream inputCertificate = mContext.getResources().openRawResource(R.raw.cert);
        Certificate certificate = null;
        CertificateFactory cf;

        try {
            cf = CertificateFactory.getInstance("X.509");
            inputCertificate = mContext.getResources().openRawResource(R.raw.cert);
            certificate = cf.generateCertificate(inputCertificate);
            inputCertificate.close();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return certificate;
    }

    /**
     * Select SSLSocketFactory.
     *
     * @param certificate
     * @return
     */
    private SSLSocketFactory getSSLSocketFactory(Certificate certificate) {
        KeyStoresSSLSocketFactory sslSocketFactory = null;

        try {
            sslSocketFactory = new KeyStoresSSLSocketFactory(certificate);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return sslSocketFactory;
    }

    /**
     * Handles the URL that will be used for requests to the remote server.
     *
     * @param path String - api/v1
     * @return String
     */
    public String urlParser(String path) {
        String url = "";
        Uri uri = Uri.parse(new Session(mContext).getString(mContext.getString(R.string.key_url_server)));

        /**
         * If there is no uri sitting in the application settings, it is used for the default
         */
        if (uri.getScheme() == null && uri.getAuthority() == null) {
            url = URI_DEFAULT;
        } else {
            if (uri.getScheme() == null && !uri.getScheme().equals("http") && !uri.getScheme().equals("https")) {
                url = "https://".concat(uri.getScheme());
                url += uri.getAuthority();
            } else {
                url = uri.getScheme().concat("://").concat(uri.getAuthority());
            }

            if (uri.getPath() != null) {
                for (String s : uri.getPath().split("/")) {
                    if (!s.isEmpty())
                        url += "/".concat(s);
                }
            }
        }

        if (path != null) {
            for (String s : path.split("/")) {
                if (s.length() > 0)
                    url += "/".concat(s);
            }
        }

        return url;
    }

    /**
     * Select the default header.
     * Authorization: JWT
     * Or Header empty if user is not logged in.
     *
     * @return Headers
     */
    public Headers getHeadersDefault() {
        Session session = new Session(mContext);
        if (!session.isLogged())
            return new Headers.Builder().build();

        return new Headers.Builder()
                .add("Authorization", "Bearer ".concat(session.getTokenLogged()))
                .build();
    }

    /**
     * Callback to return from the remote server
     */
    public interface Callback {
        void onError(JSONObject result);

        void onSuccess(JSONObject result);
    }
}
