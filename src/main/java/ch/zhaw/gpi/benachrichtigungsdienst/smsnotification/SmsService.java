package ch.zhaw.gpi.benachrichtigungsdienst.smsnotification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author scep
 */
@Service
public class SmsService {
    
    // Klassen-Variablen definieren
    private final Boolean debugSms;
    private final String smsSender;
    private final String smsOverrideNumber;

    /**
     * Konstruktor-Methode, um einerseits debugSms aus application.properties auszulesen
     * und anderseits die Authentifizierung beim Twilio-Service vorzunehmen
     * 
     * @param debugSms          siehe application.properties
     * @param smsSender         siehe application.properties
     * @param smsOverrideNumber siehe application.properties
     * @param twilioSid         siehe application.properties
     * @param twilioToken       siehe application.properties
     */
    public SmsService(
            @Value("${sms.debug}") Boolean debugSms,
            @Value("${sms.sender}") String smsSender,
            @Value("${sms.overrideReceiverNumber}") String smsOverrideNumber,
            @Value("${twilio.account.sid}") String twilioSid,
            @Value("${twilio.account.token}") String twilioToken
    ) {
        this.debugSms = debugSms;
        this.smsSender = smsSender;
        this.smsOverrideNumber = smsOverrideNumber;
        Twilio.init(twilioSid, twilioToken);
    }
    
    
    
    public void sendSms(String to, String message) throws Exception {
        if(debugSms){
            debugSms(to, message);
        } else {
            if(!smsOverrideNumber.equals("0")){
                to = smsOverrideNumber;
            }
            
            Message smsMessage = Message
                    .creator(
                            new PhoneNumber(to),
                            new PhoneNumber(smsSender),
                            message
                    ).create();
        }
    }
    
    private void debugSms(String to, String message) {
        System.out.println("SMS versandt im Debug-Modus an " + to + " mit Inhalt '" + message + "'.");
    }
}
