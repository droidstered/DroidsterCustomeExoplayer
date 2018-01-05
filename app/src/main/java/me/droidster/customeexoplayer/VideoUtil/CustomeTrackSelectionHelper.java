/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.droidster.customeexoplayer.VideoUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.SelectionOverride;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;

import java.util.ArrayList;

import me.droidster.customeexoplayer.Adapter.CustomAdapter;
import me.droidster.customeexoplayer.MainActivity;
import me.droidster.customeexoplayer.R;


public class CustomeTrackSelectionHelper implements
        DialogInterface.OnClickListener {

    private static final TrackSelection.Factory FIXED_FACTORY = new FixedTrackSelection.Factory();
    private static final TrackSelection.Factory RANDOM_FACTORY = new RandomTrackSelection.Factory();

    private final MappingTrackSelector selector;
    private final TrackSelection.Factory adaptiveTrackSelectionFactory;

    private MappedTrackInfo trackInfo;
    private int rendererIndex;
    private TrackGroupArray trackGroups;
    private boolean[] trackGroupsAdaptive;
    private boolean isDisabled;
    private SelectionOverride override;
    int passPosition = -1;

    CustomAdapter customAdapter;
    ListView listView;
    RelativeLayout rel_defult;
    ImageView img_defult;
    ArrayList<TrackData> trackGroups_list = new ArrayList<>();
    Context mcContext;
    String selected_quality = "";


    public CustomeTrackSelectionHelper(MappingTrackSelector selector,
                                       TrackSelection.Factory adaptiveTrackSelectionFactory) {
        this.selector = selector;
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
    }


    public void showSelectionDialog(Activity activity, CharSequence title, MappedTrackInfo trackInfo,
                                    int rendererIndex) {
        this.trackInfo = trackInfo;
        this.rendererIndex = rendererIndex;
        this.mcContext = activity;

        trackGroups = trackInfo.getTrackGroups(rendererIndex);
        trackGroupsAdaptive = new boolean[trackGroups.length];
        for (int i = 0; i < trackGroups.length; i++) {
            trackGroupsAdaptive[i] = adaptiveTrackSelectionFactory != null
                    && trackInfo.getAdaptiveSupport(rendererIndex, i, false)
                    != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED
                    && trackGroups.get(i).length > 1;
        }
        isDisabled = selector.getRendererDisabled(rendererIndex);
        override = selector.getSelectionOverride(rendererIndex, trackGroups);


        //,R.style.MyCustomTheme
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setView(buildView(builder.getContext()))
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    @SuppressLint("InflateParams")
    private View buildView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);


        // ravi edit
        View view = inflater.inflate(R.layout.dialog_quality_list, null);
        listView = (ListView) view.findViewById(R.id.listview);
        rel_defult = (RelativeLayout) view.findViewById(R.id.rel_defult);
        img_defult = (ImageView) view.findViewById(R.id.img_defult);

        if (passPosition == -1) {
            img_defult.setVisibility(View.VISIBLE);
        } else {
            img_defult.setVisibility(View.GONE);
        }


//        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio_default);
//        radioButton.setVisibility(View.VISIBLE);
        //and


        // Per-track views.
        boolean haveSupportedTracks = false;
        boolean haveAdaptiveTracks = false;


        trackGroups_list.clear();

        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            boolean groupIsAdaptive = trackGroupsAdaptive[groupIndex];
            haveAdaptiveTracks |= groupIsAdaptive;
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {

                //My cutome list
                trackGroups_list.add(new TrackData(group, trackInfo.getTrackFormatSupport(rendererIndex, groupIndex, trackIndex), groupIndex, trackIndex));
                //end

            }
        }


        customAdapter = new CustomAdapter(context, trackGroups_list, passPosition);
        listView.setAdapter(customAdapter);


        rel_defult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                override = null;

                passPosition = -1;

                selected_quality = "Auto";

                if (passPosition == -1) {
                    img_defult.setVisibility(View.VISIBLE);
                } else {
                    img_defult.setVisibility(View.GONE);
                }

                customAdapter = new CustomAdapter(context, trackGroups_list, passPosition);
                listView.setAdapter(customAdapter);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.v("dasdasdasda", "click " + position);
                isDisabled = false;

                passPosition = position;

                if (passPosition == -1) {
                    img_defult.setVisibility(View.VISIBLE);
                } else {
                    img_defult.setVisibility(View.GONE);
                }

                //For set quality
                selected_quality = DemoUtil.buildTrackName(trackGroups_list.get(position).getTrackGroup().getFormat(trackGroups_list.get(position).getTrackIndex()));

                customAdapter = new CustomAdapter(context, trackGroups_list, passPosition);
                listView.setAdapter(customAdapter);


//                if (txt.getText().toString().trim().equalsIgnoreCase(DemoUtil.buildTrackName(trackGroups_list.get(position).getTrackGroup().getFormat(trackGroups_list.get(position).getTrackIndex())))) {
//                    img.setVisibility(View.VISIBLE);
//                } else {
//                    img.setVisibility(View.GONE);
//                }


                int groupIndex = trackGroups_list.get(position).getGroupIndex();
                int trackIndex = trackGroups_list.get(position).getTrackIndex();

                override = new SelectionOverride(FIXED_FACTORY, groupIndex, trackIndex);

                setOverride(groupIndex, getTracksAdding(override, trackIndex),
                        false);

//

            }
        });


        return view;
    }


    // DialogInterface.OnClickListener

    @Override
    public void onClick(DialogInterface dialog, int which) {
        selector.setRendererDisabled(rendererIndex, isDisabled);
        if (override != null) {
            selector.setSelectionOverride(rendererIndex, trackGroups, override);
        } else {
            selector.clearSelectionOverrides(rendererIndex);
        }

        ((MainActivity) mcContext).setqualitytext(selected_quality);

    }


    private void setOverride(int group, int[] tracks, boolean enableRandomAdaptation) {
        TrackSelection.Factory factory = tracks.length == 1 ? FIXED_FACTORY
                : (enableRandomAdaptation ? RANDOM_FACTORY : adaptiveTrackSelectionFactory);
        override = new SelectionOverride(factory, group, tracks);
    }

    // Track array manipulation.

    private static int[] getTracksAdding(SelectionOverride override, int addedTrack) {
        int[] tracks = override.tracks;
        // tracks = Arrays.copyOf(tracks, tracks.length + 1);
        tracks[tracks.length - 1] = addedTrack;
        return tracks;
    }

    private static int[] getTracksRemoving(SelectionOverride override, int removedTrack) {
        int[] tracks = new int[override.length - 1];
        int trackCount = 0;
        for (int i = 0; i < tracks.length + 1; i++) {
            int track = override.tracks[i];
            if (track != removedTrack) {
                tracks[trackCount++] = track;
            }
        }
        return tracks;
    }

}
