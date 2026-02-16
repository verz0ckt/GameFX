module gamefx {
    requires javafx.controls;
    requires javafx.fxml;


    opens gamefx to javafx.fxml;
    exports gamefx;
    exports gamefx.rendering;
    opens gamefx.rendering to javafx.fxml;
    exports gamefx.objects;
    opens gamefx.objects to javafx.fxml;
    exports gamefx.util;
    opens gamefx.util to javafx.fxml;
}