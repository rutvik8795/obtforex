import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;
import java.util.Timer;

import javax.mail.*;    
import javax.mail.internet.*; 


import org.json.*;

class ForexObtainerE extends TimerTask {
	
private static final String USER_AGENT = "Mozilla/5.0";

	private static final String POST_URL = "http://data.fixer.io/api/latest?access_key=<input-your-access-key>&symbols=USD,INR";

	public void run() {
	try {
		double exRate = sendPOST();
		// from, password, to, subject, message
		send("from@gmail.com","password","to@gmail.com","USD-INR",Double.toString(exRate));
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

	public static double sendPOST() throws IOException {
		URL obj = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			
			//parse the JSON response
			JSONObject jO = new JSONObject(response.toString());
			
			double USD = jO.getJSONObject("rates").getDouble("USD");
			double INR = jO.getJSONObject("rates").getDouble("INR");
			
			//the base currency is GBP, so converting the base to USD
			double exRate = INR/USD;
			
			System.out.println(exRate);
			return exRate;
			
		} else {
			System.out.println("POST request not worked");
			return 0;
		}
	}
	
	static void send(String from,String password,String to,String sub,String msg){  
        //Get properties object    
        Properties props = new Properties();    
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                  "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");    
        //get Session   
        Session session = Session.getDefaultInstance(props,    
         new javax.mail.Authenticator() {    
         protected PasswordAuthentication getPasswordAuthentication() {    
         return new PasswordAuthentication(from,password);  
         }    
        });    
        //compose message    
        try {    
         MimeMessage message = new MimeMessage(session);    
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
         message.setSubject(sub);    
         message.setText(msg);    
         //send message  
         Transport.send(message);    
         System.out.println("message sent successfully");    
        } catch (MessagingException e) {throw new RuntimeException(e);}    
           
  }  
}    


public class ForexObtainer {
	public static void main(String args[]) throws InterruptedException {

		Timer time = new Timer(); // Instantiate Timer Object
		ForexObtainerE st = new ForexObtainerE(); // Instantiate ForexObtainerE class
		time.schedule(st, 0, 3600000); // Schedule the method to send the rates every hour
	}
}