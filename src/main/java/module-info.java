module sample.javafxmusicui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens sample.javafxmusicui to javafx.fxml;
    opens sample.javafxmusicui.model to javafx.base;
    exports sample.javafxmusicui;
}