module compute {
	exports compute;

	requires java.sql;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	
	opens compute to javafx.graphics, javafx.fxml;
}