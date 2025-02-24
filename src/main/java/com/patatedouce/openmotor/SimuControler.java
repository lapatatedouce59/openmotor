package com.patatedouce.openmotor;

import com.almasb.fxgl.core.collection.Array;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.util.Callback;
import java.util.concurrent.atomic.AtomicReference;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import com.fazecast.jSerialComm.*;

public class SimuControler {
    //le type de la tableview est une map de valeurs
    @FXML private TableView<Map<String, Object>> tableChaineList;
    //le type des colonnes est d'abord d'où vient l'information (de la Map qui lie une clé String avec un Object (String, int...) ) et quel sera le type de l'info finale (String)
    @FXML private TableColumn<Map<String, Object>, String> indexColumn;
    @FXML private TableColumn<Map<String, Object>, String> nomColumn;
    @FXML private TableColumn<Map<String, Object>, String> etapeColumn;
    @FXML private TableColumn<Map<String, Object>, String> vmaxColumn;
    @FXML private TableColumn<Map<String, Object>, String> startColumn;
    //observable list est une liste que javafx pourra surveiller pour mettre à jour automatiquement l'interface
    private final ObservableList<Map<String, Object>> dataTable = FXCollections.observableArrayList();

    @FXML private TableView<Map<String, Object>> tableStep;
    @FXML private TableColumn<Map<String, Object>, String> indexStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> labelStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> vdStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> vfStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> pwmdStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> pwmfStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> clkdivdStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> clkdivfStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> DCdStepColumn;
    @FXML private TableColumn<Map<String, Object>, String> DCfStepColumn;
    private final ObservableList<Map<String, Object>> dataTableStep = FXCollections.observableArrayList();

    @FXML private TableView<Map<String, Object>> tableSimu;
    @FXML private TableColumn<Map<String, Object>, String> indexSimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> clkSimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> pwmSimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> divSimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> dutySimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> nameSimuColumn;
    @FXML private TableColumn<Map<String, Object>, String> labelSimuColumn;
    private final ObservableList<Map<String, Object>> dataTableSimu = FXCollections.observableArrayList();


    private SerialConnection serialConnection = SerialConnection.getInstance();

    public JSONObject getGlobalJson(){
        try {
            String content = new String(Files.readAllBytes(Paths.get(MainController.JSON_PATH.toString())));
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject;
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier JSON : " + e.getMessage());
            return null;
        }
    }

