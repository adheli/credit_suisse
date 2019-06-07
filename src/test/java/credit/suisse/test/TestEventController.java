package credit.suisse.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import credit.suisse.api.EventController;
import credit.suisse.dao.EventResultService;
import credit.suisse.pojo.EventResult;

class TestEventController {

	private EventController controller = new EventController();
	private EventResultService service = new EventResultService();
	private ClassLoader classLoader = getClass().getClassLoader();
	private String inputFile = "";

	/**
	 * Remove all the data from table.
	 */
	@BeforeEach
	public void setUp() {
		service.cleanTable();
	}

	/**
	 * Remove all the data from table.
	 */
	@AfterEach
	public void tearDown() {
		service.cleanTable();
	}

	/**
	 * Test method for {@link credit.suisse.api.EventController#processEvents(java.lang.String)}.
	 */
	@Test
	public void testProcessEvents() {
		try {
			this.inputFile = Paths.get(classLoader.getResource("input_file.json").toURI()).toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.processEvents(this.inputFile);
		
		List<EventResult> results = controller.getEventResults();
		
		assertSame(6, results.size());
	}

	/**
	 * Test method for {@link credit.suisse.api.EventController#processEvents(java.lang.String)}.
	 * One of the tasks id doesn't have the FINISHED log in json data.
	 */
	@Test
	public void testProcessEventsMissingFinishedLog() {
		try {
			this.inputFile = Paths.get(classLoader.getResource("input_file_missing_finished_log.json").toURI()).toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.processEvents(this.inputFile);
		
		List<EventResult> results = controller.getEventResults();
		
		assertSame(5, results.size());
	}

	/**
	 * Test method for {@link credit.suisse.api.EventController#processEvents(java.lang.String)}.
	 * Check if alert is true for first object (timestamp difference is 5)
	 * Check if alert is false for second object (timestamp difference is 3)
	 */
	@Test
	public void testProcessEventsCheckAlert() {
		try {
			this.inputFile = Paths.get(classLoader.getResource("input_file_check_alert.json").toURI()).toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.processEvents(this.inputFile);
		
		List<EventResult> results = controller.getEventResults();
		assertTrue(results.get(0).getAlert());
		assertFalse(results.get(1).getAlert());
		
		assertSame(2, results.size());
	}

}
