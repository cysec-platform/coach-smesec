package eu.smesec.library.skills;

import eu.smesec.bridge.md.MetadataUtils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Represents the endurance aspect of the Skills
 */
public class Endurance extends Skill {
    private TreeMap<Long, Integer> entries;

    public Endurance(int maxValue) {
        super(maxValue);
        this.entries = new TreeMap<>();
    }

    /**
     * Behaves differently from the base class implementation as
     * it tracks the gained value with the epoch.
     */
    @Override
    public void add(int value) {
        Long now = LocalDate.now().toEpochDay();
        if (entries.containsKey(now)) {
            addEntry(now, entries.get(now) + 1);
        } else {
            addEntry(now, 1);
        }
    }

    /**
     * Restores the endurance from an string array stored within an Mvalue object.
     * Processes each key-value pair and removes leading/trailing whitespaces.
     * Attention: This method performs a destructive operation on the hashmap. It
     * clears all entries to establish a consitent state between memory and file.
     *
     * @param endurance The mvalue holding an array string
     */
    public void restore(MetadataUtils.SimpleMvalue endurance) {
        // Only continue if endurance mvalue exists
        // Upon first saving of skills, missing object will be created.
        if(endurance != null) {
            // Security precaution: make sure its cleared before reading new values
            entries.clear();
            String arrayString = endurance.getValue();
            // Strip leading [ and trailing ] from string
            if(!arrayString.isEmpty()) {
                String[] values = arrayString.substring(1, arrayString.length() - 1).split(",");
                Stream.of(values)
                        .forEach(entry -> {
                            String[] keyValuePair = entry.split("=");
                            addEntry(Long.valueOf(keyValuePair[0].trim()), Integer.valueOf(keyValuePair[1].trim()));
                        });
            }
        }
    }

    /**
     * Helper method to add entries. Capsules behaviour shared between add() and restore()
     *
     * @param key   the key for the entry as LocalDate.toEpoch
     * @param value the value for the entry
     */
    private void addEntry(Long key, Integer value) {
        entries.put(key, value);
    }

    /**
     * Updates the value with the most recent values as defined in maxValue.
     */
    public int refresh() {
        int value = 0;
        long min = LocalDate.now().minusDays(maxValue).toEpochDay();
        Iterator<Map.Entry<Long, Integer>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            if (entry.getKey() < min) {
                it.remove();
            } else {
                value += entry.getValue();
            }
        }
        return value;
    }

    @Override
    public String toString() {
        return entries.entrySet().toString();
    }

    /**
     * Override behaviour of subclass. get should return the accumulated values of
     * all entries limited by the provided maxValue (maximum number of days back).
     * @return the value of all entries at lest >= 0
     */
    @Override
    public int get() {
        return Math.max(refresh(), 0);
    }
}
