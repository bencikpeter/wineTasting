package cz.muni.fi.pv168.winetasting.backend;

import java.util.List;

/**
 * Created by bencikpeter on 15.03.16.
 */
public interface WineTastingManager {

    public void generateWinesLayout(WineTastingSession session);
    public void assignRatingToWine(WineSample sample);
    public void assignWineToSession(WineTastingSession session, WineSample sample);
    public WineTastingSession findSessionWithWine(WineSample sample);
    public List<WineSample> findAllWinesInSession(WineTastingSession session);
}
