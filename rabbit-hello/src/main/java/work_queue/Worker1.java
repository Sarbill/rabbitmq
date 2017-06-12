package work_queue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2017/5/11 0011.
 */
public class Worker1 {

    private static final  String QUEUE_NAME="work_queue";
    private static Connection con=null;

    public static void main(String[] args) {

        try {
            final Channel channel=getChannel();
            boolean durable=true;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            System.out.println("waiting for message!");
            /**
             * 公平转发（Fair dispatch）
             或许会发现，目前的消息转发机制（Round-robin）并非是我们想要的。例如，这样一种情况，对于两个消费者，有一系列的任务，奇数任务特别耗时，而偶数任务却很轻松，这样造成一个消费者一直繁忙，另一个消费者却很快执行完任务后等待。
             造成这样的原因是因为RabbitMQ仅仅是当消息到达队列进行转发消息。并不在乎有多少任务消费者并未传递一个应答给RabbitMQ。仅仅盲目转发所有的奇数给一个消费者，偶数给另一个消费者。
             为了解决这样的问题，我们可以使用basicQos方法，传递参数为prefetchCount = 1。这样告诉RabbitMQ不要在同一时间给一个消费者超过一条消息。换句话说，只有在消费者空闲的时候会发送下一条信息。
             *
             */
            channel.basicQos(1);
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message=new String(body,"UTF-8");

                    System.out.println("worker1 received message:"+message);
                    try {
                        doWork(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };
            channel.basicConsume(QUEUE_NAME, false, consumer);



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

    public static void doWork(String message) throws InterruptedException {
        for (char c : message.toCharArray()){
            if (c == '.') {
                Thread.sleep(1000);
            }
        }
    }
}