package tutorial;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

public class Main {
    final static String NODE_RPC_ENDPOINT = "http://localhost:7545";

    public static void main(String[] args) {

    System.out.println("Connecting to Ethereum RPC server ...");
    Web3j web3 = Web3j.build(new HttpService(NODE_RPC_ENDPOINT));
    System.out.println("Successfully connected to Ethereum");

        try {
            // web3_clientVersion returns the current client version.
            Web3ClientVersion clientVersion = web3.web3ClientVersion().send();

            //eth_blockNumber returns the number of most recent block.
            EthBlockNumber blockNumber = web3.ethBlockNumber().send();

            //eth_gasPrice, returns the current price per gas in wei.
    //        EthGasPrice gasPrice =  web3.ethGasPrice().send();

            // Print result
            System.out.println("Client version: " + clientVersion.getWeb3ClientVersion());
            System.out.println("Block number: " + blockNumber.getBlockNumber());
    //        System.out.println("Gas price: " + gasPrice.getGasPrice());

            EthGetBalance balanceWei = web3.ethGetBalance("0x976962a2dd287FFb24afa1b982Cc7073fc53bDC7", DefaultBlockParameterName.LATEST).send();
            System.out.println("balance in wei: " + balanceWei.getBalance());

            BigDecimal balanceInEther = Convert.fromWei(balanceWei.getBalance().toString(), Convert.Unit.ETHER);
            System.out.println("balance in ether: " + balanceInEther);

            String pk = "eec437bd2ae68fa73a955b2d0694377078b0079f7a22e92491b5aab542d8f698"; // Add a private key here

            // Decrypt and open the wallet into a Credential object
            Credentials credentials = Credentials.create(pk);

            System.out.println("Account address: " + credentials.getAddress());
            System.out.println("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));


            // Get the latest nonce
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger nonce =  ethGetTransactionCount.getTransactionCount();

            // Recipient address
            String recipientAddress = "0xb1ad3b09A72a6e812469828B2C23Ca870CA17eb0";

//            // Value to transfer (in wei)
//            BigInteger value = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();
//
//            // Gas Parameters
//            BigInteger gasLimit = BigInteger.valueOf(21000);
//            BigInteger gasPrice = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger();
//
//            // Prepare the rawTransaction
//            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
//                    nonce,
//                    gasPrice,
//                    gasLimit,
//                    recipientAddress,
//                    value);
//
//            // Sign the transaction
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//
//            // Send transaction
//            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
//            String transactionHash = ethSendTransaction.getTransactionHash();
//            System.out.println("transactionHash: " + transactionHash);

            // Wait for transaction to be mined
//            Optional<TransactionReceipt> transactionReceipt = null;
//            do {
//                System.out.println("checking if transaction " + transactionHash + " is mined....");
//                EthGetTransactionReceipt ethGetTransactionReceiptResp = web3.ethGetTransactionReceipt(transactionHash).send();
//                transactionReceipt = ethGetTransactionReceiptResp.getTransactionReceipt();
//                Thread.sleep(3000); // Wait 3 sec
//            } while(!transactionReceipt.isPresent());

            TransactionReceipt receipt = Transfer.sendFunds(web3, credentials, recipientAddress, BigDecimal.valueOf(1), Convert.Unit.ETHER).send();

            System.out.println("Transaction " + receipt.getTransactionHash() + " was mined in block # " + receipt.getBlockNumber());
            System.out.println("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));



        } catch(IOException | InterruptedException | TransactionException ex) {
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
