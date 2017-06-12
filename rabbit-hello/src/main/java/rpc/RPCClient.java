package rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/7 0007.
 */
public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String routingKey = "RPC_QUEUE";
    private String exchange = "rpc";
    private String replyQueue;


    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory con = new ConnectionFactory();
        con.setHost("127.0.0.1");
        this.connection = con.newConnection();
        this.channel = connection.createChannel();
        this.replyQueue=channel.queueDeclare().getQueue();
    }

    public String call(String message) {
        try {
            final String relationid=UUID.randomUUID().toString();
            System.out.println("--------relationid:"+relationid);
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, false);
            AMQP.BasicProperties replyPro = new AMQP.BasicProperties.Builder().correlationId(relationid).replyTo(replyQueue).build();
            channel.basicPublish(exchange, routingKey, replyPro, message.getBytes("UTF-8"));

            //定义blockqueue 阻塞队列
            final BlockingQueue<String> blockingQueue=new ArrayBlockingQueue<String>(1);//1 代表队列容量

            //rpc回调消息处理
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    //根据relationId匹配当前request
                    if (relationid.equals(properties.getCorrelationId())) {
                        String res = new String(body, "UTF-8");
                        try {
                            blockingQueue.put(res);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            channel.basicAck(envelope.getDeliveryTag(),false);
                        }

                    }
                }
            };
            channel.basicConsume(replyQueue, false, consumer);
            return blockingQueue.take();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "出现异常";
    }

    public void close() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }

}
