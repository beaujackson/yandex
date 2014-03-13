/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.profilers;

import java.util.HashMap;

public class UserProfile {

    private static HashMap<String, UserProfile> profiles = new HashMap<>();

    public String userId;
    public long sessionCount = 0;
    public long searchCount = 0;
    public HashMap<Integer, Long> searchesByDay = new HashMap<>();
    public HashMap<String, Long> searchTermCount = new HashMap<>();

    public static UserProfile getProfile(String key) {
        if (!profiles.containsKey(key)) {
            UserProfile profile = new UserProfile();
            profile.userId = key;
            profiles.put(key, profile);
        }

        return profiles.get(key);
    }

    public static void printProfiles(long threshold) {
        for (UserProfile profile : profiles.values()) {
            if (profile.searchCount > threshold) {
                profile.printProfile();
                System.out.println("-------------------------------------------------");
            }
        }
    }

    private UserProfile() {
        for (int i=0; i<=31; i++) {
            searchesByDay.put(i, 0L);
        }
    }

    public void incrementDaySearch(Integer day) {
        searchesByDay.put(day, searchesByDay.get(day) + 1);
    }

    public void incrementTermUse(String term) {
        if (searchTermCount.containsKey(term)) {
            searchTermCount.put(term, searchTermCount.get(term) + 1);
        } else {
            searchTermCount.put(term, 1L);
        }
    }

    public void printProfile() {
        System.out.println("UserID: " + userId + "; Sessions: " + sessionCount + "; Searches: " + searchCount);

        System.out.println("Searches by day:");
        for (int i=0; i<=31; i++) {
            System.out.println("Day: " + i + "; Count: " + searchesByDay.get(i));
        }

        System.out.println("Top search terms:");
        for (String key : searchTermCount.keySet()) {
            if (searchTermCount.get(key) > 5) {
                System.out.println(key + ": " + searchTermCount.get(key));
            }
        }
    }
}
