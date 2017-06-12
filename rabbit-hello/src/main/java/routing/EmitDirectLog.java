package routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class EmitDirectLog {
    public static String exchange = "log1";
    public static Connection con=null;
    public static void main(String[] args) {
        try {
            Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, false);
            Map<String, String> map = new HashMap<String, String>();
            map.put("orange","errorMessage");
            map.put("green","warningMessage");
            map.put("black","infoMessage");
            Set<Map.Entry<String,String>> set=map.entrySet();

            for (Map.Entry<String, String> entry : set) {
                channel.basicPublish(exchange, entry.getKey(), null, entry.getValue().getBytes("UTF-8"));
            }


            channel.close();
            con.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("127.0.0.1");
        con=factory.newConnection();
        return con.createChannel();
    }
}
