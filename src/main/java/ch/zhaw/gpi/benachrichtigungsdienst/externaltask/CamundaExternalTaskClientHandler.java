package ch.zhaw.gpi.benachrichtigungsdienst.externaltask;

import ch.zhaw.gpi.benachrichtigungsdienst.genericnotification.NotificationTaskHandler;
import java.util.HashMap;
import java.util.Objects;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Enthält die Business Logik, welche für vom External Task Client gefetchte
 * Tasks abarbeitet und der Process Engine als erledigt mitteilt. In diesem
 * Beispiel umfasst dies das Auslesen der Prozessvariable notificationTask, das
 * Übergeben dieser Variable an den Handler (NotificationTaskHandler) mit der
 * eigentlichen Business-Logik und schliesslich das Melden an die Process Engine
 * entweder eines erfolgreichen Abschlusses des Tasks oder eines Business-Fehlers
 *
 * @author scep
 */
@Component
public class CamundaExternalTaskClientHandler implements ExternalTaskHandler {
    
    // Instanzieren von Konstanten
    private static final String NOTIFICATION_PROCESS_VARIABLE = "notificationTask";
    private static final String DEFAULT_ERROR_MESSAGE = "Fehler beim Verarbeiten der Notification-Aufgabe";
    
    // Den Handler für die eigentliche Business-Logik verdrahten
    @Autowired
    private NotificationTaskHandler notificationTaskHandler;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        // Eine HashMap-Variable deklarieren
        HashMap<String, String> notificationTask;

        // NotificationTask-HashMap als generisches Objekt aus Prozessvariablen lesen
        Object notificationTaskObject = externalTask.getVariable(NOTIFICATION_PROCESS_VARIABLE);
        
        
        // Prüfen, ob Prozessvariable überhaupt existiert
        if(Objects.isNull(notificationTaskObject)){
            externalTaskService.handleFailure(externalTask, DEFAULT_ERROR_MESSAGE, "Prozessvariable " + NOTIFICATION_PROCESS_VARIABLE + " nicht verfügbar", 0, 1);
            return;
        }
        
        // Prüfen, ob das Objekt  eine HashMap ist
        if(notificationTaskObject instanceof HashMap) {
            // Falls ja, dieses der deklarierten HashMap-Variable zuweisen
            notificationTask = (HashMap<String, String>) notificationTaskObject;
        } else {
            // Ansonsten Fehler auslösen
            externalTaskService.handleFailure(externalTask, DEFAULT_ERROR_MESSAGE, "Keine HashMap in Prozessvariable " + NOTIFICATION_PROCESS_VARIABLE + " gefunden", 0, 1);
            return;
        }
        
        try {
            // Den NotificationTask an den Handler übergeben
            notificationTaskHandler.handleNotificationTask(notificationTask);
            
            // Falls alles erfolgreich war, den Task erledigen
            externalTaskService.complete(externalTask);
        } catch (Exception e) {
            // Bei einem Fehler soll dieser an die Process Engine zurück gemeldet werden (gibt einen Incident)
            externalTaskService.handleFailure(externalTask, DEFAULT_ERROR_MESSAGE, e.getMessage(), 0, 1);
        }
    }
}
