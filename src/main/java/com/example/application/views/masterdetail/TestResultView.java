package com.example.application.views.masterdetail;

import com.example.application.data.entity.SampleTest;
import com.example.application.data.service.SampleTestRepository;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Master-Detail")
@Route(value = "master-detail/:sampleTestID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class TestResultView extends Div implements BeforeEnterObserver {

    private final String SAMPLETEST_ID = "sampleTestID";
    private final String SAMPLETEST_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private Grid<SampleTest> grid = new Grid<>(SampleTest.class, false);

    private NumberField numberOfUsers;
    private NumberField numberOfMiners;
    private NumberField hashDifficulty;
    private NumberField duration;

    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete");

    private BeanValidationBinder<SampleTest> binder;

    private SampleTest sampleTest;

    private final SampleTestRepository sampleTestService;


    private final NumberField avgMiningPowerTF;
    private final NumberField numOfBlocksTF;
    private final NumberField numOfTransactionsTF;
    private final NumberField numOfBlocksPerSecTF;
    private final NumberField numOfTransactionsPerSecTF;
    private final NumberField numOfTransactionsPerBlockTF;
    private Div editorLayoutDiv;
    private FormLayout formLayout;

    @Autowired
    public TestResultView(SampleTestRepository sampleTestService) {

        this.sampleTestService = sampleTestService;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        Grid.Column<SampleTest> avgMiningPower = grid.addColumn("avgMiningPower").setAutoWidth(true);
        Grid.Column<SampleTest> numOfBlocks = grid.addColumn("numOfBlocks").setAutoWidth(true);
        Grid.Column<SampleTest> numOfTransactions = grid.addColumn("numOfTransactions").setAutoWidth(true);
        Grid.Column<SampleTest> numOfBlocksPerSec = grid.addColumn("numOfBlocksPerSec").setAutoWidth(true);
        Grid.Column<SampleTest> numOfTransactionsPerSec = grid.addColumn("numOfTransactionsPerSec").setAutoWidth(true);
        Grid.Column<SampleTest> numOfTransactionsPerBlock = grid.addColumn("numOfTransactionsPerBlock").setAutoWidth(true);
//        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
//                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
//                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
//                        important -> important.isImportant()
//                                ? "var(--lumo-primary-text-color)"
//                                : "var(--lumo-disabled-text-color)");

//        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);


//        grid.setItems(sampleTestService.findAll());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLETEST_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TestResultView.class);
            }
        });


        HeaderRow headerRow = grid.appendHeaderRow();

        avgMiningPowerTF = gridNumberFieldFilter(avgMiningPower.getKey(), headerRow);
        numOfBlocksTF = gridNumberFieldFilter(numOfBlocks.getKey(), headerRow);
        numOfTransactionsTF = gridNumberFieldFilter(numOfTransactions.getKey(), headerRow);
        numOfBlocksPerSecTF = gridNumberFieldFilter(numOfBlocksPerSec.getKey(), headerRow);
        numOfTransactionsPerSecTF = gridNumberFieldFilter(numOfTransactionsPerSec.getKey(), headerRow);
        numOfTransactionsPerBlockTF = gridNumberFieldFilter(numOfTransactionsPerBlock.getKey(), headerRow);

