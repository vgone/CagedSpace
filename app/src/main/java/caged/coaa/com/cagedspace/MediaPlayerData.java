package caged.coaa.com.cagedspace;

import android.media.MediaPlayer;

/**
 * Created by tarun on 10/21/15.
 */
public class MediaPlayerData {

    // false commit for master
    MediaPlayer mediaPlayer;
    String url;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
