package com.news.theguardianapp.database;

import android.os.AsyncTask;

import com.news.theguardianapp.activities.MainActivity;
import com.news.theguardianapp.entity.Article;

import java.util.List;

public class DaoAsyncTask extends AsyncTask<String, Void, List<Article>> {

    private MainActivity.AdapterDBDataSetter mAdapterDBDataSetter;
    private Article mArticle;



    public DaoAsyncTask(){}

    public DaoAsyncTask(MainActivity.AdapterDBDataSetter adapterDBDataSetter){
        mAdapterDBDataSetter = adapterDBDataSetter;
    }

    public DaoAsyncTask(Article article){
        mArticle = article;
    }



    public DaoAsyncTask(MainActivity.AdapterDBDataSetter adapterDBDataSetter, Article article){
        mAdapterDBDataSetter = adapterDBDataSetter;
        mArticle = article;
    }

    @Override
    protected List<Article> doInBackground(String... strings) {

        ArticleDao articleDao = DbWrapper.getAppDatabase().mArticleDao();
        switch (strings[0]) {
            case "findPinnedArticles": {
                if (articleDao.findPinnedArticles() != null)
                    return (articleDao.findPinnedArticles());
            }
            break;
            case "insert": {
                articleDao.insert(mArticle);
                break;
            }
            case "insertPinned": {
                articleDao.insert(mArticle);
                break;
            }

            case "findAll":{
                return articleDao.findAll();
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        super.onPostExecute(articles);
        if(mAdapterDBDataSetter != null && articles != null)
            mAdapterDBDataSetter.setAdapterDataFromDB(articles);
    }
}
