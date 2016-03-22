package cz.muni.fi.pv168.winetasting.backend;

import java.time.ZonedDateTime;

/**
 * Created by bencikpeter on 15.03.16.
 */
public class WineTastingSession {

    private Long ID;
    private String place;
    private ZonedDateTime dateTime;

    public WineTastingSession() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
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
