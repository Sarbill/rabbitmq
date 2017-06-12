package rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/7 0007.
 */
public class RPCServer {

    public static Connection con=null;
    public static final String exchange = "rpc";
    public static final String bindingkey="RPC_QUEUE";
    public static void main(String[] args) {
        try {
            final Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, false);
            String queueName=channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, bindingkey);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    int num = Integer.parseInt(message);
                    int resNum = fib(num);
                    String response = "";
                    response+=resNum;
                    channel.basicAck(envelope.getDeliveryTag(),false);
                    channel.basicPublish("",properties.getReplyTo(),properties,response.getBytes("UTF-8"));

                }
            };
            channel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    static int fib(int num) {
        if (num == 0) {
            return 0;
        }
        if (num == 1) {
            return 1;
        }
        return fib(num-1)+fib(num-2);
    }

    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("127.0.0.1");
        con=factory.newConnection();
        return con.createChannel();
    }
}
