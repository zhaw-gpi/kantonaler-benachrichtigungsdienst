package ch.zhaw.gpi.benachrichtigungsdienst.genericnotification;

import ch.zhaw.gpi.benachrichtigungsdienst.emailnotification.EmailNotificationHandler;
import ch.zhaw.gpi.benachrichtigungsdienst.smsnotification.SmsNotificationHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author scep
 */
@Component
public class NotificationTaskHandler {

    @Autowired
    private EmailNotificationHandler emailNotificationHandler;

    @Autowired
    private SmsNotificationHandler smsNotificationHandler;

    public void handleNotificationTask(HashMap<String, String> notificationTask) throws Exception {
        // Prüfen, ob ein channels-Key vorhanden ist
        if (!notificationTask.containsKey("channels") || StringUtils.isEmpty(notificationTask.get("channels"))) {
            throw new Exception("Schlüssel 'channels' fehlt oder enthält keinen Wert.");
        }

        // channels als List auslesen: splits the string on a delimiter defined as: zero or more whitespace, a literal comma, zero or more whitespace which will place the words into the list and collapse any whitespace between the words and commas
        List<String> channels = Arrays.asList(notificationTask.get("channels").split("\\s*,\\s*"));

        // Jeden Eintrag durch den passenden ChannelHandler abarbeiten lassen
        for (String channel : channels) {
            switch (channel) {
                case "email":
                    try {
                        emailNotificationHandler.handleEmailNotification(notificationTask);
                    } catch (Exception e) {
                        throw new Exception("Fehler beim Verarbeiten einer EmailNotification: " + e.getMessage());
                    }
                    break;
                case "sms":
                    try {
                        smsNotificationHandler.handleSmsNotification(notificationTask);
                    } catch (Exception e) {
                        throw new Exception("Fehler beim Verarbeiten einer SMS-Notification: " + e.getMessage());
                    }
                    break;
                default:
                    throw new Exception("Nicht unterstützter 'Kanal-Typ' " + channel + " in channels-Schlüssel übergeben");
            }
        }
    }
}
