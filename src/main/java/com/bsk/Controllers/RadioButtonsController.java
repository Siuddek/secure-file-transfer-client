package com.bsk.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class RadioButtonsController implements Initializable {
    @FXML
    RadioButton ECBRadio;
    @FXML
    RadioButton CBCRadio;
    @FXML
    RadioButton CFBRadio;
    @FXML
    RadioButton OFBRadio;
    @FXML
    ToggleGroup toggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleGroup = new ToggleGroup();
        ECBRadio.setToggleGroup(toggleGroup);
        CBCRadio.setToggleGroup(toggleGroup);
        CFBRadio.setToggleGroup(toggleGroup);
        OFBRadio.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton chk = (RadioButton)newValue.getToggleGroup().getSelectedToggle();
                System.out.println(chk.getText());
            }
        });
    }
}
