package com.news.theguardianapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.news.theguardianapp.R;
import com.news.theguardianapp.adapter.FeedAdapter;
import com.news.theguardianapp.database.DaoAsyncTask;
import com.news.theguardianapp.entity.ApiResponse;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.preference.LastIdPreference;
import com.news.theguardianapp.retrofit.ApiManager;
import com.news.theguardianapp.service.NewItemsCheckerService;
import com.news.theguardianapp.utils.GlideUtils;
import com.news.theguardianapp.utils.NetworkUtils;
import com.news.theguardianapp.utils.PinnedArticlesAdpaterUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final String APIKEY = "test";
    private final String SHOWFIELDS = "thumbnail";
    private int page = 1;
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

    private FeedAdapter.OnClickPerform mOnClickPerform = new FeedAdapter.OnClickPerform() {
        @Override
        public void onItemClick(Article article) {
            beginSharedElementTransaction(article);
        }
    };
    private Timer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initRecyclerView();
        GlideUtils.setContext(getApplicationContext());
        loadData();
        PinnedArticlesAdpaterUtils.setFeedAdapter(mPinnedFeedAdapter);
        initLinearLayoutData();
        checkNewItemsIncome();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("PAGE", page);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page = savedInstanceState.getInt("PAGE");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Please allow permission", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    loadScrollingData(1);
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


    private void initLinearLayoutData() {
        try {
            mPinnedFeedAdapter.addData(new DaoAsyncTask().execute("findPinnedArticles").get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void loadData() {
        if (NetworkUtils.isInternettAvailable())
            loadScrollingData(1);
        else {
            new DaoAsyncTask(mAdapterDBDataSetter).execute("findAll");
        }
    }

    private void loadScrollingData(final int index) {
        final Call<ApiResponse> apiResponseCall = ApiManager.getApiClient().searchArticles(APIKEY, SHOWFIELDS, page);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (index == 1) {
                        if (page == 1) {
                            LastIdPreference.saveLastId(MainActivity.this, response.body().getResponse().getArticleList().get(0).getId());
                        }
                        ++page;
                    } else if (index == 0) {
                        page = 1;
                    }
                    mFeedAdapter.addData(response.body().getResponse().getArticleList(), index);

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

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


    private void beginSharedElementTransaction(Article article) {
        Intent intent = new Intent(this, ArticleDetailsActivity.class);
        intent.putExtra(Article.class.getSimpleName(), article);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) findViewById(R.id.article_image), "article");
        startActivity(intent, options.toBundle());
    }

    private void checkNewItemsIncome() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadScrollingData(0);
            }
        }, 0, 30000);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public interface AdapterDBDataSetter {
        void setAdapterDataFromDB(List<Article> articles);
    }


}
