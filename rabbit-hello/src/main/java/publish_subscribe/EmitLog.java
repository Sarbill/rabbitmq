package publish_subscribe;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class EmitLog {
    public static String exchange = "log";
    public static Connection con=null;
    public static void main(String[] args) {
        try {
            Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, false);
            String message = "first";
            channel.basicPublish(exchange, "", null, message.getBytes("UTF-8"));//routingKey被忽略，因为exchange的type是fanout，fanout的含义是分裂发送，所以不需要指定routingKey

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
