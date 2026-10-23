/** The seven valid days. Anything else cannot be a Day. */
public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    /** Turns typed text like " monday " into MONDAY, or throws. 
     * @param text
     * @return matching day value
     * @throws IllegalArgumentException if text null or not valid*/
    public static Day fromText(String text) {
        if (text != null) {
            for (Day d : Day.values()) {
                if (d.name().equalsIgnoreCase(text.trim())) return d;
            }
        }
        throw new IllegalArgumentException(
            "Enter a valid day: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, or Saturday");
    }

    /** "MONDAY" becomes "Monday" for printing. */
    @Override public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}