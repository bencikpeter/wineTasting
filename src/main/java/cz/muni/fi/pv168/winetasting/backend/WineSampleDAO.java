package cz.muni.fi.pv168.winetasting.backend;

import java.util.List;

/**
 * Created by bencikpeter on 15.03.16.
 */
public interface WineSampleDAO {
    /**
     * gets all wine samples from db
     * @return List<WineSample>
     */
    List<WineSample> findAllWineSamples();

    /**
     * gets all wine samples with specified variety from db
     * @return List<WineSample>
     * @param variety variety of wine
     */
    List<WineSample> findWineSamplesByVariety(String variety);

    /**
     * gets specific wine sample from db
     * sample is specified by ID
     * @param id id of wine sample
     * @return WineSample
     */
    WineSample findWineSampleById(Long id);

    /**
     * inserts wine sample into db
     * @param wineSample wine sample supposed to be inserted into db
     */
    void createWineSample(WineSample wineSample);

    /**
     * deletes wine sample from db
     * @param wineSample wine sample supposed to be deleted from db
     */
    void deleteWineSample(WineSample wineSample);

    /**
     * updates wine sample in db
     * @param wineSample wine sample supposed to be updated in db
     */
    void updateWineSample(WineSample wineSample);

    /**
     * gets all wines from database which are not assigned to any session
     * @return List<WineSample>
     */
    List<WineSample> findAllUnsessionedWines();


}
