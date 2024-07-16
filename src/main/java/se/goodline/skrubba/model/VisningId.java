package se.goodline.skrubba.model;

import java.io.Serializable;
import java.util.Objects;

public class VisningId implements Serializable {
    private int id;
    private int asp;

    // Default constructor
    public VisningId() {}

    // Constructor with parameters
    public VisningId(int id, int asp) {
        this.id = id;
        this.asp = asp;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsp() {
        return asp;
    }

    public void setAsp(int asp) {
        this.asp = asp;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisningId visningId = (VisningId) o;
        return id == visningId.id && asp == visningId.asp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asp);
    }
}
