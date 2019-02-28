import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.Random;
import java.net.*;

//declaring the public class
public class Publisher {
    //atributes
    public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
    //public static final String BROKER_URL = "tcp://localhost:1883";
    private MqttClient client;
    String topic = "Demo Topic";

    //object instantiation
    public Publisher(){
        String clientId = getMacAddress() + "-pub";
        System.out.println("Client ID="+clientId);
            try{
                client = new MqttClient (BROKER_URL, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                final MqttTopic temperatureTopic = client.getTopic(topic);
                options.setWill(temperatureTopic, "I'm gone".getBytes(), 2, true);
                client.connect(options);
            }catch (MqttException e){
                e.printStackTrace();System.exit(1);
            }
        }

    //call for starting publishing
    public void startPublishing() throws MqttException{
            try{
                for(int i=0;i<10;i++)
                publishTemperature();
                Thread.sleep(2000);
                for(int i = 0; i<10; i++)
                    publishTemperature();
                client.disconnect();
            } catch (Exception e) {e.printStackTrace();}
        }

    //generate temperatures
    private void publishTemperature() throws MqttException {
        Random rand = new Random ();
        final int tempVal = rand.nextInt(20) + 30;
        final String temperature = tempVal + "°C";
        MqttMessage message = new MqttMessage(temperature.getBytes());
        client.publish(topic, message);
        System.out.println("Publishing"+message);
    }

    public byte[] getMacAddress(){
        byte[] mac = new byte[6];
        try{
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
            mac = nwi.getHardwareAddress();
            System.out.println(mac);
        } catch(Exception e) { System.out.println(e); }
        return mac;
    }

  //main function to call the startPublishing
  public static void main(String args[]) {
        try {
            System.out.println("MQTT Broker: " + BROKER_URL);
            new Publisher().startPublishing();
        } catch (MqttException e) {System.out.println(e); }
    }
}
