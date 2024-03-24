module gradeprediction {
    exports com.gradeprediction.client;
    exports com.gradeprediction.server;
    exports com.gradeprediction.common;

    requires java.rmi;
    requires javafx.base;
    requires javafx.controls;
    requires transitive javafx.graphics;
}
