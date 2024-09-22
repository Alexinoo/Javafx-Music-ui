module sample.javafxmusicui {
    requires javafx.controls;
    requires javafx.fxml;


    opens sample.javafxmusicui to javafx.fxml;
    exports sample.javafxmusicui;
}