package org.mediterraneancoin.payment.wallet;

import com.azazar.bitcoin.jsonrpcclient.Bitcoin;
import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.mediterraneancoin.payment.web.WebAppListener;

/**
 *
 * @author dev
 */
public class PaymentController {
    
    public static boolean DEBUG = false;
    
    public static int NUMBER_OF_BLOCKS_TO_CHECK = 10;
    
    public static class PaymentItem {
        int blockCount;
        public String tx;
        public double txValue;        

        @Override
        public String toString() {
            return "PaymentItem{" + "blockCount=" + blockCount + ", tx=" + tx + ", txValue=" + txValue + '}';
        }
        
        
    }
    
    public static class PaymentControllerResult {
        public ArrayList<PaymentItem> payments = new ArrayList<PaymentItem>();
        
        public Date timestamp = new Date();

        @Override
        public String toString() {
            return "PaymentControllerResult{" + "payments=" + payments + ", timestamp=" + timestamp + '}';
        }
        
        
    }
    
    public static PaymentControllerResult checkForPayment(int confirmations, String medAddress, double medAmount) throws BitcoinException {
        
        BitcoinJSONRPCClient client = WebAppListener.getMedClient();
        
        PaymentControllerResult result = new PaymentControllerResult();

        // params: number of blocks to go back, address to look for
        // returns an array containing: block number, transaction, MED amount

        int currentBlockCount = client.getBlockCount();

        if (DEBUG) 
            System.out.println("currentBlockCount: " + currentBlockCount +
                    ", starting check from " + (currentBlockCount - confirmations));

        for (int blockCount = currentBlockCount - confirmations; blockCount > currentBlockCount - NUMBER_OF_BLOCKS_TO_CHECK; blockCount--) {
            String blockHash = client.getBlockHash(blockCount);

            if (DEBUG)
                System.out.println(blockCount + " " + blockHash);                    

            Bitcoin.Block block = client.getBlock(blockHash);

            List<String> transactions = block.tx();


            if (transactions != null && transactions.size() > 0) {
                ListIterator<String> listIterator = transactions.listIterator();

                int txid = 0;

                while (listIterator.hasNext()) {

                        String tx = listIterator.next();

                        if (DEBUG)
                            System.out.print("***tx*** " + tx);

                        try {

                            Bitcoin.RawTransaction rawTransaction = client.getRawTransaction(tx);

                            //System.out.println(rawTransaction);                            

                            ListIterator<Bitcoin.RawTransaction.Out> voutIterator = rawTransaction.vOut().listIterator();

                            while (voutIterator.hasNext()) {

                                Bitcoin.RawTransaction.Out next = voutIterator.next();

                                double txValue = next.value();

                                if (DEBUG)
                                    System.out.println("value: " + txValue);

                                ListIterator<String> addressesIterator = next.scriptPubKey().addresses().listIterator();

                                while (addressesIterator.hasNext()) {

                                    String outAddress = addressesIterator.next();

                                    if (DEBUG)
                                        System.out.println("address: " + outAddress);

                                    if (medAddress.equals( outAddress )) {
                                        // add to result
                                        
                                        PaymentItem item = new PaymentItem();
                                        item.blockCount = blockCount;
                                        item.tx = tx;
                                        item.txValue = txValue;
                                
                                        result.payments.add(item);

                                    }

                                }



                            }

                        } catch (BitcoinException ex) {
                            ex.printStackTrace();
                        }

                    }


            }

        }
        
        return result;
        
    }
    
}
