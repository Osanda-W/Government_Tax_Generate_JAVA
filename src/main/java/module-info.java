module org.example.lol {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.iit to javafx.fxml;
    exports org.iit;
}