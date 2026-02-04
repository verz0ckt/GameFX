module gamefx {
    requires javafx.controls;
    requires javafx.fxml;


    opens gamefx to javafx.fxml;
    exports gamefx;
}