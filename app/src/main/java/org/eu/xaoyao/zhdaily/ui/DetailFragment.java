package org.eu.xaoyao.zhdaily.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsDetailBean;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    @BindView(R.id.web_view)
    WebView mWebView;

    private ZHApiManager mZhApiManager;
    private NewsDetailBean mNews;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, view);

        initData();
        return view;
    }

    private void initData() {
        mZhApiManager = ZHApiManager.getInstance();
        String newsId = getArguments().getString("newsId");

//        mWebView.setHorizontalScrollBarEnabled(true);
//        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);

        mZhApiManager.getNewsDetail(newsId, new Subscriber<NewsDetailBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtil.showToast(getContext(),"网络错误");
            }

            @Override
            public void onNext(NewsDetailBean newsDetailBean) {
                mNews = newsDetailBean;

                ((NewsDetailActivity) getActivity()).updateView(mNews);
                StringBuffer body = new StringBuffer();
                body.append("<html> <head> ");

                if (mNews.js.size() > 0) {
                    mWebView.loadUrl(mNews.js.get(0));
                    for (String js : mNews.js) {
                        body.append("<script type=\"text/javascript\" src=\"")
                                .append(js).append("\"/>");
                    }
                }
                if (mNews.css.size() > 0) {
                    for (String css : mNews.css) {
                        body.append("<link href=\"")
                                .append(css)
                                .append("\" rel=\"stylesheet\" type=\"text/css\" />");
                    }
                }
                body.append("</head>");
                body.append(mNews.body);
                body.append("</html>");
                mWebView.loadDataWithBaseURL(null, body.toString(), "text/html", "UTF-8", null);

            }
        });


    }


}
