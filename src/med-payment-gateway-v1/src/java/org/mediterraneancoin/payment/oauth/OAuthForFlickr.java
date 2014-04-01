package org.mediterraneancoin.payment.oauth;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * a simple program to get flickr token and token secret.
 * 
 * @author Mark Zang
 * 
 */
public class OAuthForFlickr {

    //private static String key = "ck_9bd12573d4b3b557931c2dc6edc5a595";
    //private static String secret = "cs_18523a5cb427d37822c5329e4bb85de3";
    
//Consumer Key: ck_9bd12573d4b3b557931c2dc6edc5a595
//Consumer Secret: cs_18523a5cb427d37822c5329e4bb85de3    

    private static final String HMAC_SHA1 = "HmacSHA1";

    private static final String ENC = "UTF-8";

    private static Base64 base64 = new Base64();
    
    public static boolean DEBUG = true;

    /**
     * 
     * @param url
     *            the url for "request_token" URLEncoded.
     * @param params
     *            parameters string, URLEncoded.
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String getSignature(String method, String url, String params)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        
        if (method == null)
            method = "GET";
        
        /**
         * base has three parts, they are connected by "&": 1) protocol 2) URL
         * (need to be URLEncoded) 3) Parameter List (need to be URLEncoded).
         */
        StringBuilder base = new StringBuilder();
        base.append(method);
        base.append("&");
        //base.append("GET&");
        base.append(url);
        base.append("&");
        base.append(params);
        
        System.out.println("String for oauth_signature generation:" + base);
 
        
        byte[] keyBytes = WooCommerceConfiguration.secret.getBytes(ENC);

        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);

        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);

        // encode it, base64 it, change it to string and return.
        return new String(base64.encode(mac.doFinal(base.toString().getBytes(
                ENC))), ENC).trim();
    }
    

    public static HttpResponse doPost(String uri, Map requestHeaders, Map requestHttpParameters) throws IOException {
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost postMethod = new HttpPost(uri);
        
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //HttpParams params = new BasicHttpParams();
        
        if (requestHttpParameters != null) {
            Set paramNames = requestHttpParameters.keySet();
            
            for( Iterator it = paramNames.iterator(); it.hasNext(); ) {
                String paramName = (String) it.next();
                String paramValue = (String) requestHttpParameters.get( paramName );       
              
                nameValuePairs.add(new BasicNameValuePair(paramName,
                    paramValue));              
              
                if (DEBUG) {
                    System.out.println("http param: " + paramName + "=" + paramValue);
                }
            }
        }        
        
        postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // add all the request headers
        if( requestHeaders != null ) {
            Set headers = requestHeaders.keySet();
            for( Iterator it = headers.iterator(); it.hasNext(); ) {
              String headerName = (String) it.next();
              String headerValue = (String) requestHeaders.get( headerName );
              
              postMethod.addHeader(headerName, headerValue);     
              
              if (DEBUG) {
                System.out.println("http header: " + headerName + "=" + headerValue);
              }
            }
        }        

 
        HttpResponse response = client.execute(postMethod);
       
        
        return response;
       
        
    }    

    /**
     * @param args
     * @throws IOException
     * @throws ClientProtocolException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static void main(String[] args) throws ClientProtocolException,
            IOException, URISyntaxException, InvalidKeyException,
            NoSuchAlgorithmException {
        
        // https://github.com/kloon/WooCommerce-REST-API-Client-Library/blob/master/class-wc-api-client.php
        // http://gerhardpotgieter.com/2014/02/10/woocommerce-rest-api-client-library/
        // http://stackoverflow.com/questions/3756257/absolute-minimum-code-to-get-a-valid-oauth-signature-populated-in-java-or-groovy
        // http://code.pearson.com/pearson-learningstudio/apis/authentication/authentication-sample-code/sample-code-oauth-1a-java_x
        

        HttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        
        
        
        
        // These params should ordered in key
        qparams.add(new BasicNameValuePair("oauth_consumer_key", WooCommerceConfiguration.key));
        
        
        //qparams.add(new BasicNameValuePair("oauth_callback", "oob"));        
        qparams.add(new BasicNameValuePair("oauth_nonce", ""  + (int) (Math.random() * 100000000)));
        
        qparams.add(new BasicNameValuePair("oauth_signature_method", "HMAC-SHA1"));
        
        qparams.add(new BasicNameValuePair("oauth_timestamp", "" + (System.currentTimeMillis() / 1000)));
        
        //qparams.add(new BasicNameValuePair("oauth_version", "1.0"));
        
        //qparams.add(new BasicNameValuePair("status", "completed"));
        
         
        String params = URLEncoder.encode(URLEncodedUtils.format(qparams, ENC), ENC);
        
        System.out.println("URL encoded parameters: " + params);
        
        
        

        // generate the oauth_signature
        String signature = getSignature(
                "GET",
                URLEncoder.encode(
                "http://ecommerce.mediterraneancoin.org/wc-api/v1/orders/10", ENC),
                //"http://ecommerce.mediterraneancoin.org/index.php?wc-api-route=/orders/#10", ENC),
                //"http://www.flickr.com/services/oauth/request_token", ENC),
                params);

        // add it to params list
        qparams.add(new BasicNameValuePair("oauth_signature", signature));

        // generate URI which lead to access_token and token_secret.
        URI uri = URIUtils.createURI("http", "ecommerce.mediterraneancoin.org", -1,
                //"/index.php?wc-api-route=/orders/#10",
                "/wc-api/v1/orders/10",
                //"/services/oauth/request_token",
                URLEncodedUtils.format(qparams, ENC), null);

        System.out.println("Get Token and Token Secrect from:"
                + uri.toString());

        HttpGet httpget = new HttpGet(uri);
        // output the response content.
        System.out.println("Token and Token Secrect:");

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            int len;
            byte[] tmp = new byte[2048];
            while ((len = instream.read(tmp)) != -1) {
                System.out.println(new String(tmp, 0, len, ENC));
            }
        }
    }

}