package publish_subscribe;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class ReceiveLog1 {

    public static String exchange = "log";

    public static void main(String[] args) {
        try {
            Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);
            String queueName=channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, "");
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {


                    String message = new String(body,"UTF-8");
                    System.out.println(message);

                }
            };
            channel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        return connection.createChannel();
    }
}
