package com.news.theguardianapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.news.theguardianapp.R;
import com.news.theguardianapp.adapter.FeedAdapter;
import com.news.theguardianapp.database.DaoAsyncTask;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.networking.ArticleSerialize;
import com.news.theguardianapp.networking.HandleJson;
import com.news.theguardianapp.networking.JsonTask;
import com.news.theguardianapp.preference.LastIdPreference;
import com.news.theguardianapp.service.ByteArrayDownloadService;
import com.news.theguardianapp.service.NewItemsCheckerService;
import com.news.theguardianapp.utils.GlideUtils;
import com.news.theguardianapp.utils.NetworkUtils;
import com.news.theguardianapp.utils.PinnedArticlesAdpaterUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private FeedAdapter.OnClickPerform mOnClickPerform = new FeedAdapter.OnClickPerform() {
        @Override
        public void onItemClick(Article article) {
            beginSharedElementTransaction(article);
        }
    };

    private ArticleSerialize.CallServiceForImageByteArray mCallServiceForImageByteArray = new ArticleSerialize.CallServiceForImageByteArray() {
        @Override
        public void callServiceForArray(Article article) {
            Intent intent = new Intent(MainActivity.this, ByteArrayDownloadService.class);
            intent.putExtra(Article.class.getSimpleName(), article);
            startService(intent);
        }
    };
    private ByteArrayDownloadService.SetAdapterData setAdapterData = new ByteArrayDownloadService.SetAdapterData() {
        @Override
        public void setAdapterData(Article article) {
            mFeedAdapter.addArticle(article);
        }
    };


    private Timer mTimer;
    private RecyclerView mRecyclerView;
    private FeedAdapter mFeedAdapter;
    private RecyclerView mPinnedRecyclerView;
    private FeedAdapter mPinnedFeedAdapter;
    private AdapterDBDataSetter mAdapterDBDataSetter = new AdapterDBDataSetter() {
        @Override
        public void setAdapterDataFromDB(List<Article> articles) {
            mFeedAdapter.addData(articles);
        }

    };

    private HandleJson mHandleNewItems = new HandleJson() {
        @Override
        public void getNewPosts(JSONObject jsonObject) {
            loadNewPostFromJSON(jsonObject, 0);
        }
    };

    private HandleJson mHandleJson = new HandleJson() {
        @Override
        public void getNewPosts(JSONObject jsonObject) {

            loadNewPostFromJSON(jsonObject, -1);
            checkNewItemsIncomeT();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlideUtils.setContext(getApplicationContext());
        initRecyclerView();
        loadData();
        PinnedArticlesAdpaterUtils.setFeedAdapter(mPinnedFeedAdapter);
        initLinearLayoutData();
    }

    @Override
    protected void onStop() {
        startService(new Intent(this, NewItemsCheckerService.class));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        startService(new Intent(this, NewItemsCheckerService.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recycler_type_switch_menu, menu);


        Switch mySwitch = menu.findItem(R.id.type_change).getActionView().findViewById(R.id.switch_button);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeRecyclerViewType(isChecked);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initLinearLayoutData() {
        try {
            mPinnedFeedAdapter.addData(new DaoAsyncTask().execute("findPinnedArticles").get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        mFeedAdapter = new FeedAdapter();
        mFeedAdapter.setOnClickPerform(mOnClickPerform);
        mRecyclerView = findViewById(R.id.feed);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = ((LinearLayoutManager) recyclerView.getLayoutManager()).getChildCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int totalItemCount = ((LinearLayoutManager) recyclerView.getLayoutManager()).getItemCount();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadScrollingData();
                }
            }
        });
        mPinnedFeedAdapter = new FeedAdapter();
        mPinnedFeedAdapter.setOnClickPerform(mOnClickPerform);
        mPinnedRecyclerView = findViewById(R.id.pinned_articles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mPinnedRecyclerView.setLayoutManager(layoutManager);
        mPinnedRecyclerView.setAdapter(mPinnedFeedAdapter);
    }


    private void loadData() {
        if (NetworkUtils.isInternettAvailable())
            loadScrollingData();
        else {
            new DaoAsyncTask(mAdapterDBDataSetter).execute("findAll");
        }
    }

    private void loadScrollingData() {
        if (NetworkUtils.isInternettAvailable())
            new JsonTask(mHandleJson).execute();
    }

    private void beginSharedElementTransaction(Article article) {
        Intent intent = new Intent(this, ArticleDetailsActivity.class);
        intent.putExtra(Article.class.getSimpleName(), article);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) findViewById(R.id.article_image), "article");
        startActivity(intent, options.toBundle());
    }

    private void changeRecyclerViewType(boolean isPinterest) {
        if (isPinterest) {


            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mFeedAdapter);
        mFeedAdapter.notifyDataSetChanged();
    }

    private void loadNewPostFromJSON(JSONObject jsonObject, int index) {
        ArticleSerialize articleSerialize = new ArticleSerialize();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Article.class, articleSerialize)
                .create();
        Type listType = new TypeToken<List<Article>>() {
        }.getType();
        List<Article> articles = null;
        ByteArrayDownloadService.setSetAdapterData(setAdapterData);
        try {
            if (jsonObject.getJSONArray("results") == null)
                return;
            articles = gson.fromJson(jsonObject.getJSONArray("results").toString(), listType);
            if (index == -1)
                mFeedAdapter.addData(articles);
            else
                mFeedAdapter.addData(articles, index);
            LastIdPreference.saveLastId(this, articles.get(0).getId());
            for (Article article : articles)
                mCallServiceForImageByteArray.callServiceForArray(article);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void checkNewItemsIncomeT() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new JsonTask(mHandleNewItems, 1).execute();
            }
        }, 0, 30000);
    }


    public interface AdapterDBDataSetter {
        void setAdapterDataFromDB(List<Article> articles);

    }


}
