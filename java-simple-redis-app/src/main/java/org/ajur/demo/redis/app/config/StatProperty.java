package org.ajur.demo.redis.app.config;

public class StatProperty implements Comparable<StatProperty> {

    private  String name;
    private  StatPropertyType type;
    private  String description;
    private  int pos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatPropertyType getType() {
        return type;
    }

    public void setType(StatPropertyType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public int compareTo(StatProperty prop) {
        return this.pos - prop.getPos();
    }

    @Override
    public String toString() {
        return "StatProperty{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
