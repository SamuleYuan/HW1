import java.util.List;

interface SongCache {

    /**
     * Record number of plays for a song.
     */
    void recordSongPlays(String songId, int
            numPlays);

    /**
     * Fetch the number of plays for a song.
     *
     * @return the number of plays, or -1 if the
     * song ID is unknown.
     */
    int getPlaysForSong(String songId);

    /**
     * Return the top N songs played, in descending
     * order of number of plays.
     */
    List<String> getTopNSongsPlayed(int n);
}

