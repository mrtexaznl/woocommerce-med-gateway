package org.mediterraneancoin.payment.oauth;

import static com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient.QUERY_CHARSET;
import com.azazar.krotjson.JSON;
import static com.azazar.krotjson.JSON.stringify;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author devs
 */
public class ModifyWooCommerceOrder {
    
//    private static String key = "ck_9bd12573d4b3b557931c2dc6edc5a595";
//    private static String secret = "cs_18523a5cb427d37822c5329e4bb85de3";
    
//Consumer Key: ck_9bd12573d4b3b557931c2dc6edc5a595
//Consumer Secret: cs_18523a5cb427d37822c5329e4bb85de3    

    private static final String HMAC_SHA1 = "HMAC-SHA1";

    private static final String ENC = "UTF-8";

    private static Base64 base64 = new Base64();
    
    public static boolean DEBUG = true;    
    
    
    public static void modifyOrder(String orderId, String newStatus) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException, IOException {
        

        // https://github.com/kloon/WooCommerce-REST-API-Client-Library/blob/master/class-wc-api-client.php
        // http://gerhardpotgieter.com/2014/02/10/woocommerce-rest-api-client-library/
        // http://stackoverflow.com/questions/3756257/absolute-minimum-code-to-get-a-valid-oauth-signature-populated-in-java-or-groovy
        // http://code.pearson.com/pearson-learningstudio/apis/authentication/authentication-sample-code/sample-code-oauth-1a-java_x
        

        HttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();         
        
        // These params should ordered in key
        qparams.add(new BasicNameValuePair("oauth_consumer_key", WooCommerceConfiguration.key));
         
        qparams.add(new BasicNameValuePair("oauth_nonce", ""  + (int) (Math.random() * 100000000)));
        
        qparams.add(new BasicNameValuePair("oauth_signature_method", HMAC_SHA1));
        
        qparams.add(new BasicNameValuePair("oauth_timestamp", "" + (System.currentTimeMillis() / 1000)));
 
         
        String params = URLEncoder.encode(URLEncodedUtils.format(qparams, ENC), ENC);
        
        if (DEBUG)
            System.out.println("URL encoded parameters: " + params);
        
        
        List<NameValuePair> qparams2 = new ArrayList<NameValuePair>();
        
        qparams2.add(new BasicNameValuePair("status", newStatus));
        
        String res = stringify( qparams2 ) ;
        
        if (DEBUG)
            System.out.println("json: "+ res);
        
/*        
{"order":{"id":10,"order_number":"#10","created_at":"2014-03-26T20:44:00Z","updated_at":"2014-03-28T12:26:11Z","completed_at":"2014-03-28T12:21:35Z","status":"cancelled","currency":"MED","total":"950.00","subtotal":"950.00","total_line_items_quantity":1,"total_tax":"0.00","total_shipping":"0.00","cart_tax":"0.00","shipping_tax":"0.00","total_discount":"0.00","cart_discount":"0.00","order_discount":"0.00","shipping_methods":"Free Shipping","payment_details":{"method_id":"medpayment","method_title":"Mediterraneancoin","paid":false},"billing_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT","email":"admin@netlawsrl.com","phone":"33312312321321"},"shipping_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT"},"note":"","customer_ip":"151.49.250.46","customer_user_agent":"Mozilla\/5.0 (X11; Linux x86_64; rv:28.0) Gecko\/20100101 Firefox\/28.0","customer_id":"1","view_order_url":"http:\/\/ecommerce.mediterraneancoin.org\/my-account\/view-order\/10","line_items":[{"id":1,"subtotal":"950.00","total":"950.
00","total_tax":"0.00","price":"950.00","quantity":1,"tax_class":null,"name":"My first product!!!","product_id":9,"sku":""}],"shipping_lines":[{"id":2,"method_id":"free_shipping","method_title":"Free Shipping","total":"0.00"}],"tax_lines":[],"fee_lines":[],"coupon_lines":[],"customer":{"id":1,"created_at":"2014-03-26T15:46:21Z","email":"admin@netlawsrl.com","first_name":"","last_name":"","username":"adminfgreu","last_order_id":"10","last_order_date":"2014-03-26T20:44:00Z","orders_count":1,"total_spent":"950.00","avatar_url":"http:\/\/0.gravatar.com\/avatar\/ad516503a11cd5ca435acc9bb6523536?s=96","billing_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT","email":"admin@netlawsrl.com","phone":"33312312321321"},"shipping_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT"}}}}  
  
  */ 
         

        // generate the oauth_signature
        String signature = OAuthForFlickr.getSignature(
                "PUT",
                URLEncoder.encode(
                WooCommerceConfiguration.ECOMMERCE_WEBSITE + WooCommerceConfiguration.ORDERS_API + orderId, 
                //"http://ecommerce.mediterraneancoin.org/wc-api/v1/orders/10", 
                ENC),
                //"http://ecommerce.mediterraneancoin.org/index.php?wc-api-route=/orders/#10", ENC),
                //"http://www.flickr.com/services/oauth/request_token", ENC),
                params);

        // add it to params list
        qparams.add(new BasicNameValuePair("oauth_signature", signature));

        // generate URI which lead to access_token and token_secret.
        URI uri = URIUtils.createURI("http", "ecommerce.mediterraneancoin.org", -1,
                //"/index.php?wc-api-route=/orders/#10",
                "/wc-api/v1/orders/" + orderId,
                //"/services/oauth/request_token",
                URLEncodedUtils.format(qparams, ENC), null);

        if (DEBUG)
            System.out.println("Get Token and Token Secrect from:"
                + uri.toString());
 
        HttpPut putRequest = new HttpPut(uri);
        
        putRequest.addHeader("Content-Type", "application/json");
        putRequest.addHeader("Accept", "application/json");        
        
        StringEntity input;
        
        input = new StringEntity(res);
 
        putRequest.setEntity(input);
        
 
        if (DEBUG)
            System.out.println("Token and Token Secrect:");

        HttpResponse response = httpclient.execute(putRequest);
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
    
    
    private static String stringify(List<NameValuePair> list) {
        StringBuilder b = new StringBuilder();
        b.append('{');
        boolean first = true;
        
        for (Iterator<NameValuePair> iterator = list.iterator(); iterator.hasNext(); ) {
            if (first)
                first = false;
            else
                b.append(",");
            
            NameValuePair next = iterator.next();
            
            b.append("\"" + next.getName() + "\"");
            
            b.append(':');
            
            b.append("\"" + next.getValue() + "\"");
            
        }
 
        b.append('}');
        return b.toString();
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
        
        
        if (true) {
            
            modifyOrder("10", "completed");
            
            return;
        }
        
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
        
        
        List<NameValuePair> qparams2 = new ArrayList<NameValuePair>();
        
        qparams2.add(new BasicNameValuePair("status", "completed"));
        
        String res = stringify( qparams2 ) ;
        
        System.out.println("json: "+ res);
        
/*        
{"order":{"id":10,"order_number":"#10","created_at":"2014-03-26T20:44:00Z","updated_at":"2014-03-28T12:26:11Z","completed_at":"2014-03-28T12:21:35Z","status":"cancelled","currency":"MED","total":"950.00","subtotal":"950.00","total_line_items_quantity":1,"total_tax":"0.00","total_shipping":"0.00","cart_tax":"0.00","shipping_tax":"0.00","total_discount":"0.00","cart_discount":"0.00","order_discount":"0.00","shipping_methods":"Free Shipping","payment_details":{"method_id":"medpayment","method_title":"Mediterraneancoin","paid":false},"billing_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT","email":"admin@netlawsrl.com","phone":"33312312321321"},"shipping_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT"},"note":"","customer_ip":"151.49.250.46","customer_user_agent":"Mozilla\/5.0 (X11; Linux x86_64; rv:28.0) Gecko\/20100101 Firefox\/28.0","customer_id":"1","view_order_url":"http:\/\/ecommerce.mediterraneancoin.org\/my-account\/view-order\/10","line_items":[{"id":1,"subtotal":"950.00","total":"950.
00","total_tax":"0.00","price":"950.00","quantity":1,"tax_class":null,"name":"My first product!!!","product_id":9,"sku":""}],"shipping_lines":[{"id":2,"method_id":"free_shipping","method_title":"Free Shipping","total":"0.00"}],"tax_lines":[],"fee_lines":[],"coupon_lines":[],"customer":{"id":1,"created_at":"2014-03-26T15:46:21Z","email":"admin@netlawsrl.com","first_name":"","last_name":"","username":"adminfgreu","last_order_id":"10","last_order_date":"2014-03-26T20:44:00Z","orders_count":1,"total_spent":"950.00","avatar_url":"http:\/\/0.gravatar.com\/avatar\/ad516503a11cd5ca435acc9bb6523536?s=96","billing_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT","email":"admin@netlawsrl.com","phone":"33312312321321"},"shipping_address":{"first_name":"Marco","last_name":"T.","company":"","address_1":"Via test 1","address_2":"","city":"Trieste","state":"TS","postcode":"34100","country":"IT"}}}}  
  * 
        /*new LinkedHashMap() {
            {
                put("method", method);
                put("params", params);
                put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);        */
        

        // generate the oauth_signature
        String signature = OAuthForFlickr.getSignature(
                "PUT",
                URLEncoder.encode(
                WooCommerceConfiguration.ECOMMERCE_WEBSITE + WooCommerceConfiguration.ORDERS_API + "10", 
                //"http://ecommerce.mediterraneancoin.org/wc-api/v1/orders/10", 
                ENC),
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

        //HttpGet httpget = new HttpGet(uri);
        HttpPut putRequest = new HttpPut(uri);
        
        putRequest.addHeader("Content-Type", "application/json");
        putRequest.addHeader("Accept", "application/json");        
        
        StringEntity input;
        
        input = new StringEntity(res);
 
        putRequest.setEntity(input);
        
        //httpPut.addHeader(params, params);
        // output the response content.
        System.out.println("Token and Token Secrect:");

        HttpResponse response = httpclient.execute(putRequest);
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
