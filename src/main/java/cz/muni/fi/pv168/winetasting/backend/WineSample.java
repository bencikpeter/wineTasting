package cz.muni.fi.pv168.winetasting.backend;

/**
 * Created by bencikpeter on 15.03.16.
 */
public class WineSample {

    private Integer id;
    private String vintnerFirstName;
    private String vintnerLastName;
    private String variety;
    private WineColor color;
    private WineCharacter character;
    private int year;

    public WineSample(Integer id, String vintnerFirstName, String vintnerLastName, String variety, WineColor color, WineCharacter character, int year) {
        this.id = id;
        this.vintnerFirstName = vintnerFirstName;
        this.vintnerLastName = vintnerLastName;
        this.variety = variety;
        this.color = color;
        this.character = character;
        this.year = year;
    }

    public WineSample() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVintnerFirstName() {
        return vintnerFirstName;
    }

    public void setVintnerFirstName(String vintnerFirstName) {
        this.vintnerFirstName = vintnerFirstName;
    }

    public String getVintnerLastName() {
        return vintnerLastName;
    }

    public void setVintnerLastName(String vintnerLastName) {
        this.vintnerLastName = vintnerLastName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public WineColor getColor() {
        return color;
    }

    public void setColor(WineColor color) {
        this.color = color;
    }

    public WineCharacter getCharacter() {
        return character;
    }

    public void setCharacter(WineCharacter character) {
        this.character = character;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
