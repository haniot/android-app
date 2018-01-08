package br.edu.uepb.nutes.haniot.model.dao.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class Server {
    private final String LOG = "Server";
    public static Server instance;
    private static Context mContext;

    /**
     * If you set a url it will be used as default and not entered by the user in the application settings
     */
//    /* PC HOME */ private final String URI_DEFAULT = "https://192.168.31.113/api/v1";
//    /* PC WIFI DOUGLAS */ private final String URI_DEFAULT = "http://192.168.50.175:3000/api/v1";
 //   /* PC ETHERNET DOUGLAS */ private final String URI_DEFAULT = "http://192.168.50.139:3000/api/v1";
//    /* PC EDSON */ private final String URI_DEFAULT = "http://192.168.50.38:3000/api/v1";
        /* PC IZABELLA */ private final String URI_DEFAULT = "http://192.168.50.88:3000/api/v1";


    private Server() {
    }
    private final String MEDIA_TYPE = "application/json; charset=utf-8";

    private JSONObject jsonObject;


    public static synchronized Server getInstance(Context context) {
        if (instance == null) instance = new Server();

        mContext = context;

        return instance;
    }

    /**
     * Action GET
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
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action POST
     *
     * @param path           String
     * @param json           String
     * @param headers        Headers
     * @param serverCallback Callback
     */
    public void post(String path, String json, Headers headers, Callback serverCallback) {
        RequestBody body = RequestBody.create(MediaType.parse(MEDIA_TYPE), json);

        Request request;
        if (headers == null) {
            request = new Request.Builder()
                    .post(body)
                    .url(urlParser(path))
                    .build();
        } else {
            request = new Request.Builder()
                    .post(body)
                    .url(urlParser(path))
                    .headers(headers)
                    .build();
        }
        sendRequest(request, serverCallback);
    }

    /**
     * Action POST
     *
     * @param path           String
     * @param json           String
     * @param serverCallback Callback
     */
    public void post(String path, String json, Callback serverCallback) {
        post(path, json, null, serverCallback);
    }

    /**
     * Action PUT
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
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Action DELETE
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
                .build();

        sendRequest(request, serverCallback);
    }

    /**
     * Request to remote server
     *
     * @param request
     * @param serverCallback
     */
    private void sendRequest(final Request request, final Callback serverCallback) {
        Certificate certificate = getCertificate();

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(getSSLSocketFactory(certificate),
                        KeyStoresTrustManager.getInstance(certificate))
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                JSONObject result = new JSONObject();
                try {
                    result.put("message", e.getMessage());
                    result.put("code", 500);
                    serverCallback.onError(result);

                    Log.i("SERVER - onFailure()", result.toString());
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONObject result = new JSONObject();
                try {
                    String jsonString = response.body().string();
                    if (jsonString.equals("Unauthorized")) {
                        result.put("unauthorized", mContext.getString(R.string.validate_unauthorized_access));
                    } else if (!jsonString.isEmpty()) {
                        result = new JSONObject(jsonString);
                    }

                    // Adds the HTTP response code to the json object
                    result.put("code", response.code());

                    if (!response.isSuccessful()) {
                        serverCallback.onError(result);
                    } else {
                        serverCallback.onSuccess(result);
                    }
                    Log.i("SERVER - onResponse()", result.toString());
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }
        });
    }


    /**
     *
     * @return
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
     * Callback to return from the remote server
     */
    public interface Callback {
        void onError(JSONObject result);

        void onSuccess(JSONObject result);
    }
}
