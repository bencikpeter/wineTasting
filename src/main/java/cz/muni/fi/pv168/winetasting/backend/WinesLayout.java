package cz.muni.fi.pv168.winetasting.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by lukas on 3/31/16.
 */
public class WinesLayout {
    final static Logger log = LoggerFactory.getLogger(WinesLayout.class);
    private Map<Integer, List<WineSample>> layout;

    private List<WineSample> redList(List<WineSample> wines) {
        log.debug("Generating redlists");
        List<WineSample> red = new ArrayList<>();
        for (WineSample wine : wines) {
            if (wine.getColor() == WineColor.RED) {
                red.add(wine);
            }
        }
        return red;
    }

    private void generateLists(List<WineSample> wines) {
        log.debug("Generating lists");
        int number = (wines.size() % 50) == 0 ? wines.size() / 50 : wines.size() / 50 + 1;
        int groupSize = wines.size() / number;

        int wineCounter = 0;
        int layoutSize = layout.size();
        for (int i = layoutSize; i < number + layoutSize; ++i) {
            List<WineSample> l = new ArrayList<>();
            for (int j = 0; j < groupSize; ++j) {
                l.add(wines.get(wineCounter));
                ++wineCounter;
            }
            if (i == number + layoutSize - 1) {
                for (; wineCounter < wines.size(); ++wineCounter) {
                    l.add(wines.get(wineCounter));
                    ++wineCounter;
                }
                layout.put(i, l);
            }
        }
    }

    public WinesLayout(List<WineSample> wines) {
        log.debug("Generating wines layout");
        List red = redList(wines);
        List others = new ArrayList<>(wines);
        others.removeAll(red);

        CharCompare compare = new CharCompare();
        red.sort(compare);
        others.sort(compare);
        generateLists(others);
        generateLists(red);
    }

    public void outPutCommission(int i) {
        log.debug("Outputing commission number: {}",i);
        if (i >= layout.size() || i < 0) {
            return;
        }
        int size = layout.get(i).size();
        for (int j = 0; j < size; ++j) {
            //TODO output to GUI somehow
        }
    }

    class CharCompare implements Comparator<WineSample> {

        @Override
        public int compare(WineSample o1, WineSample o2) {
            return o1.getCharacter().compareTo(o2.getCharacter());
        }
    }
}


