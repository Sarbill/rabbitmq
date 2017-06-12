package topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class ReceiveTopic01 {

    public static String exchange = "topic";

    public static void main(String[] args) {
        try {
            final Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC,true);
            //channel.queueDeclare().getQueue();
            String queueName= channel.queueDeclare("test", true, false, false, null).getQueue();
            System.out.println("queueName:"+queueName);
            //   * (star) can substitute for exactly one word.
            //   # (hash) can substitute for zero or more words.
            //binding key 必须字符之间的连接必须用“.”分开
            channel.queueBind(queueName, exchange, "*.orange.*");
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {


                    String message = new String(body,"UTF-8");
                    System.out.println(message);
                    channel.basicAck(envelope.getDeliveryTag(), false);

                }
            };
            System.out.println("waiting for orange topic message--------------");
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
