module com.patatedouce.openmotor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fazecast.jSerialComm;
    requires org.json;

    opens com.patatedouce.openmotor to javafx.fxml;
    exports com.patatedouce.openmotor;
}