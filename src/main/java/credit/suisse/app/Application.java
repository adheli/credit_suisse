package credit.suisse.app;

import credit.suisse.api.EventController;
import credit.suisse.api.EventControllerImpl;

public class Application {
	
	private EventController controller = new EventControllerImpl();
	
	private EventController getController() {
		return controller;
	}

	public static void main(String[] args) {
		Application app = new Application();

		String argument = args[0];

		switch (argument) {
			case "create_table":
				app.getController().prepareEnvironment();
				break;
			case "results":
				System.out.println(app.getController().showEventResults());
				break;
			case "clean_data":
				app.getController().cleanEnvironment();
				break;
			default:
				app.getController().processEvents(argument);
				break;
		}
	}

}
