package ch.zhaw.gpi.benachrichtigungsdienst.smsnotification;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Handler, welche die Business-Logik enthält, um SMS-Mitteilungsaufgaben abzuarbeiten
 * 
 * Prüft, ob alle erforderlichen Angaben in der HashMap vorhanden sind und falls ja
 * wird der SMS-Service aufgerufen, welcher den SMS-Versand implementiert
 * 
 * @author scep
 */
@Component
public class SmsNotificationHandler {
    
    // Verdrahten der SMS-Versand-Service-Klasse
    @Autowired
    private SmsService smsService;
    
    public void handleSmsNotification(HashMap<String, String> notificationTask) throws Exception {
        // Prüfen, ob die smsMessage vorhanden ist und nicht leer
        if(!notificationTask.containsKey("smsMessage") || StringUtils.isEmpty(notificationTask.get("smsMessage"))){
            throw new Exception("Nachrichteninhalt nicht gesetzt");
        }
        
        // Prüfen, ob ein Empfänger gesetzt ist
        if(!notificationTask.containsKey("smsTo") || StringUtils.isEmpty(notificationTask.get("smsTo"))){
            throw new Exception("Empfänger nicht gesetzt");
        }
        
        // Versuchen, die Nachricht über den SMS-Service zu senden
        try {
            smsService.sendSms(notificationTask.get("smsTo"), notificationTask.get("smsMessage"));
        } catch (Exception exception) {
            throw exception;
        }
    }
}
