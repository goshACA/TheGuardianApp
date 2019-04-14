package com.news.theguardianapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.theguardianapp.R;
import com.news.theguardianapp.database.DaoAsyncTask;
import com.news.theguardianapp.entity.Article;
import com.news.theguardianapp.utils.BitmapConverterUtils;
import com.news.theguardianapp.utils.GlideUtils;
import com.news.theguardianapp.utils.PinnedArticlesAdpaterUtils;

public class ArticleDetailsActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitleText;
    private TextView mCategoryText;
    private Article mArticle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        init();
        if (getIntent() != null && getIntent().getSerializableExtra(Article.class.getSimpleName()) != null) {
            mArticle = (Article) getIntent().getSerializableExtra(Article.class.getSimpleName());
            loadInformationFromArticle(mArticle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item: {
                saveArticleInDB();
                return true;
            }
            case R.id.pin_item: {
                pinArticle();
                return true;
            }
            default:
                return false;
        }
    }

    private void init() {
        mTitleText = findViewById(R.id.text_article_details_title);
        mImageView = findViewById(R.id.article_image);
        mCategoryText = findViewById(R.id.text_article_details_category);
    }

    private void loadInformationFromArticle(Article article) {
        if (article.getImageBytes() != null)
            mImageView.setImageBitmap(BitmapConverterUtils.convertByteArrayToBitmap(article.getImageBytes()));
        else
            GlideUtils.loadImage(mImageView, article.getImageUrl());
        mTitleText.setText(article.getTitle());
        mCategoryText.setText(article.getCategory());
    }

    private void saveArticleInDB() {
        new DaoAsyncTask(mArticle).execute("insert");
    }

    private void pinArticle() {
        mArticle.setPined(true);
        new DaoAsyncTask(mArticle).execute("insertPinned");
        PinnedArticlesAdpaterUtils.getFeedAdapter().addArticle(mArticle);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
