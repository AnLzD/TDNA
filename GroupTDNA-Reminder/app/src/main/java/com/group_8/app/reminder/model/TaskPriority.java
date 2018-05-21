package com.group_8.app.reminder.model;

public class TaskPriority {
    private int id;
    private String name;
    private String color;
    private String[] colors;

    public TaskPriority(int id, String name, String color){
        this.id = id;
        this.name = name;
        this.color = color;
        if(color != null && color.length() == 15){
            colors = color.split("-");
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColorAlpha(){
        if(colors != null && colors.length == 4){
            return Integer.parseInt(colors[0]);
        }
        return -1;
    }
    public int getColorGreen(){
        if(colors != null && colors.length == 4){
            return Integer.parseInt(colors[1]);
        }
        return -1;
    }
    public int getColorRed(){
        if(colors != null && colors.length == 4){
            return Integer.parseInt(colors[2]);
        }
        return -1;
    }
    public int getColorBlue(){
        if(colors != null && colors.length == 4){
            return Integer.parseInt(colors[3]);
        }
        return -1;
    }

}
