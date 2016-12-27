package mx.com.broadcastv.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mx.com.broadcastv.R;
import mx.com.broadcastv.ui.SearchActivity;
import mx.com.broadcastv.ui.fragment.MainListFragment;


/**
 * Created by LuisGerardo on 16/07/2015.
 * For more info contact : luis.crackle@gmail.com
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> implements
        View.OnClickListener{

    SearchActivity.SearchSelectionListener listener;

    final Context context;
    private Cursor mCursor;
    public static final int COL_ID = 0;
    public static final int COL_COUNTRY = 1;
    public static final int COL_DESCRIPTION = 2;
    public static final int COL_CHANNEL_ID = 3;
    public static final int COL_NAME = 4;
    public static final int COL_LANGUAGE = 5;
    public static final int COL_LOGO = 6;
    public static final int COL_URL = 7;
    public static final int COL_GROUP_ID = 8;
    public static final int COL_GROUP_NAME = 9;
    public static final int COL_ID_USER_CHANNEL = 10;
    public static final int COL_IS_FAVORITE = 11;

    public SearchAdapter(Context context,SearchActivity.SearchSelectionListener listener){
        this.context = context;
        this.listener = listener;
    }

    public SearchAdapter(Context context){
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view= (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.textItemName.setText(mCursor.getString(COL_NAME));
        holder.textItemName.setContentDescription(mCursor.getString(COL_NAME));
        holder.textItemName.setSingleLine(true);
        holder.textItemName.setEllipsize(TextUtils.TruncateAt.END);
        holder.textItemGroup.setText(mCursor.getString(COL_GROUP_NAME));
        holder.textItemGroup.setSingleLine(true);
        holder.textItemGroup.setEllipsize(TextUtils.TruncateAt.END);
        holder.cardView.setTag(mCursor.getString(COL_CHANNEL_ID));
        holder.cardView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if(mCursor== null){
            return -1;
        }else{
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
        if(v instanceof CardView){
            Intent data = new Intent();
            String channelId = (String) v.getTag();
            data.putExtra(SearchActivity.SEARCH_CHANNEL_URI, channelId);

            listener.searchSelection(data);
            // listener.setSelection(browseModel.getID(),MainHolderActivity.CHANNEL_TYPE,browseModel.getRootChannelID(),null);
        }
    }


    public static class ItemHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView textItemName;
        TextView textItemGroup;
        ImageView playButton;


        public ItemHolder(CardView cView) {
            super(cView);
            cardView = cView;
            textItemName = (TextView) cardView.findViewById(R.id.item_name);
            textItemGroup = (TextView) cardView.findViewById(R.id.item_group);
            playButton    = (ImageView) cardView.findViewById(R.id.playButton);
        }
    }

}
