package com.patatedouce.openmotor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import com.fazecast.jSerialComm.*;
import javafx.stage.Window;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;


public class MainController {

    private SerialConnection serialConnection = SerialConnection.getInstance();

    @FXML private ComboBox<String> portList;
    @FXML private TextField com_status;

    public static final Path JSON_PATH = Paths.get(System.getProperty("user.home"), "openmotor", "save.json");

    @FXML
    public void initialize() {
        for (SerialPort port : SerialPort.getCommPorts()) {
            portList.getItems().add(port.getSystemPortName());
        }

        ensureJsonFileExists();
    }

    private static void ensureJsonFileExists() {
        try {
            // Crée le dossier s'il n'existe pas
            Files.createDirectories(JSON_PATH.getParent());

            // Crée le fichier avec un contenu vide s'il n'existe pas
            if (!Files.exists(JSON_PATH)) {
                Files.writeString(JSON_PATH, "{\"chaines\": []}", StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void SerialOpenBtn() {
        String selectedPort = portList.getValue();
        if(selectedPort == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le port n'est pas valide. Merci d'en sélectionner un existant.");
            alert.show();
            return;
        }
        if(SerialConnection.getInstance().isConnected()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Ouverture du port refusée car une connexion est déjà en cours.");
            alert.show();
            return;
        }
        if (serialConnection.openConnection(selectedPort, 9600)) {
            com_status.setText("OUI");
            com_status.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            SerialConnection.getInstance().addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    SerialPort port = event.getSerialPort();
                    byte[] buffer = new byte[port.bytesAvailable()];
                    int numRead = port.readBytes(buffer, buffer.length);

                    String receivedData = new String(buffer, 0, numRead);
                    System.out.println("Données reçues : " + receivedData);
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Connexion au port impossible.");
            alert.show();
        }
    }

    @FXML
    private void SerialCloseBtn() {
        String selectedPort = portList.getValue();
        if (SerialConnection.getInstance().isConnected()) {
            SerialConnection.getInstance().closeConnection();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Fermeture du port impossible car aucune connexion active.");
            alert.show();
        }
        com_status.setText("NON");
        com_status.setStyle("-fx-background-color: red; -fx-text-fill: white;");
    }


    private Stage secondaryStage = null;
    @FXML
    private void openSimu() {
        if (SerialConnection.getInstance().isConnected()) {
            try {
                if(secondaryStage != null) {
                    secondaryStage.toFront();
                    return;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("chaines-view.fxml"));
                secondaryStage = new Stage();
                Scene scene = new Scene(loader.load());
                secondaryStage.setTitle("OpenMotor Simulation");
                secondaryStage.setResizable(false);
                secondaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.JPG")));
                secondaryStage.setOnCloseRequest(event -> secondaryStage = null);
                secondaryStage.setScene(scene);
                secondaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La connexion avec le MCU n'est pas établie.");
            alert.show();

            return;
        }
    }
}