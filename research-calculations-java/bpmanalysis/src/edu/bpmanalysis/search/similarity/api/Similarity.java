package edu.bpmanalysis.search.similarity.api;

import edu.bpmanalysis.description.tools.Model;

public interface Similarity {

    double compare(Model first, Model second);
}
