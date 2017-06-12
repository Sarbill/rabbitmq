package rpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/7 0007.
 */
public class FibApplication {
    public static void main(String[] args) {

        try {
            RPCClient rpc=new RPCClient();
            String res=rpc.call("10");
            System.out.println("res:"+res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
