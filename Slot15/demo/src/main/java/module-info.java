module fu.de200319.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens fu.de200319.demo to javafx.fxml;
    opens com.fu.ch08.b4 to javafx.fxml;
    exports fu.de200319.demo;
    exports com.fu.ch08.b1;
    exports com.fu.ch08.b2;
    exports com.fu.ch08.b3;
    exports com.fu.ch08.b4;
    exports com.fu.ch08.common;
}