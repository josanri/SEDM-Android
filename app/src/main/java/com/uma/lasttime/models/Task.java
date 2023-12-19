package com.uma.lasttime.models;

import java.util.Objects;

public class Task {
    private Integer id;
    private String title;
    private String description;
    private String lastDate;

    public Task(Integer id, String title, String description, String lastDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lastDate = lastDate;
    }

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        sb.append(lastDate);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
