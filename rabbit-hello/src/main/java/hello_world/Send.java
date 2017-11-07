package hello_world;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/5/10 0010.
 */
public class Send {

    public static final  String QUEUE_NAME="hello";
    public static Connection con=null;

    //test git commit;

    public static void main(String[] args) {
        try {
            Channel channel = getChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message="hello,world!";
            channel.basicPublish("", QUEUE_NAME, false, null, message.getBytes("UTF-8"));
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
        Channel channel=con.createChannel();
        return channel;
    }
}
