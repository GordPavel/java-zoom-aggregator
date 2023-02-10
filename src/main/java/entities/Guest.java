package entities;

import com.opencsv.bean.CsvBindByPosition;

public class Guest {
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String email;

    @CsvBindByPosition(position = 2)
    private int duration;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", duration=" + duration +
                '}';
    }
}
