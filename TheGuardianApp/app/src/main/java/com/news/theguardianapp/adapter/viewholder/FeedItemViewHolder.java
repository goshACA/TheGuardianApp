package com.news.theguardianapp.adapter.viewholder;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.theguardianapp.R;

public class FeedItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImageView;
    private TextView mCategoryTextView;
    private TextView mTitleTextView;
    private OnViewClick mOnViewClick;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mOnViewClick.onItemViewClick(getAdapterPosition());
        }
    };

    public FeedItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.article_image);
        mCategoryTextView = itemView.findViewById(R.id.article_category);
        mTitleTextView = itemView.findViewById(R.id.article_title);
        itemView.setOnClickListener(mOnClickListener);
    }

    public void setCategoryText(String text) {
        mCategoryTextView.setText(text);
    }

    public void setTitleText(String text) {
        mTitleTextView.setText(text);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    public TextView getCategoryTextView() {
        return mCategoryTextView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public void setOnViewClick(OnViewClick onViewClick) {
        mOnViewClick = onViewClick;
    }

    public interface OnViewClick {
        void onItemViewClick(int position);
    }
}
