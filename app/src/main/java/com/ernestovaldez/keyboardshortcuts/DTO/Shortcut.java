package com.ernestovaldez.keyboardshortcuts.DTO;

import java.util.ArrayList;
import java.util.List;

public class Shortcut {

    private String name;
    private List<String> keys;
    private String description;

    public Shortcut() {
        keys = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Shortcut{" +
                "name='" + name + '\'' +
                ", keys=" + keys +
                ", description='" + description + '\'' +
                '}';
    }
}
