package br.edu.uepb.nutes.haniot.model.dao.server;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by faels on 12/12/2017.
 */

public class TrustAllHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
