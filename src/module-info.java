module compute {
	exports View;

	requires java.sql;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	
	opens View to javafx.graphics, javafx.fxml;
}