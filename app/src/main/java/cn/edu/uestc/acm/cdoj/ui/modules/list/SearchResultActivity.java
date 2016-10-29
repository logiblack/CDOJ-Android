package cn.edu.uestc.acm.cdoj.ui.modules.list;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

import cn.edu.uestc.acm.cdoj.R;
import cn.edu.uestc.acm.cdoj.net.ConvertNetData;
import cn.edu.uestc.acm.cdoj.net.NetData;
import cn.edu.uestc.acm.cdoj.net.NetDataPlus;
import cn.edu.uestc.acm.cdoj.ui.contest.ContestList;
import cn.edu.uestc.acm.cdoj.ui.problem.ProblemList;
import cn.edu.uestc.acm.cdoj.ui.statusBar.FlyMeUtils;
import cn.edu.uestc.acm.cdoj.ui.statusBar.MIUIUtils;
import cn.edu.uestc.acm.cdoj.ui.statusBar.StatusBarUtil;

/**
 * Created by Great on 2016/10/12.
 */

public class SearchResultActivity extends AppCompatActivity {
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setupSystemBar();
        if (savedInstanceState == null) {
            initViews();
        }
    }

    private void setupSystemBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        StatusBarUtil.setStatusBarColor(this, R.color.statusBar_background_white, R.color.statusBar_background_gray, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || MIUIUtils.isMIUI() || FlyMeUtils.isFlyMe()) {
            StatusBarUtil.StatusBarLightMode(this);
        }
    }

    private void initViews() {
        intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        String key = intent.getStringExtra("key");
        ((TextView) findViewById(R.id.searchResult_title))
                .setText(String.format(Locale.CHINA, "\"%s\" search result:", key));

        ConvertNetData result = null;
        switch (type) {
            case NetData.PROBLEM_LIST:
                result = new ProblemList(this);
                int problemId = intent.getIntExtra("problemId", 0);
                if (problemId != 0) key = "";
                NetDataPlus.getProblemList(this, 1, key, problemId, result);
                break;
            case NetData.CONTEST_LIST:
                result = new ContestList(this);
                NetDataPlus.getContestList(this, 1, key, result);
                break;
        }
        if (result != null) {
            ((ListViewWithGestureLoad)result).setRefreshEnable(false);
        }
        ((ViewGroup)findViewById(R.id.searchResult_container))
                .addView((View) result);
    }
}
