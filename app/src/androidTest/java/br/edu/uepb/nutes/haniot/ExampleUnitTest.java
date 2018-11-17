package br.edu.uepb.nutes.haniot;

import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String TAG = "JWT";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        JWT jwt = new JWT(token);
        String issuer = jwt.getIssuer();
        Log.i(TAG, "issuer: "+issuer);
        String subject = jwt.getSubject();
        Log.i(TAG, "subject: "+subject);
        List<String> audience = jwt.getAudience();
        Log.i(TAG, "audience: "+audience);
        Date expiresAt = jwt.getExpiresAt();
        Log.i(TAG, "expiresAt: "+expiresAt);
        Date notBefore = jwt.getNotBefore();
        Log.i(TAG, "notBefore: "+notBefore);
        Date issuedAt = jwt.getIssuedAt();
        Log.i(TAG, "issuedAt: "+issuedAt);
        String id = jwt.getId();
        Log.i(TAG, "id: "+id);
        boolean isExpired = jwt.isExpired(10); // 10 seconds leeway
        Log.i(TAG, "isExpired: "+isExpired);
        Claim claim = jwt.getClaim("isAdmin");
        Log.i(TAG, "claim: "+claim.toString());
    }
}