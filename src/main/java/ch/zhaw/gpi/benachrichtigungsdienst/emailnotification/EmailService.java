package ch.zhaw.gpi.benachrichtigungsdienst.emailnotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sendet Mails über Spring Mail-Komponente basierend auf den entsprechenden
 * Application Properties für den SMTP-Server. Basierend auf:
 * https://docs.spring.io/spring/docs/5.0.8.RELEASE/spring-framework-reference/integration.html#mail
 * http://www.baeldung.com/spring-email
 * https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html
 *
 * @author scep
 */
@Service
public class EmailService {

    // Verdrahtet die Spring Java Mail-Sender-Klasse
    @Autowired
    public JavaMailSender javaMailSender;

    // Application.Properties-Eigenschaften in Variablen auslesen
    @Value("${mail.debug}")
    private Boolean debugMail;
    
    @Value("${mail.overrideReceiver}")
    private String mailOverrideReceiver;

    /**
     * Methode, um eine einfache Mail zu senden
     *
     * @param to Empfänger
     * @param subject Betreff
     * @param body Mail-Text
     * @param from Absender
     */
    public void sendSimpleMail(String to, String subject, String body, String from) throws MailException {
        // Prüfen, ob Mail nur in Konsole ausgegeben werden soll
        if (debugMail) {
            this.debugMail(to, subject, body);
        } else {
            // Instanziert eine neue SimpleMail-Nachricht
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            
            // Wenn fürs Testen die übergebene Mail-Adresse stets durch eine in einer Umgebungsvariable gesetzten Mail-Adresse ersetzt werden soll
            if(!mailOverrideReceiver.equals("-")){
                to = mailOverrideReceiver;
            }

            // Legt Empfänger, Betreff und Mail-Text fest
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setReplyTo(from);

            // Versucht, die Mail abzusenden. Klappt es nicht, wird der Fehler geworfen
            javaMailSender.send(simpleMailMessage);
        }
    }

    /**
     * Sendet das Mail nicht, sondern gibt die Angaben in die Konsole aus
     *
     * @param to Empfänger
     * @param subject Betreff
     * @param body Mail-Text
     */
    private void debugMail(String to, String subject, String body) {
        System.out.println("########### BEGIN MAIL ##########################");
        System.out.println("############################### Mail-Subjekt: " + subject);
        System.out.println("############################### Mail-Empfänger: " + to);
        System.out.println(body);
        System.out.println("########### END MAIL ############################");
    }
}
