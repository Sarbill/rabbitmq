package routing;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class ReceiveLog3 {

    public static String exchange = "log1";

    public static void main(String[] args) {
        try {
            Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
            String queueName=channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, "orange");
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {


                    String message = new String(body,"UTF-8");
                    System.out.println(message);

                }
            };
            System.out.println("waiting for error message--------------");
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
