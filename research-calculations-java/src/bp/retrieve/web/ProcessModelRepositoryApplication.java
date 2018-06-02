package bp.retrieve.web;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.staticFiles;

import bp.retrieve.collection.ProcessModelRepository;
import bp.retrieve.web.controller.ProcessModelRepositoryController;
import bp.retrieve.web.service.ProcessModelRepositoryService;

/**
 * Provides an entry point to the process model repository application.
 * 
 * @author Andrii Kopp
 */
public class ProcessModelRepositoryApplication {

	/**
	 * Initialize the repository application.
	 * 
	 * @param processModelRepository
	 *            - the instance of process model repository.
	 */
	public static void init(ProcessModelRepository processModelRepository) {
		ProcessModelRepositoryService.setProcessModelRepository(processModelRepository);

		staticFiles.location("/repository");

		path("/", () -> {
            get("/", ProcessModelRepositoryController.sharedModels);
			get("/retrieveByKeywords", ProcessModelRepositoryController.retrieveByKeywords);
			get("/retrieveSimilar", ProcessModelRepositoryController.retrieveSimilar);
			get("/retrieveById", ProcessModelRepositoryController.retrieveById);
		});

		System.out.println("Business Process Model Repository Application started...");
	}
}
