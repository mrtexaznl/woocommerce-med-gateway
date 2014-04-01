package org.mediterraneancoin.payment.service;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mediterraneancoin.payment.oauth.ModifyWooCommerceOrder;
import org.mediterraneancoin.payment.wallet.PaymentController;
 

/**
 *
 * @author dev2
 */
public class PaymentService implements Runnable {
    
    public int NUM_CONFIRMATIONS = 6;
    
    /**
     * time out period for payment, expresses in minutes
     */
    public long TIME_OUT_PERIOD_MINUTES = 60;
    
    private boolean threadRunning;
    private boolean stopThread;
    
    private PaymentService() {
        new Thread(this).start();
    }
    
    private static PaymentService instance;
    
    private final LinkedBlockingQueue<PaymentServiceItem> queue = new LinkedBlockingQueue<PaymentServiceItem>();
    
    public static synchronized PaymentService getInstance() {
        
        if (instance == null) {
            instance = new PaymentService();
        }
        
        return instance;        
    }
    
        
    public void addPaymentItem(PaymentServiceItem item) {        
        
        if (item.medAddress == null || item.medAddress.equals(""))
            throw new RuntimeException("wrong MED address");
        
        if (item.amount <= 0)
            throw new RuntimeException("wrong amount");
        
        if (item.orderNumber == null || item.orderNumber.equals(""))
            throw new RuntimeException("wrong orderNumber");
        
        queue.add(item);        
    }

    @Override
    public void run() {
        
        threadRunning = true;
        
        while (!stopThread) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
            }            
            
            if (queue.isEmpty())
                continue;
            
            for (Iterator<PaymentServiceItem> iterator = queue.iterator(); iterator.hasNext();) {
                PaymentServiceItem item = iterator.next();
                                
                long delta = System.currentTimeMillis() - item.timestamp;
                
                if (delta > item.timeOutPeriod * 1000  /*TIME_OUT_PERIOD_MINUTES * 60 * 1000*/) {
                    
                    item.status = PaymentServiceItem.PaymentStatus.Cancelled;
                    
                    queue.remove(item);                    
                    
                    Logger.getLogger(PaymentService.class.getName()).log(Level.WARNING, "timeout for payment item: {0}", item);                    
                    
                    continue;
                }
                
                if (item.status != PaymentServiceItem.PaymentStatus.Processing)
                    continue;                
                
                try {
                    
                    PaymentController.PaymentControllerResult checkForPayment = PaymentController.checkForPayment(NUM_CONFIRMATIONS, 
                            item.medAddress, item.amount);
                    
                    item.lastcheck = System.currentTimeMillis();
                    
                    double totalAmount = 0;
                    
                    for (Iterator<PaymentController.PaymentItem> paymentIterator = checkForPayment.payments.iterator();
                            paymentIterator.hasNext();) {
                        
                        PaymentController.PaymentItem item2 = paymentIterator.next();
                        
                        totalAmount += item2.txValue;                        
                    }
                    
                    if (totalAmount >= item.amount) {                                                
                        // now send payment confirmation to ecommerce website
                        
                        ModifyWooCommerceOrder.modifyOrder( item.orderNumber , "completed");
                        
                        Logger.getLogger(PaymentService.class.getName()).log(Level.INFO, "payment OK: {0}", item);
                        
                        item.status = PaymentServiceItem.PaymentStatus.PaymentConfirmed;
                        
                    }
                    
                } catch (BitcoinException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PaymentService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
        threadRunning = false;
        stopThread = false;
    }
    
    public boolean hasPaymentBeenCompleted(String address, double amount, String orderNumber) {
        
        if (address == null || address.equals("") || amount <= 0 || orderNumber == "" || 
                orderNumber.equals(""))
            throw new RuntimeException("hasPaymentBeenCompleted - wrong parameters");
        
        for (Iterator<PaymentServiceItem> iterator = queue.iterator(); iterator.hasNext();) {
            
            PaymentServiceItem item = iterator.next();
            
            if (item.amount >= amount && item.medAddress.equals(address) && item.orderNumber.equals(orderNumber) &&
                    item.status == PaymentServiceItem.PaymentStatus.PaymentConfirmed)
                return true;
            
        }
        
        return false;
        
    }
    
    
    public void stopService() {
        stopThread = true;
    }
    
}
