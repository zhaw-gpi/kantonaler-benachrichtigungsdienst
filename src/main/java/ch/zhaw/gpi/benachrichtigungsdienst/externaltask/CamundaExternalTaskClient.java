package ch.zhaw.gpi.benachrichtigungsdienst.externaltask;

import javax.annotation.PostConstruct;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.exception.ExternalTaskClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Camunda External Task Client, welcher das Topic Notification abonniert
 *
 * @author scep
 */
@Component
public class CamundaExternalTaskClient {

    // Variable für ein CamundaExternalTaskClient-Objekt
    private ExternalTaskClient externalTaskClient;

    // Variable aus application.properties, welche die URL der Process Engine-REST API enthält
    @Value("${camunda.rest.url}")
    private String baseUrl;

    // Variable für das automatisch verdrahtete Handler-Objekt
    @Autowired
    private CamundaExternalTaskClientHandler notificationHandler;

    // Initiieren des ExternalTaskClients. Dank @PostConstruct (und Bezeichnung
    // init) geschieht dies erst nach dem Autowiring und Ausführen von @Value
    @PostConstruct
    private void init() {
        try {
            /**
             * 1. Eine neue External Task Client-Instanz erstellen und
             * konfigurieren mit dem ExternalTaskClientBuilder
             * https://github.com/camunda/camunda-external-task-client-java/blob/1.0.0/client/src/main/java/org/camunda/bpm/client/ExternalTaskClientBuilder.java
             */
            externalTaskClient = ExternalTaskClient
                    .create() // Den ExternalTaskClientBuilder initiieren
                    .baseUrl(baseUrl) // URL der REST API der Process Engine
                    .workerId("KantonalerBenachrichtigungsdienst") // Eindeutiger Name, damit die Process Engine "weiss", wer einen bestimmten Task gelocked hat
                    .maxTasks(10) // Wie viele Tasks sollen maximal auf einen "Schlag" (Batch) gefetched werden
                    .lockDuration(2000) // Long Polling für 2 Sekunden (2000 Millisekunden) -> siehe https://docs.camunda.org/manual/latest/user-guide/process-engine/external-tasks/#long-polling-to-fetch-and-lock-external-tasks
                    .build(); // Die External Task Client-Instanz mit den vorhergehenden Angaben erstellen

            /**
             * 2. Der External Task Client kann sich für mehrere Topics
             * registrieren, in diesem Beispiel nur für das "Notification"-Topic.
             * Registrieren bedeutet hierbei, dass der Client in regelmässigen
             * Abständen (siehe lockDuration oben) bei der Process Engine nach
             * neuen Tasks für den Topic anfrägt. Falls welche vorhanden sind,
             * werden diese bezogen (Fetch) und blockiert (lock), so dass kein
             * anderer Client die Aufgaben auch bearbeiten könnte (=>
             * Konflikte). Nun werden sie von einem External Task Handler (die
             * eigentliche Business Logik) abgearbeitet und der Process Engine
             * als erledigt (complete) gemeldet. Die Registration umfasst die
             * folgenden Schritte:
             */
            /**
             * a) Für jedes Topic ist eine External Task Handler-Implementation
             * anzugeben, welche wie hier gezeigt als eigene Klasse
             * CamundaExternalTaskClientHandler implementiert sein kann und eine Instanz davon
             * hier erstellt wird. Oder wer sich mit Lambda-Expressions
             * auskennt, kann dies auch kürzer haben wie z.B. gezeigt in
             * https://docs.camunda.org/get-started/quick-start/service-task/#implement-an-external-task-worker
             * https://github.com/camunda/camunda-external-task-client-java/blob/1.0.0/client/src/main/java/org/camunda/bpm/client/topic/TopicSubscriptionBuilder.java
             */
            /**
             * b) Das Registrieren geschieht über einen Fluent Builder wie schon
             * in Schritt 1. Er umfasst: - Festlegen des Topics (subscribe) - Die
             * Handler-Klasse (handler), welche gefetchte Tasks abarbeitet - Das
             * eigentliche Registrieren (open)
             */
            externalTaskClient
                    .subscribe("Notification")
                    .handler(notificationHandler)
                    .open();
        } catch (ExternalTaskClientException etce) {
            System.err.println("Fehler beim Erstellen des External Task Clients. Details: " + etce.getLocalizedMessage());
        }
    }
}
