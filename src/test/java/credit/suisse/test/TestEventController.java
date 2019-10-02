package credit.suisse.test;

import credit.suisse.api.EventController;
import credit.suisse.api.EventControllerImpl;
import credit.suisse.pojo.EventResult;
import credit.suisse.service.EventService;
import credit.suisse.service.EventServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TestEventController {

	private EventController controller = new EventControllerImpl();
	private EventService service = new EventServiceImpl();
	private ClassLoader classLoader = getClass().getClassLoader();
	private static final String inputFileSuccess = "input_file.json";
	private static final String inputFileMissingFinishedLog = "input_file_missing_finished_log.json";
	private static final String inputFileCheckAlert = "input_file_check_alert.json";

	/**
	 * Remove all the data from table.
	 */
	@BeforeEach
	void setUp() {
		service.cleanTable();
	}

	/**
	 * Remove all the data from table.
	 */
	@AfterEach
	void tearDown() {
		service.cleanTable();
	}

	private String loadResourceInputFile(String inputFilePath) {
		try {
			return Paths.get(Objects.requireNonNull(classLoader.getResource(inputFilePath)).toURI()).toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Test method for {@link EventControllerImpl#processEvents(java.lang.String)}.
	 */
	@Test
	void testProcessEvents() {
		String inputFile = loadResourceInputFile(inputFileSuccess);
		controller.processEvents(inputFile);
		
		List<EventResult> results = controller.showEventResults();
		
		assertSame(6, results.size());
	}

	/**
	 * Test method for {@link EventControllerImpl#processEvents(java.lang.String)}.
	 * One of the tasks id doesn't have the FINISHED log in json data.
	 */
	@Test
	void testProcessEventsMissingFinishedLog() {
		String inputFile = loadResourceInputFile(inputFileMissingFinishedLog);
		controller.processEvents(inputFile);
		
		List<EventResult> results = controller.showEventResults();
		
		assertSame(5, results.size());
	}

	/**
	 * Test method for {@link EventControllerImpl#processEvents(java.lang.String)}.
	 * Check if alert is true for first object (timestamp difference is 5)
	 * Check if alert is false for second object (timestamp difference is 3)
	 */
	@Test
	void testProcessEventsCheckAlert() {
		String inputFile = loadResourceInputFile(inputFileCheckAlert);
		controller.processEvents(inputFile);
		
		List<EventResult> results = controller.showEventResults();
		assertTrue(results.get(0).getAlert());
		assertFalse(results.get(1).getAlert());
		
		assertSame(2, results.size());
	}

}
