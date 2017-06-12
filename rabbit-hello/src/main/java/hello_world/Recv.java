package hello_world;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/5/10 0010.
 */
public class Recv {

    public static final  String QUEUE_NAME="hello";
    public static Connection con=null;

    public static void main(String[] args) {
        try {
            Channel channel = getChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Waiting for message......");
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received Message:" + message);
                }

            };
            channel.basicConsume(QUEUE_NAME, true, consumer);//设置为true代表server默认消息为自动应答模式，设置为false消息为主动应答模式，需要消费端给出明确的处理结果

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取rabbit channel
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("127.0.0.1");
        con=factory.newConnection();
        Channel channel=con.createChannel();
        return channel;
    }
}
