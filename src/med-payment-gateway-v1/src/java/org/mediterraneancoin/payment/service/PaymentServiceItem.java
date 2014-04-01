package org.mediterraneancoin.payment.service;

/**
 *
 * @author dev2
 */
public class PaymentServiceItem {
    
    public long timestamp = System.currentTimeMillis();
    
    public String medAddress;
    
    public long lastcheck;
    
    public String currency;
    
    public double amount;
    
    public String email;
    
    public String itemName;
    
    public String orderNumber;
    
    /**
     * timeout period in seconds; default: 60 minutes
     */
    public long timeOutPeriod = 60 * 60;
    
    public static enum PaymentStatus {
        Processing, PaymentConfirmed, Cancelled;
    }
    
    public PaymentStatus status = PaymentStatus.Processing;

    @Override
    public String toString() {
        return "PaymentServiceItem{" + "timestamp=" + timestamp + ", medAddress=" + medAddress + ", lastcheck=" + lastcheck + ", currency=" + currency + ", amount=" + amount + ", email=" + email + ", itemName=" + itemName + ", orderNumber=" + orderNumber + ", status=" + status + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        hash = 23 * hash + (this.medAddress != null ? this.medAddress.hashCode() : 0);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PaymentServiceItem other = (PaymentServiceItem) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if ((this.medAddress == null) ? (other.medAddress != null) : !this.medAddress.equals(other.medAddress)) {
            return false;
        }
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        return true;
    }

 

 
   
    
    
}
