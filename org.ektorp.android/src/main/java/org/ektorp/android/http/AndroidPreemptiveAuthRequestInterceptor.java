package org.ektorp.android.http;

import java.io.IOException;

import com.couchbase.httpclient.android.HttpException;
import com.couchbase.httpclient.android.HttpHost;
import com.couchbase.httpclient.android.HttpRequest;
import com.couchbase.httpclient.android.HttpRequestInterceptor;
import com.couchbase.httpclient.android.auth.AuthScope;
import com.couchbase.httpclient.android.auth.AuthState;
import com.couchbase.httpclient.android.auth.Credentials;
import com.couchbase.httpclient.android.client.CredentialsProvider;
import com.couchbase.httpclient.android.client.protocol.ClientContext;
import com.couchbase.httpclient.android.impl.auth.BasicScheme;
import com.couchbase.httpclient.android.protocol.ExecutionContext;
import com.couchbase.httpclient.android.protocol.HttpContext;
/**
 * Interceptor that preemptively introduces an instance of BasicScheme to the execution context, if no authentication has been attempted yet.
 *
 */
public class AndroidPreemptiveAuthRequestInterceptor implements HttpRequestInterceptor {

	public void process(
            final HttpRequest request,
            final HttpContext context) throws HttpException, IOException {

        AuthState authState = (AuthState) context.getAttribute(
                ClientContext.TARGET_AUTH_STATE);
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                ClientContext.CREDS_PROVIDER);
        HttpHost targetHost = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);

        // If not auth scheme has been initialized yet
        if (authState.getAuthScheme() == null) {
            AuthScope authScope = new AuthScope(
                    targetHost.getHostName(),
                    targetHost.getPort());
            // Obtain credentials matching the target host
            Credentials creds = credsProvider.getCredentials(authScope);
            // If found, generate BasicScheme preemptively
            if (creds != null) {
                authState.setAuthScheme(new BasicScheme());
                authState.setCredentials(creds);
            }
        }
    }

}
