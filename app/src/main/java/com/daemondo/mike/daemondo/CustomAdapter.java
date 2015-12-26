package com.daemondo.mike.daemondo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mike on 20/12/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
        implements ItemTouchHelperAdapter{

    private ArrayList<CardItem> mItems;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView mCard;
        public RelativeLayout mRl;
        public EditText mTextView;
        public ImageView mExtra;
        public ImageView mBody;
        public ImageView mEyes;
        
        public ViewHolder(View v) {
            super(v);
            mCard = (CardView)v.findViewById(R.id.card_view);
            mRl = (RelativeLayout)v.findViewById(R.id.daemon_card);
            mTextView = (EditText)v.findViewById(R.id.note);
            mExtra = (ImageView)v.findViewById(R.id.card_extra);
            mBody = (ImageView)v.findViewById(R.id.card_body);
            mEyes = (ImageView)v.findViewById(R.id.card_eyes);
        }
    }

    public CustomAdapter(ArrayList<CardItem> myDataset) {
        mItems = myDataset;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        holder.mTextView.setText(mItems.get(position).getNote());
        Daemon d = mItems.get(position).getDaemon();
        holder.mExtra.setImageResource(mItems.get(position).getDaemon().extraRes);
        holder.mBody.setImageResource(mItems.get(position).getDaemon().bodyRes);
        holder.mEyes.setImageResource(mItems.get(position).getDaemon().eyesRes);
        d.setViews(holder.mRl, holder.mExtra, holder.mBody, holder.mEyes);
        d.idle();

        // Attach text listener
        holder.mTextView.addTextChangedListener(new CustomTextListener(mItems.get(position)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onItemDismiss(int position) {
        MainActivity.mainActivity.cardRemoved(mItems.get(position).getDaemon());
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class CustomTextListener implements TextWatcher {

        private CardItem mCardItem;
        public CustomTextListener(CardItem cardItem)
        {
            mCardItem = cardItem;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCardItem.setNote(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
