# Kantonaler Benachrichtigungsdienst (kantonaler-benachrichtigungsdienst)

> Autoren der Dokumentation: Björn Scheppler

> Dokumentation letztmals aktualisiert: 4.10.2019

Dieses Projekt simuliert den Kantonalen Benachrichtigungsdienst, welcher unter anderem für die [Umzugsplattform](https://github.com/zhaw-gpi/eumzug-plattform-2018) benötigt wird.

## (Technische) Komponenten des Benachrichtigungsdienstes
1. **Spring Boot Starter** beinhaltend:
    1. Spring Boot-Standardkonfiguration mit Tomcat als Applikations- und Webserver
    2. Main-Methode in KantonalerBenachrichtigungsDienstApplication-Klasse
2. **Camunda External Task Client**:
    1. Java External Task Client API-Dependency
    2. CamundaExternalTaskClient-Klasse, welche sich bei der Process Engine registriert und das Topic Notification abonniert. Hier wurde stark vereinfacht, denn in der Realität würde man sich wohl eher bei einem kantonsweiten Nachrichten-Anbieter registrieren (z.B. über JMS implementiert) oder wenigstens einer kantonsweiten Process Engine-Instanz, die nicht bloss für die Umzugsplattform benötigt wird.
    3. CamundaExternalTaskClientHandler-Klasse, welche vom External Task Client gefetchte Tasks abarbeitet und der Process Engine als erledigt mitteilt. Das eigentliche Abarbeiten geschieht über die NotificationTaskHandler-Klasse
    4. In application.properties ist die URL der Process Engine angegeben
3. **NotificationTaskHandler**: Enthält die eigentliche Business-Logik, also das Verarbeiten der Notification-Aufgabe, wobei lediglich geprüft wird, ob diese Aufgabe korrekt konfiguriert ist und dann für jeden Notification-Kanal eine eigene Handler-Klasse aufgerufen wird (siehe nächste Punkte)
4. **E-Mail-Kanal**:
    1. Spring Boot Mail Starter-Abhängigkeit für den eigentlichen Versand von Mails per SMTP
    2. EmailNotificationHandler-Klasse, welche die Business-Logik enthält, um E-Mail-Mitteilungsaufgaben abzuarbeiten
    3. EmailService-Klasse, welche die Spring Boot Mail-Komponente konfiguriert und ihr den Versand von Mails übergibt
    4. In application.properties respektive Umgebungsvariablen sind dabei alle Angaben für die SMTP-Konfiguration enthalten
5. **SMS-Kanal**:
    1. Twilio Java API Helper-Abhängigkeit für den Versand von SMS
    2. SmsNotificationHandler-Klasse, welche die Business-Logik enthält, um SMS-Mitteilungsaufgaben abzuarbeiten
    3. SmsService-Klasse, welche SMS über die Twilio API versendet (abgeleitet aus https://www.baeldung.com/java-sms-twilio)
    4. In application.properties respektive Umgebungsvariablen sind dabei alle Angaben für Twilio enthalten
6. "Sinnvolle" **Grundkonfiguration** in application.properties für Tomcat

## Erforderliche Schritte für das Testen der Applikation
### Voraussetzungen
1. **Laufende eUmzugsplattform**: Denn sonst gibt es ja nichts zu tun.
2. **SMTP-Angaben**: Wie in den Kommentaren im application.properties-File beschrieben, sind verschiedenste Angaben als Umgebungsvariablen zu setzen (in VS Code z.B. in launch.json in der env-Eigenschaft).
3. **Twilio-Angaben**: Dasselbe gilt für Twilio, wofür man überhaupt erst einen Account erstellen muss, wie es in https://www.baeldung.com/java-sms-twilio beschrieben ist. Auch muss dort die Telefonnummer erfasst sein, an welche man die Nachrichten senden will. Achtung: Der Trial-Account ist in der Schweiz de facto nur für einen Monat gültig, da eine Nummer pro Monat 8 USD kostet und man nur 16 USD Guthaben hat, das Senden der SMS aber auch 0.07 USD kostet.

### Deployment
1. **Erstmalig** oder bei Problemen ein `mvn clean install` durchführen
2. Bei Änderungen am POM-File oder bei **(Neu)kompilierungsbedarf** genügt ein `mvn install`
3. Für den **Start** ist ein `java -jar .\target\NAME DES JAR-FILES.jar` erforderlich. Dabei wird Tomcat gestartet mit den Eigenschaften (application.properties) und die verschiedenen Beans erstellt und konfiguriert.
4. Das **Beenden** geschieht mit **CTRL+C**

## Nutzung (Testing) der Applikation
1. Damit man den Client in Aktion sieht, muss mindestens eine Aufgabe vom Topic "Notification" zu erledigen sein, also in der Umzugsplattform eine der Aktivitäten 'xyz mitteilen' als Instanz existieren.
2. Je nach den Einstellungen mail.debug und sms.debug sollte nach einiger Verzögerung nun einerseits eine SMS auf dem eigenen Smartphone eintreffen als auch eine E-Mail im Posteingang der Mail-Adresse, welche in Kontaktangaben eingestellt ist (oder in overrideReceiver). Oder aber die Nachrichten werden nur in der Konsole erscheinen.