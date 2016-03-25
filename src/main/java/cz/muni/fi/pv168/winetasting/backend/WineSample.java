package cz.muni.fi.pv168.winetasting.backend;

/**
 * Created by bencikpeter on 15.03.16.
 */
public class WineSample {

    private Long id = null;
    private String vintnerFirstName;
    private String vintnerLastName;
    private String variety;
    private WineColor color;
    private WineCharacter character;
    private int year;

    public static class Builder{
        private String vintnerFirstName;
        private String vintnerLastName;
        private String variety;
        private WineColor color;
        private WineCharacter character;
        private int year;

        public Builder(String variety) {
            this.variety = variety;
        }

        public Builder vintnerName(String vintnerLastName, String vintnerFirstName) {
            this.vintnerLastName = vintnerLastName;
            this.vintnerFirstName = vintnerFirstName;
            return this;
        }

        public Builder color(WineColor color){
            this.color = color;
            return this;
        }

        public Builder character(WineCharacter character) {
            this.character = character;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public WineSample build() {
            return new WineSample(this);
        }

    }

    private WineSample(Builder builder) {
        this.variety = builder.variety;
        this.vintnerFirstName = builder.vintnerFirstName;
        this.vintnerLastName = builder.vintnerLastName;
        this.color = builder.color;
        this.character = builder.character;
        this.year = builder.year;
    }

    public WineSample() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
