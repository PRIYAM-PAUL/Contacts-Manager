package com.springboot.manager.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;

@Service
public class Mail {
	
	public boolean sendEmail(String to,String subject, String bodyText) {
		boolean flag=false;
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port", 587);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		String username="priyampaul106";
		String password="lwmcedxcjljoutvv";
		String from="priyampaul106@gmail.com";
		Session session = Session.getInstance(properties,new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
			        return new  PasswordAuthentication(username, password);
		}});
		
		MimeMessage message = new MimeMessage(session);
		try {
		message.setRecipient(RecipientType.TO, new InternetAddress(to));
		message.setFrom(new InternetAddress(from));
		message.setSubject(subject);
		message.setContent(bodyText,"text/html");
		Transport.send(message);
		flag=true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
