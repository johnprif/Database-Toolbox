module myModule {
	exports View;

	requires java.sql;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	
	opens Model to javafx.base, javafx.fxml;
	opens View to javafx.graphics, javafx.fxml;
}