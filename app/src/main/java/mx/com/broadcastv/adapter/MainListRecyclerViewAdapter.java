package mx.com.broadcastv.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mx.com.broadcastv.R;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.ui.fragment.DetailChannelFragment;
import mx.com.broadcastv.ui.fragment.MainListFragment;
import mx.com.broadcastv.ui.interfaces.OnClickCallback;


public class MainListRecyclerViewAdapter extends
        RecyclerView.Adapter<MainListRecyclerViewAdapter.ItemHolder> implements View.OnClickListener {

    private static final String ITEM_POS = "item_pos";
    private FragmentManager fm;
    private MainListFragment mainListFragment;
    private Context context;
    private Cursor mCursor;
    private DetailChannelFragment detailChannelFragment;


    public MainListRecyclerViewAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CardView itemCardView =
                (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new ItemHolder(itemCardView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.textItemName.setText(mCursor.getString(MainListFragment.COL_NAME));
        holder.textItemName.setContentDescription(mCursor.getString(MainListFragment.COL_NAME));
        holder.textItemName.setSingleLine(true);
        holder.textItemName.setEllipsize(TextUtils.TruncateAt.END);
        holder.textItemGroup.setText(mCursor.getString(MainListFragment.COL_GROUP_NAME));
        holder.textItemGroup.setSingleLine(true);
        holder.textItemGroup.setEllipsize(TextUtils.TruncateAt.END);
        holder.cardView.setTag(mCursor.getString(MainListFragment.COL_CHANNEL_ID));
        holder.cardView.setOnClickListener(this);
        holder.cardView.setContentDescription(mCursor.getString(MainListFragment.COL_NAME));
        holder.playButton.setTag(mCursor);
        holder.playButton.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CardView) {
            String channel_id = (String) v.getTag();
            ((OnClickCallback) context).onItemSelected(
                    ServicesContract.ChannelEntry.buildChannelIdUriQuery(channel_id)
            );
        } else if (v instanceof ImageView) {
            Cursor cursor = (Cursor) v.getTag();
            String url = cursor.getString(MainListFragment.COL_URL);
            String channelName = cursor.getString(MainListFragment.COL_NAME);
            ((OnClickCallback) context).onPlayButtonClicked(url, channelName);
        }
    }

    private void restartChannels() {
        Bundle args = new Bundle();
        mainListFragment = (MainListFragment) fm.findFragmentByTag(MainListFragment.FRAGMENT_TAG);
        detailChannelFragment = (DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if (mainListFragment != null) {
            mainListFragment.onOrderChanged(args);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textItemName;
        TextView textItemGroup;
        ImageView playButton;


        public ItemHolder(CardView cView) {
            super(cView);
            cardView = cView;
            textItemName = (TextView) cardView.findViewById(R.id.item_name);
            textItemGroup = (TextView) cardView.findViewById(R.id.item_group);
            playButton = (ImageView) cardView.findViewById(R.id.playButton);
        }
    }


}
