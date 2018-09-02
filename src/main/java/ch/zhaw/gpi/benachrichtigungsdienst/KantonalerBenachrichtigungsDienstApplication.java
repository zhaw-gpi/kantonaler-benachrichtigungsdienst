package ch.zhaw.gpi.benachrichtigungsdienst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hauptklasse für die Spring Boot-Applikation, welche diese mit allen
 * Abhängigkeiten ausführt. Basis ist das Spring-Framework.
 *
 * Zusammengefasst werden dabei dank @SpringBootApplication und
 * SpringApplication.run folgende Schritte durchlaufen: 1. Tomcat initialisieren
 * 2. Alle Beans, Components und Services automatisch konfigurieren und
 * instanzieren. 3. Das bedeutet konkret: 1. Der TwitterService steht zur
 * Verfügung und initial wird die Methode ausgeführt, welche in der
 * Output-Konsole die letzten 20 Tweets ausgibt 2. Der External Task Client ist
 * initiiert und hat sich bei der Process Engine registriert 3. Der
 * SendTweetHandler kann die gefetchten Tasks abarbeiten
 *
 * PS: @ComponentScan stellt sicher, dass Spring Boot alle mit
 * @Component/@Service annotierten Klassen im ClassPath erkennt.
 */
@SpringBootApplication
@ComponentScan
public class KantonalerBenachrichtigungsDienstApplication {

    /**
     * Startet die Spring Boot-Applikation
     * 
     * @param args 
     */
    public static void main(String[] args) {
        SpringApplication.run(KantonalerBenachrichtigungsDienstApplication.class, args);
    }
}
