package cz.muni.fi.pv168.winetasting.backend;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bencikpeter on 15.03.16.
 */

public interface WineTastingDAO {

    void createSession(WineTastingSession session);

    void updateSession(WineTastingSession session);

    void deleteSession(WineTastingSession session);

    WineTastingSession findSessionById (Long id);

    List<WineTastingSession> findSessionByDate(LocalDate date) throws SQLException; //maybe other type would be appropriate

    List<WineTastingSession> findAllSessions();
}
