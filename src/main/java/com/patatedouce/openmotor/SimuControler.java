package com.patatedouce.openmotor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import com.fazecast.jSerialComm.*;
import com.patatedouce.openmotor.SerialConnection;

public class SimuControler {
    private SerialConnection serialConnection = SerialConnection.getInstance();
}
