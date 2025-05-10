package dev.cs3431.yelpapp;

public class BusinessAttributes {
    private final String id;
    private final String attribute;
    private final String attribute_value;

    public BusinessAttributes(String id, String attribute, String attribute_value) {
        this.id = id;
        this.attribute = attribute;
        this.attribute_value = attribute_value;
    }

    public String getId() {
        return id;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getAttribute_value() {
        return attribute_value;
    }
}
