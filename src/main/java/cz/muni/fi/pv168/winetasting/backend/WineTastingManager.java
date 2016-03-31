package cz.muni.fi.pv168.winetasting.backend;

import java.util.List;

/**
 * Created by bencikpeter on 15.03.16.
 */
public interface WineTastingManager {
    /**
     * Divides all wines assigned to specific session into several groups (commissions)
     * these wines need to be in these groups for output at least, maybe in the database too
     * @param session session, which will do the tasting and rating of wines
     */
    WinesLayout generateWinesLayout(WineTastingSession session); // maybe should not return void, need to reconsider

    /**
     * assign wine sample to specific session
     * this information needs to be stored in database table representing sample-session relation
     * @param session wine sample assigned to session
     * @param sample session, to which wine sample will be asigned
     */
    void assignWineToSession(WineTastingSession session, WineSample sample);

    /**
     * Specific session will rate specific wine sample with points
     * this information needs to be stored in database table representing sample-session relation
     * @param sample wine sample to be rated
     * @param session session which will rate wine sample
     * @param rating rating of sample, number between 0 to 100
     */
    void assignRatingToWine(WineSample sample, WineTastingSession session, int rating);

    /**
     * Finds session to which the wine sample is assigned
     * @param sample specific wine sample
     * @return session to which the wine sample is assigned
     */
    WineTastingSession findSessionWithWine(WineSample sample);

    /**
     * Finds all wines assigned to the session
     * @param session specific session
     * @return list of all wine samples assigned to the session
     */
    List<WineSample> findAllWinesInSession(WineTastingSession session);
}
