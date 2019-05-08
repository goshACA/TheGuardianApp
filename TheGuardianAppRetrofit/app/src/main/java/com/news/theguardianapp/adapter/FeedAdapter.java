package com.news.theguardianapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.news.theguardianapp.R;
import com.news.theguardianapp.adapter.viewholder.FeedItemViewHolder;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.utils.GlideUtils;
import com.news.theguardianapp.utils.ImageUtils;
import com.news.theguardianapp.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedItemViewHolder> {

    private List<Article> mArticles = new ArrayList<>();
    private OnClickPerform mOnClickPerform;


    private FeedItemViewHolder.OnViewClick mOnViewClick = new FeedItemViewHolder.OnViewClick() {
        @Override
        public void onItemViewClick(int position) {
            mOnClickPerform.onItemClick(mArticles.get(position));
        }
    };


    public void addData(List<Article> data) {

        mArticles.addAll(data);
       notifyDataSetChanged();
    }

    public void addData(List<Article> data, int index) {
        data.removeAll(mArticles);
        mArticles.addAll(data);
        notifyDataSetChanged();

    }

    public void addArticle(Article article) {
        int articleIndex = mArticles.indexOf(article);
        if (articleIndex == -1) {
            mArticles.add(article);
            notifyDataSetChanged();
            return;
        }
        mArticles.set(articleIndex, article);
        notifyDataSetChanged();
    }

    public void clear() {
        mArticles.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FeedItemViewHolder feedItemViewHolder = new FeedItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false));
        feedItemViewHolder.setOnViewClick(mOnViewClick);
        return feedItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedItemViewHolder feedItemViewHolder, int i) {
        Article item = mArticles.get(i);
        if (item == null)
            return;
        feedItemViewHolder.setCategoryText(item.getCategory());
        feedItemViewHolder.setTitleText(item.getTitle());
        if(NetworkUtils.isInternettAvailable())
            GlideUtils.loadImage(feedItemViewHolder.getImageView(), item.getImageUrl());
        else
            feedItemViewHolder.getImageView().setImageBitmap(ImageUtils.loadImageFromFolder(item.getImageUrl()));
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void setOnClickPerform(OnClickPerform onClickPerform) {
        mOnClickPerform = onClickPerform;
    }

    public interface OnClickPerform {
        void onItemClick(Article article);
    }
}

