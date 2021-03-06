package cn.edu.uestc.acm.cdoj.ui.detailFragment.contestDetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cn.edu.uestc.acm.cdoj.genaralData.GeneralFragment;

/**
 * Created by 14779 on 2017-8-5.
 */

public class ContestRankListFrg extends GeneralFragment {

    @SuppressLint("ValidFragment")
    public ContestRankListFrg(Context context, String type) {
        super(context, type);
    }

    public ContestRankListFrg() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setEnableLoadmore(false);
    }
}
