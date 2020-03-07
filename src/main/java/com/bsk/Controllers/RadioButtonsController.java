package com.bsk.Controllers;

import com.bsk.Models.BlockCipherState;
import com.bsk.Models.BlockCipherTypes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
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
    private final BlockCipherState blockCipherState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setToggleGroup();
        blockCipherState.setCurrentBlockCipherState(BlockCipherTypes.valueOf(ECBRadio.getText()));
        addListenerToRadioButtons();
    }

    private void setToggleGroup() {
        toggleGroup = new ToggleGroup();
        ECBRadio.setToggleGroup(toggleGroup);
        CBCRadio.setToggleGroup(toggleGroup);
        CFBRadio.setToggleGroup(toggleGroup);
        OFBRadio.setToggleGroup(toggleGroup);
    }

    private void addListenerToRadioButtons() {
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton clickedRadioButton = (RadioButton)newValue.getToggleGroup().getSelectedToggle();
            System.out.println(clickedRadioButton.getText());
            blockCipherState.setCurrentBlockCipherState(BlockCipherTypes.valueOf(clickedRadioButton.getText()));
        });
    }
}
