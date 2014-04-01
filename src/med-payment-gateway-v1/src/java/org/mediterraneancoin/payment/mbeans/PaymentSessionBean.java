package org.mediterraneancoin.payment.mbeans;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.mediterraneancoin.payment.service.PaymentService;
import org.mediterraneancoin.payment.wallet.PaymentController;
import org.mediterraneancoin.payment.web.WebAppListener;

/**
 *
 * @author jj
 */
@ManagedBean
@SessionScoped
public class PaymentSessionBean {
    private String orderNumber;

    /**
     * Creates a new instance of PaymentSessionBean
     */
    public PaymentSessionBean() {
    }
    
    private String paymentSessionAddress;
    private Date paymentSessionAddressTimestamp;
    
    private double amountDue;
    
    public String getNewPaymentAddress() throws BitcoinException {
        
        if (paymentSessionAddress == null) {
            paymentSessionAddress = WebAppListener.getMedClient().getNewAddress();
            paymentSessionAddressTimestamp = new Date();
        }
        
        return paymentSessionAddress;        
    }
    
    public Date getPaymentSessionAddressTimestamp() {
        return paymentSessionAddressTimestamp;
    }
    
    public void setAmountDue(double amountDue) {
        
        System.out.println("amountDue = " + amountDue);
        
        this.amountDue = amountDue;
    }
    
    public void setOrderNumber(String orderNumber) {
        
        System.out.println("orderNumber = " + orderNumber);
        
        this.orderNumber = orderNumber;
    }
    
    public String getPaymentCheckStatus() throws BitcoinException {
        
        //PaymentController.PaymentControllerResult checkForPayment = PaymentController.checkForPayment(3, paymentSessionAddress, amountDue);
        
        String result;
        
        if (hasPaymentBeenCompleted)
            result = "payment has been completed successfully!";
        else
            result = "payment has not been completed yet";
        
        return result;
        
        //System.out.println("getPaymentCheckStatus result: " + checkForPayment.toString());
        
        
        //return checkForPayment.toString();
    }
    
    boolean hasPaymentBeenCompleted;
    
    public void checkForPayment() {
        //
        hasPaymentBeenCompleted = PaymentService.getInstance().hasPaymentBeenCompleted(paymentSessionAddress, amountDue, orderNumber);

        
        
    }
    
}
