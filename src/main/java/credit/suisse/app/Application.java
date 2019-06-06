package credit.suisse.app;

import credit.suisse.api.EventController;

public class Application {
	
	EventController controller = new EventController();
	
	public EventController getController() {
		return controller;
	}

}
