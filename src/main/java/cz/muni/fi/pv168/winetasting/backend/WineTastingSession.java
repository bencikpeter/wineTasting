package cz.muni.fi.pv168.winetasting.backend;

import java.time.LocalDate;

/**
 * Created by bencikpeter on 15.03.16.
 */
public class WineTastingSession {

    private Long ID;
    private String place;
    private LocalDate date;

    public WineTastingSession() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
