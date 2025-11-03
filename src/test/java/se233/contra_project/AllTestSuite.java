package se233.contra_project;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "se233.contra_project.actors",
        "se233.contra_project.game",
        "se233.contra_project.ui"
})
public class AllTestSuite {

}
