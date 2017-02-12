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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import mx.com.broadcastv.BroadcastvApplication;
import mx.com.broadcastv.R;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.ui.MainListActivity;
import mx.com.broadcastv.ui.fragment.FavoritesFragment;
import mx.com.broadcastv.ui.fragment.MainListFragment;
import mx.com.broadcastv.ui.interfaces.OnClickCallback;
import mx.com.broadcastv.util.BroadcastvSQLUtil;

public class FavoritesViewAdapter extends RecyclerView.Adapter<FavoritesViewAdapter.ItemHolder>
        implements View.OnClickListener {

    private static final String ITEM_POS = "item_pos";
    private static final String CHANNEL_ID = "channel_id";
    private static final String USERID = "user_id";
    private FragmentManager fm;
    private Context context;
    private Cursor mCursor;
    private FavoritesFragment favoriteFragment;

    public FavoritesViewAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    @Override
    public FavoritesViewAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CardView itemCardView =
                (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_card_view, viewGroup, false);
        return new FavoritesViewAdapter.ItemHolder(itemCardView, this);
    }

    @Override
    public void onBindViewHolder(FavoritesViewAdapter.ItemHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.textItemName.setText(mCursor.getString(FavoritesFragment.COL_NAME));
        holder.textItemName.setContentDescription(mCursor.getString(FavoritesFragment.COL_NAME));
        holder.textItemName.setSingleLine(true);
        holder.textItemName.setEllipsize(TextUtils.TruncateAt.END);
        holder.textItemGroup.setText(mCursor.getString(FavoritesFragment.COL_GROUP_NAME));
        holder.textItemGroup.setSingleLine(true);
        holder.textItemGroup.setEllipsize(TextUtils.TruncateAt.END);
        holder.cardView.setTag(mCursor.getString(FavoritesFragment.COL_CHANNEL_ID));
        holder.cardView.setOnClickListener(this);
        holder.cardView.setContentDescription(mCursor.getString(FavoritesFragment.COL_NAME));
        holder.playButton.setTag(mCursor);
        holder.playButton.setOnClickListener(this);
        Bundle args = new Bundle();
        args.putInt(ITEM_POS, position);
        args.putString(CHANNEL_ID, mCursor.getString(MainListFragment.COL_CHANNEL_ID));
        args.putString(USERID, mCursor.getString(MainListFragment.COL_ID_USER_CHANNEL));
        holder.deleteButton.setTag(args);
        holder.deleteButton.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return -1;
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
        } else if (v instanceof ImageButton) {
            Bundle data = (Bundle) v.getTag();
//            0 for removing the channel from favorite fragment
            BroadcastvSQLUtil.updateIsFavoriteChannel(context, BroadcastvApplication.getInstance().getUserId(), 0, data.getString(CHANNEL_ID));
            restartFavorites(data.getString(USERID));
            notifyItemRemoved(data.getInt(ITEM_POS));
            notifyDataSetChanged();
            ((OnClickCallback) context).showInteractiveMsg(context.getResources().getString(R.string.delete_favorite));
        } else if (v instanceof ImageView) {
            Cursor cursor = (Cursor) v.getTag();
            String url = cursor.getString(FavoritesFragment.COL_URL);
            String channelName = cursor.getString(FavoritesFragment.COL_NAME);
            ((OnClickCallback) context).onPlayButtonClicked(url, channelName);
        }
    }

    private void restartFavorites(String userId) {
        Bundle args = new Bundle();
        args.putString(USERID, userId);
        favoriteFragment = (FavoritesFragment) fm.findFragmentByTag(FavoritesFragment.FRAGMENT_TAG);
        if (favoriteFragment != null) {
            favoriteFragment.onOrderChanged(args);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView textItemName;
        TextView textItemGroup;
        ImageView playButton;
        ImageButton deleteButton;
        private FavoritesViewAdapter parent;
        private CardView cardView;

        public ItemHolder(CardView cView, FavoritesViewAdapter parent) {
            super(cView);
            cardView = cView;
            this.parent = parent;
            textItemName = (TextView) cardView.findViewById(R.id.item_name);
            textItemGroup = (TextView) cardView.findViewById(R.id.item_group);
            playButton = (ImageView) cardView.findViewById(R.id.playButton);
            deleteButton = (ImageButton) cardView.findViewById(R.id.deleteButton);
        }
    }
}