    public boolean setGlobalJson(JSONObject jsonObject){
        try {
            Files.write(Paths.get(MainController.JSON_PATH.toString()), jsonObject.toString(4).getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Erreur d'écriture du fichier : " + e.getMessage());
            return false;
        }
    }


    private void LoadChaineTable(){
        indexColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id").toString()));
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("nom").toString()));
        etapeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("etapes").toString()));
        vmaxColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("vmax").toString()));
        startColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("duty_start").toString()));

        // Charger les données JSON
        loadJsonDataChaine(MainController.JSON_PATH.toString());

        // Ajouter les données au TableView
        tableChaineList.setItems(dataTable);
    }

    private void loadJsonDataChaine(String filePath) {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        for (int i = 0; i < chaines.length(); i++) {
            JSONObject chaineJson = chaines.getJSONObject(i);
            Map<String, Object> row = new HashMap<>();
            row.put("id", i);
            row.put("nom", chaineJson.getString("name"));
            row.put("etapes", chaineJson.getJSONArray("steps").length());
            row.put("vmax", chaineJson.getInt("vmax"));
            row.put("duty_start", chaineJson.getInt("duty_start"));
            dataTable.add(row);
        }
    }

    private void LoadChaineStep(){
        indexStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id").toString()));
        labelStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("label").toString()));
        vdStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("vd").toString()));
        vfStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("vf").toString()));
        pwmdStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("pwmd").toString()));
        pwmfStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("pwmf").toString()));
        clkdivdStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("clkdivd").toString()));
        clkdivfStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("clkdivf").toString()));
        DCdStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("dcd").toString()));
        DCfStepColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("dcf").toString()));

        // Charger les données JSON
        loadJsonDataStep(MainController.JSON_PATH.toString());

        // Ajouter les données au TableView
        tableStep.setItems(dataTableStep);
    }

    private void loadJsonDataStep(String filePath) {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");
        JSONArray steps = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText())).getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++) {
            JSONObject stepsObj = steps.getJSONObject(i);
            Map<String, Object> row = new HashMap<>();
            row.put("id", i);
            row.put("label", stepsObj.getString("label"));
            row.put("vd", stepsObj.getFloat("vfrom"));
            row.put("vf", stepsObj.getFloat("vto"));
            row.put("pwmd", stepsObj.getInt("pmwfrom"));
            row.put("pwmf", stepsObj.getInt("pmwto"));
            row.put("clkdivd", stepsObj.getFloat("clkdivfrom"));
            row.put("clkdivf", stepsObj.getFloat("clkdivto"));
            //row.put("div", stepsObj.getFloat("duty_div"));
            row.put("dcd", stepsObj.getFloat("dcfrom"));
            row.put("dcf", stepsObj.getFloat("dcto"));
            dataTableStep.add(row);
        }
    }

    private void LoadSimuTable(){
        indexSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id").toString()));
        clkSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("clk").toString()));
        pwmSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("pwm").toString()));
        divSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("div").toString()));
        dutySimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("duty").toString()));
        nameSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("name").toString()));
        labelSimuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("label").toString()));

        // Ajouter les données au TableView
        tableSimu.setItems(dataTableSimu);
    }

    @FXML private TextField modify_chaine_index_input;
    @FXML private TextField delete_chaine_index_input;
    @FXML private TextField param_chaine_vmax;
    @FXML private TextField param_chaine_duty_start;
    @FXML private TextField param_chaine_name;

    @FXML private RadioButton param_chaine_duty_start_radio_off;
    @FXML private RadioButton param_chaine_duty_start_radio_on;

    @FXML private RadioButton chain_prog_radios_new;
    @FXML private RadioButton chain_prog_radios_mod;

    @FXML private void ShowChaineDetails() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if(Objects.equals(modify_chaine_index_input.getText(), "") || Integer.parseInt(modify_chaine_index_input.getText())>chaines.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index valide. Impossible d'afficher les détails.");
            alert.show();
        } else {
            JSONObject activeChaine = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText()));
            param_chaine_name.setText(activeChaine.getString("name"));
            param_chaine_vmax.setText(activeChaine.get("vmax").toString());
            param_chaine_duty_start.setText(activeChaine.get("duty_start").toString());

            if(activeChaine.getInt("duty_start")>0){
                param_chaine_duty_start_radio_on.setSelected(true);
            } else {
                param_chaine_duty_start_radio_off.setSelected(true);
            }

            dataTableStep.clear();
            LoadChaineStep();
        }
    }

    @FXML private void SaveChaineDetails() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if((Objects.equals(modify_chaine_index_input.getText(), "") || Integer.parseInt(modify_chaine_index_input.getText())>chaines.length()-1) && chain_prog_radios_mod.isSelected()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index valide. Impossible d'enregistrer les modifications.");
            alert.show();
        } else {
            if(chain_prog_radios_mod.isSelected()){
                JSONObject activeChaine = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText()));
                activeChaine.put("name",param_chaine_name.getText());
                activeChaine.put("vmax",Integer.parseInt(param_chaine_vmax.getText()));
                activeChaine.put("duty_start",Integer.parseInt(param_chaine_duty_start.getText()));
            } else {
                JSONObject activeChaine = new JSONObject();
                activeChaine.put("name",param_chaine_name.getText());
                activeChaine.put("vmax",Integer.parseInt(param_chaine_vmax.getText()));
                activeChaine.put("duty_start",Integer.parseInt(param_chaine_duty_start.getText()));
                activeChaine.put("steps",new JSONArray());
                chaines.put(activeChaine);
            }
            if(!setGlobalJson(jsonObject)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de l'enregistrement.");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validé");
                alert.setHeaderText(null);
                alert.setContentText("L'enregistrement s'est bien déroulé.");
                alert.show();
                dataTable.clear();
                LoadChaineTable();
            }
        }
    }

    @FXML private void DeleteChaine() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if((Objects.equals(delete_chaine_index_input.getText(), "") || Integer.parseInt(delete_chaine_index_input.getText())>chaines.length()-1)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index valide. Impossible de supprimer l'élément.");
            alert.show();
        } else {
            chaines.remove(Integer.parseInt(delete_chaine_index_input.getText()));
            if(!setGlobalJson(jsonObject)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de la suppression.");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validé");
                alert.setHeaderText(null);
                alert.setContentText("La suppression s'est bien déroulée.");
                alert.show();
                dataTable.clear();
                LoadChaineTable();
            }
        }
    }







    @FXML private TextField modify_step_index_input;
    @FXML private TextField delete_step_index_input;

    @FXML private TextField param_step_label;
    @FXML private TextField param_step_vd;
    @FXML private TextField param_step_vf;
    @FXML private TextField param_step_div;
    @FXML private TextField param_step_clkdivd;
    @FXML private TextField param_step_clkdivf;
    @FXML private TextField param_step_pwmd;
    @FXML private TextField param_step_pwmf;
    @FXML private TextField param_step_dcd;
    @FXML private TextField param_step_dcf;
    @FXML private TextField param_step_auto_dcd;
    @FXML private TextField param_step_auto_dcf;

    @FXML private RadioButton step_prog_radios_new;
    @FXML private RadioButton step_prog_radios_mod;

    @FXML private void ShowStepDetails() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if(Objects.equals(modify_chaine_index_input.getText(), "") || Integer.parseInt(modify_chaine_index_input.getText())>chaines.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index de chaine valide. Impossible d'afficher les détails d'étapes.");
            alert.show();
            return;
        }

        JSONObject chaine = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText()));
        JSONArray steps = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText())).getJSONArray("steps");

        if(Objects.equals(modify_step_index_input.getText(), "") || Integer.parseInt(modify_step_index_input.getText())>steps.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index d'étape valide. Impossible d'afficher les détails.");
            alert.show();
        } else {
            JSONObject step = steps.getJSONObject(Integer.parseInt(modify_step_index_input.getText()));
            param_step_label.setText(step.getString("label"));
            param_step_vd.setText(step.get("vfrom").toString());
            param_step_vf.setText(step.get("vto").toString());
            param_step_div.setText(step.get("duty_div").toString());
            param_step_clkdivd.setText(step.get("clkdivfrom").toString());
            param_step_clkdivf.setText(step.get("clkdivto").toString());
            param_step_pwmd.setText(step.get("pmwfrom").toString());
            param_step_pwmf.setText(step.get("pmwto").toString());
            param_step_dcd.setText(step.get("dcfrom").toString());
            param_step_dcf.setText(step.get("dcto").toString());
            float auto_dcd = (Float.parseFloat(step.get("vfrom").toString())/chaine.getInt("vmax"))*100;
            float auto_dcf = (Float.parseFloat(step.get("vto").toString())/chaine.getInt("vmax"))*100;
            param_step_auto_dcd.setText(String.format("%.1f",auto_dcd));
            param_step_auto_dcf.setText(String.format("%.1f",auto_dcf));
        }
    }

    @FXML private void SaveStepDetails() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if((Objects.equals(modify_chaine_index_input.getText(), "") || Integer.parseInt(modify_chaine_index_input.getText())>chaines.length()-1) /*&& chain_prog_radios_mod.isSelected()*/){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index de chaine valide. Impossible d'enregistrer les modifications.");
            alert.show();
            return;
        } else if ((Objects.equals(modify_step_index_input.getText(), "") || Integer.parseInt(modify_step_index_input.getText())>chaines.length()-1) && step_prog_radios_mod.isSelected()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index d'étape valide. Impossible d'enregistrer les modifications.");
            alert.show();
            return;
        } else  {
            if(step_prog_radios_mod.isSelected()){
                JSONArray steps = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText())).getJSONArray("steps");
                JSONObject step = steps.getJSONObject(Integer.parseInt(modify_step_index_input.getText()));
                step.put("label",param_step_label.getText());
                step.put("vfrom",Float.parseFloat(param_step_vd.getText()));
                step.put("vto",Float.parseFloat(param_step_vf.getText()));
                step.put("duty_div",Float.parseFloat(param_step_div.getText()));
                step.put("pmwfrom",Integer.parseInt(param_step_pwmd.getText()));
                step.put("pmwto",Integer.parseInt(param_step_pwmf.getText()));
                step.put("clkdivfrom",Float.parseFloat(param_step_clkdivd.getText()));
                step.put("clkdivto",Float.parseFloat(param_step_clkdivf.getText()));
                step.put("dcfrom",Float.parseFloat(param_step_dcd.getText()));
                step.put("dcto",Float.parseFloat(param_step_dcf.getText()));
            } else {
                JSONArray steps = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText())).getJSONArray("steps");
                JSONObject step = new JSONObject();
                step.put("label",param_step_label.getText());
                step.put("vfrom",Float.parseFloat(param_step_vd.getText()));
                step.put("vto",Float.parseFloat(param_step_vf.getText()));
                step.put("duty_div",Float.parseFloat(param_step_div.getText()));
                step.put("pmwfrom",Integer.parseInt(param_step_pwmd.getText()));
                step.put("pmwto",Integer.parseInt(param_step_pwmf.getText()));
                step.put("clkdivfrom",Float.parseFloat(param_step_clkdivd.getText()));
                step.put("clkdivto",Float.parseFloat(param_step_clkdivf.getText()));
                step.put("dcfrom",Float.parseFloat(param_step_dcd.getText()));
                step.put("dcto",Float.parseFloat(param_step_dcf.getText()));
                steps.put(step);
            }
            if(!setGlobalJson(jsonObject)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de l'enregistrement.");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validé");
                alert.setHeaderText(null);
                alert.setContentText("L'enregistrement s'est bien déroulé.");
                alert.show();
                dataTable.clear();
                LoadChaineTable();
                dataTableStep.clear();
                LoadChaineStep();
            }
        }
    }

    @FXML private void DeleteStep() {
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if(Objects.equals(modify_chaine_index_input.getText(), "") || Integer.parseInt(modify_chaine_index_input.getText())>chaines.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index de chaine valide. Impossible de supprimer l'élément.");
            alert.show();
            return;
        }

        JSONArray steps = chaines.getJSONObject(Integer.parseInt(modify_chaine_index_input.getText())).getJSONArray("steps");

        if(Objects.equals(delete_step_index_input.getText(), "") || Integer.parseInt(delete_step_index_input.getText())>steps.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index d'étape valide. Impossible de supprimer l'élément.");
            alert.show();
        } else {
            steps.remove(Integer.parseInt(delete_step_index_input.getText()));
            if(!setGlobalJson(jsonObject)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de la suppression.");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validé");
                alert.setHeaderText(null);
                alert.setContentText("La suppression de l'étape s'est bien déroulée.");
                alert.show();
                dataTableStep.clear();
                LoadChaineStep();
                dataTable.clear();
                LoadChaineTable();
            }
        }
    }

    private Stage vitStage = null;
    @FXML void GoToVitParam(){
        try {
            if(vitStage != null) {
                vitStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("prog-vit-view.fxml"));
            vitStage = new Stage();
            Scene scene = new Scene(loader.load());
            vitStage.setTitle("OpenMotor Programmation vitesses");
            vitStage.setResizable(false);
            vitStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.JPG")));
            vitStage.setOnCloseRequest(event -> vitStage = null);
            vitStage.setScene(scene);
            vitStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    int INCREMENT_CALL = 0;

    @FXML private TextField simu_apply_index;
    @FXML private TextField simu_applied_index;
    @FXML private TextField status_simu;
    @FXML private TextField status_simu_defStep;
    @FXML private TextField status_simu_noStep;
    @FXML private TextField simu_wait_ms;
    @FXML private TextField simu_speed;

    @FXML private Slider duty_cycle_slider;

    private final AtomicReference<JSONObject> CurrentChaine = new AtomicReference<>(null);
    private boolean SimulationLoop = false;
    private Thread simulationThread;
    private volatile int waitSendLoop = 50;

    private long lastUpdateTimeJavaFX = 0;
    private final long minWaitJavaFXNano = 250_000_000; //le pas executer le runLater à chaque calculs

    @FXML void StartSimulation() {
        /*if(SimulationLoop){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Une simulation est déjà en cours.");
            alert.show();
            return;
        }*/
        JSONObject jsonObject = getGlobalJson();
        JSONArray chaines = jsonObject.getJSONArray("chaines");

        if(Objects.equals(simu_apply_index.getText(), "") || Integer.parseInt(simu_apply_index.getText())>chaines.length()-1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Vous n'avez pas renseigné d'index de chaine valide. Impossible d'appliquer la chaine.");
            alert.show();
        } else {
            JSONObject activeChaine = chaines.getJSONObject(Integer.parseInt(simu_apply_index.getText()));
            CurrentChaine.set(activeChaine);
            simu_applied_index.setText(simu_apply_index.getText());
            status_simu.setStyle("-fx-text-fill: green;");
            SimulationLoop=true;

            Task<Void> simulationTask = new Task<>() {
                @Override
                protected Void call() {
                    while (SimulationLoop) {

                        JSONObject CalcChaine = CurrentChaine.get();

                        JSONArray CalcStep = new JSONArray();

                        double CalcVitesse = (duty_cycle_slider.getValue()/100)*Integer.parseInt(CalcChaine.get("vmax").toString());

                        for (int i = 0; i < CalcChaine.getJSONArray("steps").length(); i++) {
                            JSONObject step = CalcChaine.getJSONArray("steps").getJSONObject(i);
                            if(CalcVitesse>=Integer.parseInt(step.get("vfrom").toString()) && CalcVitesse< Integer.parseInt(step.get("vto").toString())){
                                CalcStep.put(step);
                            }
                        }

                        boolean showNoStepAlarm;
                        boolean showStepDefAlarm;

                        showNoStepAlarm = CalcStep.isEmpty();
                        showStepDefAlarm = CalcStep.length() > 1;

                        double pwm_step;
                        double clk_step;
                        double fin_duty;
                        double div_step;
                        String step_lbl;

                        if(!showNoStepAlarm){
                            JSONObject CurrentStep = CalcStep.getJSONObject(0);

                            //int CalcVitInt = (int) Math.floor(CalcVitesse);
                            double MaxStepVit = CurrentStep.getInt("vto")-CurrentStep.getInt("vfrom");
                            double AdvStepVit = CalcVitesse-CurrentStep.getInt("vfrom");
                            double adv_vit_step = AdvStepVit/MaxStepVit;

                            pwm_step = ((CurrentStep.getInt("pmwto")-CurrentStep.getInt("pmwfrom"))*adv_vit_step)+CurrentStep.getInt("pmwfrom");
                            clk_step = ((CurrentStep.getInt("clkdivto")-CurrentStep.getInt("clkdivfrom"))*adv_vit_step)+CurrentStep.getInt("clkdivfrom");
                            double duty_step = ((CurrentStep.getInt("dcto")-CurrentStep.getInt("dcfrom"))*adv_vit_step)+CurrentStep.getInt("dcfrom");
                            double add_duty_first = (CalcChaine.getInt("duty_start")/65535.0);
                            double add_duty = add_duty_first*100;
                            fin_duty = Math.min(duty_step+add_duty,100.0);
                            div_step=Float.parseFloat(CurrentStep.get("duty_div").toString());
                            step_lbl=CurrentStep.getString("label");

                            System.out.println(INCREMENT_CALL);
                            serialConnection.sendData("PW"+String.format("%.0f",pwm_step)+"\n");
                            serialConnection.sendData("CK"+String.format("%.2f",clk_step)+"\n");
                            serialConnection.sendData("DC"+String.format("%.2f",fin_duty)+"\n");
                            serialConnection.sendData("DD"+String.format("%.2f",Float.parseFloat(CurrentStep.get("duty_div").toString()))+"\n");

                            INCREMENT_CALL++;
                        } else {
                            pwm_step=0;
                            clk_step=0;
                            fin_duty=0;
                            div_step=0;
                            step_lbl="Inconnu";
                        }



                        long now = System.nanoTime();
                        if(now - lastUpdateTimeJavaFX >= minWaitJavaFXNano){
                            lastUpdateTimeJavaFX = now;
                            // les mises à jour de l'interface doivent être faites sur le JavaFX Thread
                            Platform.runLater(() -> {
                                //maj interface
                                simu_speed.setText(String.format("%.2f", CalcVitesse));
                                if(showNoStepAlarm){
                                    status_simu_noStep.setStyle("-fx-text-fill: red;");
                                } else {
                                    status_simu_noStep.setStyle("-fx-text-fill: black;");
                                }
                                if(showStepDefAlarm){
                                    status_simu_defStep.setStyle("-fx-text-fill: red;");
                                } else {
                                    status_simu_defStep.setStyle("-fx-text-fill: black;");
                                }


                                dataTableSimu.clear();
                                Map<String, Object> row = new HashMap<>();
                                row.put("id", simu_applied_index.getText());
                                row.put("clk", String.format("%.2f",clk_step));
                                row.put("pwm", String.format("%.0f",pwm_step));
                                row.put("div", String.format("%.2f",div_step));
                                row.put("duty", String.format("%.2f",fin_duty));
                                row.put("name", CurrentChaine.get().getString("name"));
                                row.put("label", step_lbl);
                                dataTableSimu.add(row);
                                LoadSimuTable();
                            });
                        }




                        // pause pour éviter de surcharger le CPU
                        try {
                            Thread.sleep(waitSendLoop);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    return null;
                }
            };

            if(simulationThread == null){
                simulationThread = new Thread(simulationTask);
                simulationThread.setDaemon(true); // s'arrête quand l'application ferme
                simulationThread.start();
            }
        }
    }

    @FXML void StopSimulation() {
        if(!SimulationLoop){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Aucune chaine en cours.");
            alert.show();
        } else {
            CurrentChaine.set(null);
            simu_applied_index.setText("");
            status_simu.setStyle("-fx-text-fill: black;");
            SimulationLoop=false;
            serialConnection.sendData("PW500\n");
            serialConnection.sendData("CK0.0\n");
            serialConnection.sendData("DC0.0\n");
            serialConnection.sendData("DD1\n");
            if (simulationThread != null) {
                simulationThread.interrupt();
                simulationThread=null;
            }
        }
    }

    @FXML void SetWaitSimu() {
        if(Objects.equals(simu_wait_ms.getText(), "")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Aucune valeur n'a été renseigné.");
            alert.show();
            return;
        } else if (Integer.parseInt(simu_wait_ms.getText())<10){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("La valeur renseignée est trop petite (risque de surcharge).");
            alert.show();
            return;
        }
        waitSendLoop=Integer.parseInt(simu_wait_ms.getText());
    }



    @FXML
    public void initialize() {
        tableChaineList.setPlaceholder(new Label("Aucune chaine à afficher"));
        tableStep.setPlaceholder(new Label("Aucune étape à afficher"));
        LoadChaineTable();
    }
}
