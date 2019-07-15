package credit.suisse.app;

import credit.suisse.api.EventController;

public class Application {

	private EventController controller = new EventController();
	
	private EventController getController() {
		return controller;
	}

	public static void main(String[] args) {
		Application app = new Application();

		String argument = args[0];

		switch (argument) {
			case "create_table":
				app.getController().prepareTable();
				break;
			case "results":
				app.getController().getEventResults().forEach(System.out::println);
				break;
			case "clean_data":
				app.getController().cleanData();
				break;
			default:
				app.getController().processEvents(argument);
				break;
		}
	}

}