//        headerRow.getCell(avgM).setComponent(
//                createFilterHeader("Name", SampleTestFilter::setFullName));


        // Configure Form
        binder = new BeanValidationBinder<>(SampleTest.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e -> {

            if (this.sampleTest == null) {
                this.sampleTest = new SampleTest();
            }
//                binder.writeBean(this.sampleTest);

            sampleTestService.delete(this.sampleTest);
            clearForm();
            refreshGrid();
            Notification.show("Test deleted");
            UI.getCurrent().navigate(TestResultView.class);

        });

            closeEditor();

            grid.asSingleSelect().addValueChangeListener( e -> editTest(e.getValue()));
        Button setItemsSync = new Button("Fetch items sync", e -> setGridValuesSync());
        add(setItemsSync);
    }

    private void editTest(SampleTest sampleTest) {
        if (sampleTest == null){
            closeEditor();
        }else{
            editorLayoutDiv.setVisible(true);
        }

    }

    private void closeEditor() {
        editorLayoutDiv.setVisible(false);
    }

    private NumberField gridNumberFieldFilter(String columnKey, HeaderRow headerRow) {
        NumberField filter = new NumberField();
        filter.setValueChangeMode(ValueChangeMode.TIMEOUT);
        filter.addValueChangeListener(event -> this.onFilterChange());
        filter.setWidthFull();
        headerRow.getCell(grid.getColumnByKey(columnKey)).setComponent(filter);
        return filter;
    }

    private void onFilterChange() {
        ListDataProvider<SampleTest> listDataProvider = (ListDataProvider<SampleTest>) grid.getDataProvider();
        // Since this will be the only active filter, it needs to account for all values of my filter fields
        listDataProvider.setFilter(item -> {
            boolean isAvgMP = true;
            boolean isNumB = true;
            boolean isNumT = true;
            boolean isNumBps = true;
            boolean isNumTps = true;
            boolean isNumTpB = true;

            if (!avgMiningPowerTF.isEmpty()) {
                isAvgMP = item.getAvgMiningPower() == avgMiningPowerTF.getValue();
            }
            if (!numOfBlocksTF.isEmpty()) {
                isNumB = item.getNumOfBlocks() == numOfBlocksTF.getValue();
            }
            if (!numOfTransactionsTF.isEmpty()) {
                isNumT = item.getNumOfTransactions() == numOfTransactionsTF.getValue();
            }
            if (!numOfBlocksPerSecTF.isEmpty()) {
                isNumBps = item.getNumOfBlocksPerSec() == numOfBlocksPerSecTF.getValue();
            }
            if (!numOfTransactionsPerSecTF.isEmpty()) {
                isNumTps = item.getNumOfTransactionsPerSec() == numOfTransactionsPerSecTF.getValue();
            }
            if (!numOfTransactionsPerBlockTF.isEmpty()) {
                isNumTpB = item.getNumOfTransactionsPerBlock() == numOfTransactionsPerBlockTF.getValue();
            }

            return isAvgMP && isNumB && isNumT && isNumTpB && isNumBps && isNumTps;
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> samplePersonId = event.getRouteParameters().get(SAMPLETEST_ID);
        if (samplePersonId.isPresent()) {
            Optional<SampleTest> sampleTestfromBackend = sampleTestService.findById(samplePersonId.get());
            if (sampleTestfromBackend.isPresent()) {
                populateForm(sampleTestfromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested test was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(TestResultView.class);
            }
        }
    }


//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        if (attachEvent.isInitialAttach()) {
//
//
//            Button setItemsSync = new Button("Fetch items sync", e -> setGridValuesSync());
//            Button setItemsAsync = new Button("Fetch items async", e -> setGridValuesAsync());
////            Button setItemsReactive = new Button("Fetch items reactive", e -> setGridValuesReactive());
//
//            Button clear = new Button("Clear", e -> grid.setItems(Collections.emptyList()));
//            clear.addThemeVariants(ButtonVariant.LUMO_ERROR);
//
//            Button responsivenessButton = new Button("Test responsiveness",
//                    e -> Notification.show("Response"));
//            responsivenessButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//            add(grid, new HorizontalLayout(
//                    setItemsSync, setItemsAsync, setItemsReactive, clear, responsivenessButton));
//        }
//    }

    private void setGridValuesSync() {
        grid.setItems(sampleTestService.findAll());
    }

//    private void setGridValuesAsync() {
//        UI ui = getUI().get();
//        cityClient.findCitiesAsync(
//                cities -> ui.access(() -> grid.setItems(cities)));
//    }

    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.add(new H2("Test params"));
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        formLayout = new FormLayout();
        numberOfUsers = new NumberField("Number Of Users");
        numberOfMiners = new NumberField("Number Of Miners");
        hashDifficulty = new NumberField("Hash Difficulty");
        duration = new NumberField("Duration (seconds)");
        Component[] fields = new Component[]{numberOfUsers, numberOfMiners, hashDifficulty, duration};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);


        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(sampleTestService.findAll());

    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SampleTest value) {
        this.sampleTest = value;
        binder.readBean(this.sampleTest);
    }

//    private static Component createFilterHeader(String labelText,
//                                                Consumer<String> filterChangeConsumer) {
//        Label label = new Label(labelText);
//        label.getStyle().set("padding-top", "var(--lumo-space-m)")
//                .set("font-size", "var(--lumo-font-size-xs)");
//        TextField textField = new TextField();
//        textField.setValueChangeMode(ValueChangeMode.EAGER);
//        textField.setClearButtonVisible(true);
//        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
//        textField.setWidthFull();
//        textField.getStyle().set("max-width", "100%");
//        textField.addValueChangeListener(
//                e -> filterChangeConsumer.accept(e.getValue()));
//        VerticalLayout layout = new VerticalLayout(label, textField);
//        layout.getThemeList().clear();
//        layout.getThemeList().add("spacing-xs");
//
//        return layout;
//    }
}
