package ch.zhaw.gpi.benachrichtigungsdienst.smsnotification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sendet SMS über die Twilio API
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
    
    /**
     * Methode, um eine SMS zu senden
     * 
     * @param to            Empfänger (z.B. +41760000000)
     * @param message       Nachrichteninhalt
     * @throws Exception
     */
    public void sendSms(String to, String message) throws Exception {
        // Prüfen, ob SMS nur in Konsole ausgegeben werden soll
        if(debugSms){
            debugSms(to, message);
        } else {
            // Falls in der Umgebungsvariable smsOverrideNumber nicht der Wert 0 steht (sondern eine richtige Telefonnummer)
            if(!smsOverrideNumber.equals("0")){
                // ... dann die Empfänger-Nummer überschreiben mit dieser Nummer
                to = smsOverrideNumber;
            }
            
            // Eine neue Nachricht versenden über den Twilio Message-Creator
            // Die Strings müssen in PhoneNumber-Objekte übersetzt werden
            // Die create-Methode gibt den Auftrag an Twilio, um die SMS-Nachricht
            // zu senden. Nicht implementiert ist das Auswerten des Status, was
            // synchron (kann lange dauern) oder asynchron (benötigt Webhooks)
            // erfolgen könnte
            Message smsMessage = Message
                    .creator(
                            new PhoneNumber(to),
                            new PhoneNumber(smsSender),
                            message
                    ).create();
        }
    }
    
    // Sms-Versand als Fake durch Ausgabe in die Konsole für Testzwecke
    private void debugSms(String to, String message) {
        System.out.println("SMS versandt im Debug-Modus an " + to + " mit Inhalt '" + message + "'.");
    }
}
