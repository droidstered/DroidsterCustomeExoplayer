package me.droidster.customeexoplayer.VideoUtil;

import com.google.android.exoplayer2.source.TrackGroup;

/**
 * Created by Jetbro-4 on 31-05-2017.
 */

public class TrackData {

    private TrackGroup trackGroup;
    private int  track_support_or_not;
    private int  groupIndex;
    private int  trackIndex;

    public TrackData(TrackGroup trackGroup, int track_support_or_not,int groupIndex,int trackIndex) {

        this.trackGroup = trackGroup;
        this.track_support_or_not = track_support_or_not;
        this.groupIndex = groupIndex;
        this.trackIndex = trackIndex;

    }

    public int getTrack_support_or_not() {
        return track_support_or_not;
    }

    public void setTrack_support_or_not(int track_support_or_not) {
        this.track_support_or_not = track_support_or_not;
    }

    public TrackGroup getTrackGroup() {
        return trackGroup;
    }

    public void setTrackGroup(TrackGroup trackGroup) {
        this.trackGroup = trackGroup;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }
}
