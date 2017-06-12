package topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 2017/6/6 0006.
 */
public class TopicTask {
    public static String exchange = "topic";
    public static Connection con=null;
    public static void main(String[] args) {
        try {
            Channel channel=getChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
            Map<String, String> map = new HashMap<String, String>();
            map.put("quick.orange.element","I'm a quick orange element");
            map.put("quick.orange.rabbit","I'm a quick orange rabbit");
            map.put("quick.green.rabbit","I'm a quick green rabbit");
            map.put("quick.green.rabbit.big","I'm a quick green rabbit big");
            map.put("lazy.green.rabbit.small","I'm a lazy green rabbit small");
            Set<Map.Entry<String,String>> set=map.entrySet();

            for (Map.Entry<String, String> entry : set) {
                channel.basicPublish(exchange, entry.getKey(), MessageProperties.PERSISTENT_TEXT_PLAIN, entry.getValue().getBytes("UTF-8"));
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
