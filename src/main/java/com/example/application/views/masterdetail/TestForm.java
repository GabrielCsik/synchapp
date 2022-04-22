package com.example.application.views.masterdetail;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class TestForm extends FormLayout {

    private NumberField numberOfUsers = new NumberField("Number Of Users");
    private NumberField numberOfMiners = new NumberField("Number Of Miners");
    private NumberField hashDifficulty = new NumberField("Hash Difficulty");
    private NumberField duration = new NumberField("Duration (seconds)");

    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    public TestForm() {
        addClassName("test-form");
        add(numberOfUsers, numberOfMiners, hashDifficulty, duration, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(delete, close);
    }
}
