package org.mediterraneancoin.payment.web;

import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.mediterraneancoin.payment.oauth.WooCommerceConfiguration;
import org.mediterraneancoin.payment.service.PaymentService;

/**
 * Web application lifecycle listener.
 *
 * @author dev3
 */
public class WebAppListener implements ServletContextListener {
  
    private URL medUrl;
    
    static private BitcoinJSONRPCClient medClient;
    
    private boolean USE_REMOTE = false;
 
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        

        System.out.println("contextInitialized");
     
        String host = null;
 
        
        if (USE_REMOTE) {

            host = "faucet.mediterraneancoin.org";
        
        } else {
 
            host = "localhost";       
            
        }        


        String medUser = "mediterraneancoinrpc";
        String medPassword = "DtkDK6xVzXyRCBUXmCpNsutv4Gpx2BfxDdrt2njMrsux";
        String medPort = "9372";
        
        try {
            medUrl = new URL("http://"+medUser+':'+medPassword+"@"+host+":"+(medPort==null?"9372":medPort)+"/");
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebAppListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    

        if (USE_REMOTE)
            medClient  = new BitcoinJSONRPCClient(medUrl);
        else 
            medClient  = new BitcoinJSONRPCClient(false);
        
        
        String initParameter = sce.getServletContext().getInitParameter("ORDER_MANAGEMENT_URL");
        
        WooCommerceConfiguration.ECOMMERCE_WEBSITE = initParameter;
        
 
        // this starts the service
        PaymentService.getInstance();
        
         
    }
    

    public static BitcoinJSONRPCClient getMedClient() {
        return medClient;
    }
 
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
 
    }
}
