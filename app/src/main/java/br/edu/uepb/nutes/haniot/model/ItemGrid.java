package br.edu.uepb.nutes.haniot.model;

public class ItemGrid {

    private int icon;
    private String description;
    private String name;

    public ItemGrid() {
    }

    public ItemGrid(int icon, String description, String name) {
        this.icon = icon;
        this.description = description;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ItemGrid{" +
                "icon=" + icon +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
