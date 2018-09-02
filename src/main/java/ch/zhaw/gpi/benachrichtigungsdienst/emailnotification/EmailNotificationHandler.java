package ch.zhaw.gpi.benachrichtigungsdienst.emailnotification;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Handler, welche die Business-Logik enthält, um E-Mail-Mitteilungsaufgaben abzuarbeiten
 * 
 * Prüft, ob alle erforderlichen Angaben in der HashMap vorhanden sind und falls ja
 * wird der E-Mail-Service aufgerufen, welcher den Mail-Versand implementiert
 * 
 * @author scep
 */
@Component
public class EmailNotificationHandler {
    
    // Auslesen der Standard-Sender-Adresse, wenn keine mitgegeben wurde in der HashMap
    @Value("${mail.senderaddress}")
    private String senderAddress;
    
    // Verdrahten der Mail-Versand-Service-Klasse
    @Autowired
    private EmailService emailService;
    
    public void handleEmailNotification(HashMap<String, String> notificationTask) throws Exception {
        // Prüfen, ob die emailMessage vorhanden ist und nicht leer
        if(!notificationTask.containsKey("emailMessage") || StringUtils.isEmpty(notificationTask.get("emailMessage"))){
            throw new Exception("Nachrichteninhalt nicht gesetzt");
        }
        
        // Prüfen, ob ein Empfänger gesetzt ist
        if(!notificationTask.containsKey("emailTo") || StringUtils.isEmpty(notificationTask.get("emailTo"))){
            throw new Exception("Empfänger nicht gesetzt");
        }
        
        // Prüfen, ob das Subjekt gesetzt ist
        if(!notificationTask.containsKey("emailSubject") || StringUtils.isEmpty(notificationTask.get("emailSubject"))){
            notificationTask.put("emailSubject", "Meldung vom kantonalen Benachrichtigungsdienst Bern");
        }
        
        // Prüfen, ob der Absender gesetzt ist
        if(!notificationTask.containsKey("emailFrom") || StringUtils.isEmpty(notificationTask.get("emailFrom"))){
            notificationTask.put("emailFrom", senderAddress);
        }        
        
        // Versuchen, die Nachricht über den EMail-Service zu senden
        try {
            emailService.sendSimpleMail(notificationTask.get("emailTo"), notificationTask.get("emailSubject"), notificationTask.get("emailMessage"), notificationTask.get("emailFrom"));
        } catch (MailException mailException) {
            throw new Exception(mailException.getMessage());
        }
    }
}
