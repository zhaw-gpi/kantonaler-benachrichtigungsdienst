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
 * instanzieren.
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
