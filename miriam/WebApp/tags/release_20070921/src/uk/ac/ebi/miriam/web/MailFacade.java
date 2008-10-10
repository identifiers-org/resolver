/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2007  Marco Donizelli and Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */


package uk.ac.ebi.miriam.web;


import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.*;


/**
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2007 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Marco Donizelli (for BioModel.net)
 * @version very old, but works well, so...
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
