package edu.kopp.phd.express.governance;

import edu.kopp.phd.express.metamodel.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class GovernanceLog {
    private List<Model> landscape;

    public GovernanceLog() {
        this.landscape = new ArrayList<>();
    }

    public abstract void analyze();

    public List<Model> getLandscape() {
        return landscape;
    }

    public void setLandscape(List<Model> landscape) {
        this.landscape = landscape;
    }
}
