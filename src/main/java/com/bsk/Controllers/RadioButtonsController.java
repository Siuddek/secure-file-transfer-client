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
    ToggleGroup defaultGroup;
    private final BlockCipherState blockCipherState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultGroup = new ToggleGroup();
        setDefaultGroup();
        setDefaultRadioButton();
        addListenerToRadioButtons();
    }

    private void setDefaultGroup() {
        ECBRadio.setToggleGroup(defaultGroup);
        CBCRadio.setToggleGroup(defaultGroup);
        CFBRadio.setToggleGroup(defaultGroup);
        OFBRadio.setToggleGroup(defaultGroup);
    }

    private void setDefaultRadioButton() {
        blockCipherState.setCurrentBlockCipherState(BlockCipherTypes.valueOf(ECBRadio.getText()));
    }

    private void addListenerToRadioButtons() {
        defaultGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton clickedRadioButton = (RadioButton)newValue.getToggleGroup().getSelectedToggle();
            System.out.println(clickedRadioButton.getText());
            blockCipherState.setCurrentBlockCipherState(BlockCipherTypes.valueOf(clickedRadioButton.getText()));
        });
    }
}
