import java.util.*;


/**
 * Author: Shuai Yuan
 * Date: 03/27/2022
 *
 */
class SongCacheImpl implements SongCache {

    private HashMap<String, Integer> map = new HashMap<>();
    //Record number of plays for a song
    public synchronized void recordSongPlays(String songId, int numPlays) {
        map.put(songId, map.getOrDefault(songId, 0) + numPlays);
    }
    //Fetch the number of plays for a song. Return the number of plays, or -1 if the song ID is unknown
    public int getPlaysForSong(String songId) {
        return map.getOrDefault(songId, -1);
    }
    //Return the top N songs played, in descending order of number of plays
    public List<String> getTopNSongsPlayed(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        PriorityQueue<String> topN = new PriorityQueue<String>(n, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Integer.compare(map.get(s1), map.get(s2));
            }
        });
        for (String key : map.keySet()) {
            if (topN.size() < n) {
                topN.add(key);
            } else if (map.get(topN.peek()) < map.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        return (List) Arrays.asList(topN.toArray());
    }


    public static void main(String[] args){
        SongCache cache = new SongCacheImpl();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);

        System.out.println(cache.getPlaysForSong("ID-1")); // is(4)
        System.out.println(cache.getPlaysForSong("ID-9"));//is(-1)

        System.out.println(cache.getTopNSongsPlayed(2)); // id3 id1
        System.out.println(cache.getTopNSongsPlayed(0));// empty;
    }


}
