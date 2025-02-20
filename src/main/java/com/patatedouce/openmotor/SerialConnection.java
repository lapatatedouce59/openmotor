package com.patatedouce.openmotor;

import com.fazecast.jSerialComm.*;

import java.util.ArrayList;
import java.util.List;

public class SerialConnection {
    private static SerialConnection instance;
    private SerialPort serialPort;
    private final List<SerialPortDataListener> listeners = new ArrayList<>();

    private SerialConnection() {}

    public static SerialConnection getInstance() {
        if (instance == null) {
            instance = new SerialConnection();
        }
        return instance;
    }

    public boolean openConnection(String portName, int baudRate) {
        if (serialPort != null && serialPort.isOpen()) {
            return true;  // Déjà ouvert
        }

        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);

        boolean success = serialPort.openPort();
        if (success) {
            attachMultiListener();  // Attacher le gestionnaire de listeners
        }
        return success;
    }

    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    public void closeConnection() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    public void addDataListener(SerialPortDataListener listener) {
        listeners.add(listener);
    }

    public void removeDataListener(SerialPortDataListener listener) {
        listeners.remove(listener);
    }

    private void attachMultiListener() {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    for (SerialPortDataListener listener : listeners) {
                        listener.serialEvent(event);
                    }
                }
            }
        });
    }
}
