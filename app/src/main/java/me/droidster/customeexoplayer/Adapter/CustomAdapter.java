package me.droidster.customeexoplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.RendererCapabilities;

import java.util.ArrayList;

import me.droidster.customeexoplayer.VideoUtil.DemoUtil;
import me.droidster.customeexoplayer.R;
import me.droidster.customeexoplayer.VideoUtil.TrackData;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by Piyush on 31-05-2017.
 */
public class CustomAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<TrackData> trackGroups;
    LayoutInflater mInflater;

    int passposition = -1;

    public CustomAdapter(Context context, ArrayList<TrackData> trackGroups, int passPosition) {
        this.mContext = context;
        this.trackGroups = trackGroups;
        mInflater = LayoutInflater.from(mContext);
        this.passposition = passPosition;
    }

    @Override
    public int getCount() {
        return trackGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_check, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.simpleCheckedTextView = (TextView) convertView.findViewById(R.id.simpleCheckedTextView);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //TrackGroup group = trackGroups.get(position);

        if(passposition==position){
            viewHolder.img.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img.setVisibility(View.GONE);
        }

        viewHolder.simpleCheckedTextView.setText(DemoUtil.buildTrackName(trackGroups.get(position).getTrackGroup().getFormat(trackGroups.get(position).getTrackIndex())));


        if (trackGroups.get(position).getTrack_support_or_not()
                == RendererCapabilities.FORMAT_HANDLED) {
            convertView.setFocusable(false);
            convertView.setEnabled(false);
            convertView.setBackgroundColor(getColor(mContext, R.color.white));
            viewHolder.simpleCheckedTextView.setTextColor(getColor(mContext, android.R.color.black));
            //trackView.setTag(Pair.create(groupIndex, trackIndex));
            // viewHolder.simpleCheckedTextView.setOnClickListener(mContext);
        } else {
            convertView.setFocusable(true);
            convertView.setEnabled(true);
            //convertView.setBackgroundColor(getColor(mContext,R.color.text_gray_light));
            viewHolder.simpleCheckedTextView.setTextColor(getColor(mContext, R.color.text_gray_light));

        }

        return convertView;
    }

    static class ViewHolder {
        TextView simpleCheckedTextView;
        ImageView img;
    }
}
