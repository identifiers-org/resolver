package uk.ac.ebi.miriam.web;


import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.*;


/**
 *
 * @author Marco Donizelli (for BioModel.net)
 *
 */
final class MailFacade
{
    static void send(String from, String to, String subject, String content, String type)
    {
		try
		{
		    Context initial_context = new InitialContext();
		    Context context = (Context)initial_context.lookup("java:comp/env");
	
		    Session session = (Session)context.lookup("mail/Session");
	
		    Message message = new MimeMessage(session);
		    //message.setHeader(arg0, arg1);
		    message.setFrom(new InternetAddress(from));
		    InternetAddress[] recipients = new InternetAddress[1];
		    recipients[0] = new InternetAddress(to);
		    message.setRecipients(Message.RecipientType.TO, recipients);
		    message.setSubject(subject);
		    message.setContent(content, type);
	
		    Transport.send(message);
		}
		catch (NamingException e)
		{
		    throw new RuntimeException(e);
	
		}
		catch (AddressException e)
		{
		    throw new RuntimeException(e);
		}
		catch (MessagingException e)
		{
		    throw new RuntimeException(e);
		}
    }

    static void send(String from, String to, String cc, String subject, String content, String type)
    {
		try
		{
		    Context initial_context = new InitialContext();
		    Context context = (Context)initial_context.lookup("java:comp/env");
	
		    Session session = (Session)context.lookup("mail/Session");
	
		    Message message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(from));
		    InternetAddress[] recipients = new InternetAddress[2];
		    recipients[0] = new InternetAddress(to);
		    recipients[1] = new InternetAddress(cc);
		    message.setRecipient(Message.RecipientType.TO, recipients[0]);
		    message.setRecipient(Message.RecipientType.CC, recipients[1]);
		    message.setSubject(subject);
		    message.setContent(content, type);
	
		    Transport.send(message);
		}
		catch (NamingException e)
		{
		    throw new RuntimeException(e);
	
		}
		catch (AddressException e)
		{
		    throw new RuntimeException(e);
		}
		catch (MessagingException e)
		{
		    throw new RuntimeException(e);
		}
    }
}
