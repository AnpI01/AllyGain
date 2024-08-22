package com.zenzsol.allygain.email;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
@Service
public class MailJetEmailSender implements EmailManager{
   private static final	String API_KEY = "rtrtrrtr";
   private static final String API_SECRET = "37777";
   private static final String EMAIL_SENDER = "s";
   
   private static final String PASSWORD_RESETURL = "https://allygain.com/reginit/";
   
   private MailjetClient getEmailClient() {
		ClientOptions options = ClientOptions.builder()				
                .apiKey(API_KEY)
                .apiSecretKey(API_SECRET)
                .build();
        return new MailjetClient(options);
   }
   
   private MailjetRequest getEmailRequest(String recipient, String subject, String text, String html ) {	   
	   
	   JSONObject jsonEmailMsg =  new JSONObject();
	   jsonEmailMsg
	       .put(
               Emailv31.Message.FROM,
               new JSONObject().put("Email", EMAIL_SENDER).put("Name", "Allygain Support"))
           .put(
               Emailv31.Message.TO,
               new JSONArray().put(new JSONObject().put("Email", recipient)))
           .put(Emailv31.Message.SUBJECT, subject)
           .put(
               Emailv31.Message.TEXTPART,text);
	   
         if(!html.isBlank()) {
        	 jsonEmailMsg.put(
                     Emailv31.Message.HTMLPART,html);
         }
           
	   
	    MailjetRequest email =
		        new MailjetRequest(Emailv31.resource)
		            .property(
		                Emailv31.MESSAGES,
		                new JSONArray()
		                    .put(jsonEmailMsg));
	    return email;
		  
   }
   
   private MailjetResponse sendEmail(MailjetClient client, MailjetRequest request) {
	    try {
		     
		      MailjetResponse response = client.post(request);

		      //System.out.println(response.getStatus());
		     // System.out.println(response.getData());
		      return response;
		    } catch (MailjetException e) {
		      //System.out.println("Mailjet Exception: " + e);
		      return null;
		    }
   }
   
   public void sendWelcomeEmail(String recipient, String resetUrl) {
	   resetUrl = PASSWORD_RESETURL+resetUrl;
	   String subject =  "Welcome to AllyGain";
	   String emailTxt = "<p>Hello, Welcome to AllyGain. Your community manager has added you to AllyGain where you can buy/sell," 
			   + "reserve your community facilities and share stuff within the community. Below is the link to set password and login to AllyGain.</p>";
	   
	   String htmlTxt = emailTxt+"<p><a href=\""+resetUrl+"\">"+resetUrl+"</a></p>";
	   
	   MailjetRequest email = getEmailRequest(recipient, subject, "", htmlTxt);
	   sendEmail(getEmailClient(), email);
	   
   }
   public void sendResetPasswordEmail(String recipient,  String resetUrl) {
	   resetUrl = PASSWORD_RESETURL+resetUrl;
	   
	   String subject =  "Rest Password - AllyGain";
	   String emailTxt = "<p>Hello, You wanted to reset password to access AllyGain. Below is the link to reset password and login to Allygain.</p>";
	   
	   String htmlTxt = emailTxt+"<p><a href=\""+resetUrl+"\">"+resetUrl+"</a></p>";
	   
	   MailjetRequest email = getEmailRequest(recipient, subject, "", htmlTxt);
	   sendEmail(getEmailClient(), email);
   }
   public void send() {
	   
   }

@Override
public void borrowItemInformOwner(String email, String item, String borrowerResUnitId) {

	   String subject =  "Shared item to be collected - AllyGain";
	   String emailTxt = "Hello, Resident living in the unit "+borrowerResUnitId+" would like to borrow "+item+" item shared by you on AllyGain, the resident will come by and collect the item. Thank you." ;  	 
	   
	   MailjetRequest emailReq = getEmailRequest(email, subject, emailTxt, "");
	   sendEmail(getEmailClient(), emailReq);
}

@Override
public void remindBorrowerReturnItem(String email, String item, String ownerResId) {
	   String subject =  "Return the borrowed item - AllyGain";
	   String emailTxt = "Hello, Please return the item "+item+" borrowed by you to the resident living in the unit "+ownerResId+" Thank you.";  
	   
	   MailjetRequest emailReq = getEmailRequest(email, subject, emailTxt, "");
	   sendEmail(getEmailClient(), emailReq);
}


}
