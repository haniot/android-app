package br.edu.uepb.nutes.haniot.data.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class to be inherited for repository implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public abstract class BaseNetRepository {
    protected Context mContext;
    private OkHttpClient.Builder mClient;

    public BaseNetRepository(Context mContext) {
        this.mContext = mContext;
    }

    private Cache provideHttpCache() {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(mContext.getCacheDir(), cacheSize);
    }

    private Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation();

        return gsonBuilder.create();
    }

    private OkHttpClient provideOkHttpClient() {
        if (mClient == null) mClient = new OkHttpClient().newBuilder();
        mClient.followRedirects(false)
                .cache(provideHttpCache());

        return mClient.build();
    }

    protected void addRequestInterceptor(Interceptor interceptor) {
        if (interceptor == null) return;
        if (mClient == null) mClient = this.getUnsafeOkHttpClient();

        mClient.addInterceptor(interceptor);
    }

    protected void addResponseInterceptor(Interceptor interceptor) {
        if (interceptor == null) return;
        if (mClient == null) mClient = this.getUnsafeOkHttpClient();

        mClient.addNetworkInterceptor(interceptor);
    }

    protected Retrofit provideRetrofit(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideOkHttpClient())
                .build();
    }

    private OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
