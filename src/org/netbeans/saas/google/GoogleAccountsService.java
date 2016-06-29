/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.saas.google;

import java.io.IOException;
import org.netbeans.saas.RestConnection;
import org.netbeans.saas.RestResponse;

/**
 * GoogleAccountsService Service
 *
 * @author ron
 */
public class GoogleAccountsService {

    /**
     * Creates a new instance of GoogleAccountsService
     */
    public GoogleAccountsService() {
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Throwable th) {
        }
    }

    /**
     *
     * @param accountType
     * @param email
     * @param passwd
     * @param service
     * @param source
     * @return an instance of RestResponse
     * @throws java.io.IOException
     */
    public static RestResponse accountsClientLogin(String accountType, String email, String passwd, String service, String source) throws IOException {
        String apiKey = GoogleAccountsServiceAuthenticator.getApiKey();
        String[][] pathParams = new String[][]{};
        String[][] queryParams = new String[][]{{"accountType", accountType}, {"Email", email}, {"Passwd", passwd}, {"service", service}, {"source", source}};
        RestConnection conn = new RestConnection("https://www.google.com/accounts/ClientLogin", pathParams, null);
        sleep(1000);
        return conn.post(null, queryParams);
    }
}
