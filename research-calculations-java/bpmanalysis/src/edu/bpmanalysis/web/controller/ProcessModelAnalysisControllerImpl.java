package edu.bpmanalysis.web.controller;

import com.google.gson.Gson;
import edu.bpmanalysis.analysis.ProcessModelAnalysisUtil;
import edu.bpmanalysis.analysis.bean.ProcessModelAnalysisBean;
import edu.bpmanalysis.description.tools.Model;
import edu.bpmanalysis.search.pattern.ProcessModelPatternMatchingStorage;
import edu.bpmanalysis.search.similarity.ProcessModelSimilaritySearchStorage;
import edu.bpmanalysis.web.api.DetailedRepositoryDashboard;
import edu.bpmanalysis.web.api.SummaryRepositoryDashboard;
import edu.bpmanalysis.web.controller.api.ProcessModelAnalysisController;
import edu.bpmanalysis.web.model.api.ProcessModelRepository;
import edu.bpmanalysis.web.model.api.UserRepository;
import edu.bpmanalysis.web.model.bean.ProcessModelBean;
import edu.bpmanalysis.web.model.bean.UserBean;
import org.apache.commons.codec.digest.DigestUtils;
import spark.Route;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

public class ProcessModelAnalysisControllerImpl implements ProcessModelAnalysisController {
    private ProcessModelRepository repository;
    private UserRepository userRepository;

    public Route models = (req, res) -> {
        List<ProcessModelBean> processModels = repository.getProcessModels();
        Collections.reverse(processModels);

        return new Gson().toJson(processModels);
    };

    public Route retrieve = (req, res) -> {
        String id = req.params(":id");
        ProcessModelBean processModelBean = repository.getProcessModel(id);
        return new Gson().toJson(processModelBean);
    };

    public Route store = (req, res) -> {
        String json = req.body();

        ProcessModelBean processModelBean = new Gson().fromJson(json, ProcessModelBean.class);
        processModelBean.setId(UUID.randomUUID().toString());
        processModelBean.setTimeStamp(new Timestamp(System.currentTimeMillis()).toString());

        repository.addProcessModel(processModelBean);

        return null;
    };

    public Route edit = (req, res) -> {
        String json = req.body();

        ProcessModelBean update = new Gson().fromJson(json, ProcessModelBean.class);

        ProcessModelBean processModelBean = repository.getProcessModel(update.getId());
        processModelBean.setName(update.getName());
        processModelBean.setLevel(update.getLevel());
        processModelBean.setDescription(update.getDescription());

        repository.updateProcessModel(processModelBean);

        return null;
    };

    public Route analyze = (req, res) -> {
        String id = req.params(":id");
        ProcessModelBean processModelBean = repository.getProcessModel(id);

        Model model = ProcessModelAnalysisUtil.transformToModel(processModelBean);
        ProcessModelAnalysisBean processModelAnalysisBean = ProcessModelAnalysisUtil.analyzeModel(model);

        return new Gson().toJson(processModelAnalysisBean);
    };

    public Route remove = (req, res) -> {
        String id = req.params(":id");

        repository.deleteProcessModel(id);

        res.redirect("/");
        return null;
    };

    public Route search = (req, res) -> {
        String model = req.params("model");
        String node = req.params("node");

        return new Gson().toJson(ProcessModelPatternMatchingStorage.search(model, node));
    };

    @Override
    public void init() {
        ProcessModelSimilaritySearchStorage.loadModels(repository);
        ProcessModelPatternMatchingStorage.loadModels(repository);

        staticFiles.location("/web");

        path("/", () -> {
            get("/models", models);
            get("/retrieve/:id", retrieve);
            post("/store", store);
            post("/edit", edit);
            get("/analyze/:id", analyze);
            get("/remove/:id", remove);
            get("/search/:model/:node", search);

            get("/api", (req, res) ->
                    new Gson().toJson(new SummaryRepositoryDashboard(repository)));
            get("/api/detailed", (req, res) ->
                    new Gson().toJson(new DetailedRepositoryDashboard(repository)));

            get("/login/:user/:password", (req, res) -> {
                String login = req.params("user");
                String password = req.params("password");

                UserBean userBean = userRepository.getUser(login);

                if (userBean != null &&
                        DigestUtils.md5Hex(password).toUpperCase().equals(userBean.getPassword())) {
                    return new Gson().toJson(userBean);
                }

                return "null";
            });

            get("/login/:user", (req, res) -> {
                String login = req.params("user");

                UserBean userBean = userRepository.getUser(login);

                if (userBean == null) {
                    return "null";
                }

                return new Gson().toJson(userBean);
            });
        });
    }

    @Override
    public void setRepository(ProcessModelRepository repository) {
        this.repository = repository;
    }

    @Override
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
