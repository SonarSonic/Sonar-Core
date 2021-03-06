package sonar.core.translate;

public class Localisation {

    public boolean wasFound;
    public String original;
    public String toDisplay = "TRANSLATION ERROR";

    public Localisation(String original) {
        this.original = original;
    }

    /** gets the translation */
    public String t() {
        return toDisplay;
    }

    /** gets the original */
    public String o() {
        return original;
    }

    public String toString() {
        return t();
    }

    public class Tile extends Localisation {

        public Tile(String original) {
            super("tile." + original + ".name");
        }

    }

    public class Item extends Localisation {

        public Item(String original) {
            super("item." + original + ".name");
        }

    }
}