package cz.muni.fi.pv168.winetasting.backend;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bencikpeter on 15.03.16.
 */

public interface WineTastingDAO {

    public void createSession(WineTastingSession session);

    public void updateSession(WineTastingSession session);

    public void deeleteSession(WineTastingSession session);

    public WineTastingSession findSessionByDate(ZonedDateTime date); //maybe other type would be appropriate

    public List<WineTastingSession> findAllSessions();
}