package entities;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import utils.CSVUtils;

public class Conference {

    final static Set<String> USUALLY_VISIT_EMAILS = new HashSet<>();

    @CsvBindByPosition(position = 0)
    private String id;

    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByPosition(position = 2)
    @CsvDate(value = CSVUtils.DATE_FRMT)
    private Date startDate;

    @CsvBindByPosition(position = 3)
    @CsvDate(value = CSVUtils.DATE_FRMT)
    private Date endDate;

    @CsvBindByPosition(position = 4)
    private String email;

    @CsvBindByPosition(position = 5)
    private int duration;

    @CsvBindByPosition(position = 6)
    private int countGuests;

    private final List<Guest> guests = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEmail() {
        return email;
    }

    public int getDuration() {
        return duration;
    }

    public int getCountGuests() {
        return countGuests;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void addGuests(List<Guest> guests) {
        this.guests.addAll(guests);
    }

    public double getPercentGuests(List<Participant> participants) {
        return (guests.size() * 100.0 / participants.size());
    }

    public double getPercentUsualGuests() {
        return (guests.size() * 100.0 / USUALLY_VISIT_EMAILS.size());
    }

    public static void addUsuallyVisits(Collection<String> emails) {
        USUALLY_VISIT_EMAILS.addAll(emails);
    }

    public static boolean hasAtLEastOneVisit(String email) {
        return USUALLY_VISIT_EMAILS.contains(email);
    }

    @Override
    public String toString() {
        return "Conference{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", email='" + email + '\'' +
            ", duration=" + duration +
            ", countGuests=" + countGuests +
            '}';
    }
}