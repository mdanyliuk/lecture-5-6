package task2;

import java.time.Instant;
import java.util.Objects;

public class Clazz {
    private String stringProperty;
    @Property(name="numberProperty")
    private int myNumber;
    @Property(format="dd.MM.yyyy HH:mm")
    private Instant timeProperty;

    public Clazz() {
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "stringProperty='" + stringProperty + '\'' +
                ", myNumber=" + myNumber +
                ", timeProperty=" + timeProperty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clazz clazz = (Clazz) o;
        return myNumber == clazz.myNumber && Objects.equals(stringProperty, clazz.stringProperty) && Objects.equals(timeProperty, clazz.timeProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringProperty, myNumber, timeProperty);
    }
}