package ch.zhaw.gpi.benachrichtigungsdienst.smsnotification;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author scep
 */
@Component
public class SmsNotificationHandler {
    
    @Autowired
    private SmsService smsService;
    
    public void handleSmsNotification(HashMap<String, String> notificationTask) throws Exception {
        // Prüfen, ob die emailMessage vorhanden ist und nicht leer
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
