package com.musicplayer.model.filter;

import com.musicplayer.model.Track;

public interface TrackFilterStrategy {
    boolean matches(Track track);
}