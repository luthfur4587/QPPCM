package com.mail;

 


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import org.testng.annotations.Test;

//import com.qtpselenium.facebook.pom.util.FBConstants;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

 

public class SendMail{
	@Test
    public  void sendmail() throws Exception{
    
//		String reportFolder="FBConstants.REPORTS_PATH";
		String reportFolder=System.getProperty("user.dir")+"//Reports//";
//    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//         FileFilterDateIntervalUtils filter =
//             new FileFilterDateIntervalUtils("2010-01-04", "2050-01-20");
//         File folder =  new File(reportFolder);
//         File files[] = folder.listFiles(filter);
//        //date
         
//         String fileName=files[files.length-1].getName();
         String fileName= "QPPCM_RegressionReport.html";
         String extentFilePath=reportFolder+fileName;
         String subject="QPPCM Automation Test Report - HTML";
         String body= "Selenium Auto Generated Report- Do not Reply";
         
         // mail extent reports
         		String from="lkhan@scopeinfotechinc.com";
         		String Password="2020Password$$";
         		String Server="smtp-mail.outlook.com";
         		String port="587";
 //               String[] to={"lkhan@scopeinfotechinc.com"};
                String[] to={"lkhan@scopeinfotechinc.com", "QPP_CM@scopeinfotechinc.com"};

                String[] cc={};
                String[] bcc={};

                //This is for Gmail

                			sendMail(from,
                		            Password,
                		            Server,
                		            port,
                		            "true",
                		            "true",
                		            true,
                		            "javax.net.ssl.SSLSocketFactory",
                		            "false",
                		            to,
                		            cc,
                		            bcc,
                		            subject,
                		            body,
                		            extentFilePath,
                		            fileName);
   

    }

 

        public  static boolean sendMail(
        		final String userName,
        		final String passWord,
        		String host,
        		String port,
        		String starttls,
        		String auth,
        		boolean debug,
        		String socketFactoryClass,
        		String fallback,
        		String[] to,
        		String[] cc,
        		String[] bcc,
        		String subject,
        		String text,
        		String attachmentPath,
        		String attachmentName){



        	Properties props = new Properties();
        	
            props.put("mail.smtp.starttls.enable", starttls);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", userName);
            props.put("mail.smtp.password", passWord);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", auth);
            
//            props.put("mail.smtp.starttls.enable", starttls);
//            props.put("mail.smtp.auth",auth);
//            props.put("mail.smtp.host", host);
//            props.put("mail.smtp.port", port);

        try

        {

        	Session session = Session.getInstance(props,
        	          new javax.mail.Authenticator() {
        	            protected PasswordAuthentication getPasswordAuthentication() {
        	                return new PasswordAuthentication(userName, passWord);
        	            }
        	          });

            MimeMessage msg = new MimeMessage(session);

            //Set Subject
            msg.setSubject(subject);

            //Body Text
            BodyPart bodyContent = new MimeBodyPart();
            bodyContent.setText(text);

            //attachment start      
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = 
              new FileDataSource(attachmentPath);
            messageBodyPart.setDataHandler(
              new DataHandler(source));
            messageBodyPart.setFileName(attachmentName);
            //attachment ends
            
            //create the message part   
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(bodyContent);


            //Put parts in message
            msg.setContent(multipart);

            msg.setFrom(new InternetAddress(userName));

                        for(int i=0;i<to.length;i++){

            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));

                        }

                        for(int i=0;i<cc.length;i++){

            msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));

                        }

                        for(int i=0;i<bcc.length;i++){

            msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));

                        }

            msg.saveChanges();

                        Transport transport = session.getTransport("smtp");

                        transport.connect(host, userName, passWord);

                        transport.sendMessage(msg, msg.getAllRecipients());

                        transport.close();
                        System.out.println("Email has been sent");
                        return true;

        }

        catch (Exception mex)
        {
            mex.printStackTrace();

                        return false;
        }
        }

 

}