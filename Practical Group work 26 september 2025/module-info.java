module NgoProjectTracking {
    requires java.sql;       // built-in JDBC API
    requires java.desktop;   // Swing
    opens gui to java.desktop; // allow Swing reflection for GUI
    exports gui;
    exports db;
}
