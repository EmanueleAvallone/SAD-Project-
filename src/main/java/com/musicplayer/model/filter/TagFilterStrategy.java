package com.musicplayer.model.filter;

import com.musicplayer.model.Tag;
import com.musicplayer.model.Track;
import java.util.Set;

public class TagFilterStrategy implements TrackFilterStrategy {
    private final Set<Tag> requiredTags;

    public TagFilterStrategy(Set<Tag> requiredTags) {
        this.requiredTags = requiredTags;
    }

    @Override
    public boolean matches(Track track) {
        if (requiredTags == null || requiredTags.isEmpty()) {
            return true;
        }
        return track.getTags().containsAll(requiredTags);
    }
}