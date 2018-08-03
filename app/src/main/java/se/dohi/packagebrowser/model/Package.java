package se.dohi.packagebrowser.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sam22 on 9/28/15.
 */
public class Package {

    private String name;
    private Set<String> languages;

    public Package(String name) {
        this.name = name;
        languages = new HashSet<>();
    }

    public void addLanguage(String lang) {
        languages.add(lang);
    }

    public String getName() {
        return name;
    }

    public Set<String> getLanguages() {
        return languages;
    }

}
