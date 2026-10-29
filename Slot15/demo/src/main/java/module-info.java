module fu.de200319.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens fu.de200319.demo to javafx.fxml;
    exports fu.de200319.demo;
    exports com.fu.ch08.b1;
}