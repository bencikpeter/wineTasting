package cz.muni.fi.pv168.winetasting.backend;

import java.time.LocalDate;

/**
 * Created by bencikpeter on 15.03.16.
 */
public class WineTastingSession {

    private Long ID = null;
    private String place = null;
    private LocalDate date = null;

    public WineTastingSession() {
        //throw new UnsupportedOperationException("Not implemented yet");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WineTastingSession that = (WineTastingSession) o;

        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        if (place != null ? !place.equals(that.place) : that.place != null) return false;
        return !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
