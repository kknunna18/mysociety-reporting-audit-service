package com.mysociety.reporting;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.mysociety.reporting")
class ArchitectureTest {
    @ArchTest
    static final ArchRule controllers_do_not_access_repositories =
            noClasses().that().resideInAPackage("..audit..").and().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository");
}
