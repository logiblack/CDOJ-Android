package cn.edu.uestc.acm.cdoj.layout.detail.contest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.edu.uestc.acm.cdoj.Global;
import cn.edu.uestc.acm.cdoj.R;
import cn.edu.uestc.acm.cdoj.TimeFormat;
import cn.edu.uestc.acm.cdoj.layout.list.ListFragmentWithGestureLoad;
import cn.edu.uestc.acm.cdoj.net.NetData;
import cn.edu.uestc.acm.cdoj.net.ViewHandler;
import cn.edu.uestc.acm.cdoj.net.data.Rank;

import static cn.edu.uestc.acm.cdoj.Global.userManager;

/**
 * Created by great on 2016/8/25.
 */
public class ContestRank extends ListFragmentWithGestureLoad implements ViewHandler {
    public static final int NOTHING = 0;
    public static final int TRIED = 1;
    public static final int SOLVED = 2;
    public static final int THEFIRSTSOLVED = 3;

    private SimpleAdapter adapter;
    private ArrayList<Map<String, Object>> listItems = new ArrayList<>();
    private int contestID;
    private ListView mListView;
    private int probCount;
    private boolean firstLoad = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mListView = getListView();
            if (firstLoad && contestID != -1) refresh();
            setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            firstLoad = false;
        }
    }

    @Override
    public void addListItem(Map<String, Object> listItem) {
        listItems.add(listItem);
    }

    @Override
    public void notifyDataSetChanged() {
        if (adapter == null) {
            createAdapter();
        }
        adapter.notifyDataSetChanged();
        super.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> listItem = listItems.get(position);
        ListView rankItemListView = new ListView(l.getContext());
        LayoutInflater inflater = (LayoutInflater) l.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout headerView = (LinearLayout) inflater.inflate(R.layout.contest_rank_list_item_detail_header, null);
        if (listItem.get("header") instanceof Bitmap) {
            ((ImageView) headerView.findViewById(R.id.contestRankItemHeader_header))
                    .setImageBitmap((Bitmap) listItem.get("header"));
        } else {
            ((ImageView) headerView.findViewById(R.id.contestRankItemHeader_header))
                    .setImageResource((int) listItem.get("header"));
        }
        ((TextView) headerView.findViewById(R.id.contestRankItemHeader_nickName))
                .setText((String) listItem.get("nickName"));
        ((TextView) headerView.findViewById(R.id.contestRankItemHeader_rank))
                .setText((String) listItem.get("rank"));
        ((TextView) headerView.findViewById(R.id.contestRankItemHeader_solvedNum))
                .setText(String.valueOf((int) listItem.get("solvedNum")));
        rankItemListView.addHeaderView(headerView);
        final ArrayList<Map<String, Object>> rankProblemsStatusList = (ArrayList<Map<String, Object>>) listItems.get(position).get("problemsStatus");
        SimpleAdapter adapter = new SimpleAdapter(
                Global.currentMainActivity, rankProblemsStatusList, R.layout.contest_rank_list_item_detail,
                new String[]{"probOrder", "solvedTime", "failureNum", "isFirstSuccess"},
                new int[]{R.id.contestRankItem_Prob, R.id.contestRankItem_solvedTime, R.id.contestRankItem_failureCount, R.id.contestRankItem_isFirstSuccess}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                switch ((int) (rankProblemsStatusList.get(position).get("solvedStatus"))) {
                    case ContestRank.TRIED:
                        v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.rank_yellow));
                        return v;
                    case ContestRank.SOLVED:
                        v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.rank_halfGreen));
                        return v;
                    case ContestRank.THEFIRSTSOLVED:
                        v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.rank_green));
                        return v;
                }
                v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.rank_gray));
                return v;
            }
        };
        rankItemListView.setAdapter(adapter);
        new AlertDialog.Builder(l.getContext())
                .setView(rankItemListView)
                .setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    private void createAdapter() {
        adapter = new SimpleAdapter(
                Global.currentMainActivity, listItems, R.layout.contest_rank_list_item,
                new String[]{"header", "rank", "nickName", "account", "solvedDetail"},
                new int[]{R.id.contestRank_header, R.id.contestRank_rank, R.id.contestRank_nickName,
                        R.id.contestRank_account, R.id.contestRank_solved});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView) {
                    if (data instanceof Integer) {
                        ((ImageView) view).setImageResource((int) data);
                        return true;
                    } else if (data instanceof Bitmap) {
                        ((ImageView) view).setImageBitmap((Bitmap) data);
                        return true;
                    }
                } else if (view instanceof TextView) {
                    ((TextView) view).setText(data.toString());
                    return true;
                } else if (view instanceof LinearLayout) {
                    int[] solvedDetail = (int[]) data;
                    char text = 'A';
                    for (int i = 0; i < 9 && i < probCount; ++i) {
                        FrameLayout markContainer = (FrameLayout) ((LinearLayout) view).getChildAt(i);
                        ImageView bg = (ImageView) markContainer.getChildAt(0);
                        TextView tx = (TextView) markContainer.getChildAt(1);
                        tx.setText(String.valueOf((char) (text + i)));
                        if (((solvedDetail[2] >> i) & 1) == 1) {
                            bg.setImageResource(R.drawable.contest_rank_mark_bg_green);
                        } else if (((solvedDetail[1] >> i) & 1) == 1) {
                            bg.setImageResource(R.drawable.contest_rank_mark_bg_green_half);
                        } else if (((solvedDetail[0] >> i) & 1) == 1) {
                            bg.setImageResource(R.drawable.contest_rank_mark_bg_yellow);
                        } else
                            bg.setImageResource(R.drawable.contest_rank_mark_bg_gray);
                    }
                    if (probCount >= 9) {
                        TextView tx = (TextView) ((LinearLayout) view).getChildAt(9);
                        tx.setText("......");
                    } else {
                        for (int i = probCount; i < 9; i++) {
                            FrameLayout markContainer = (FrameLayout) ((LinearLayout) view).getChildAt(i);
                            markContainer.setVisibility(View.INVISIBLE);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    public void setContestID(int id) {
        contestID = id;
    }

    @Override
    public void show(int which, Object data, long time) {
        switch (which) {
            case ViewHandler.CONTEST_RANK:
                Rank rankInfo = (Rank) data;
                if (rankInfo.result) {
                    ArrayList<Rank.Performance> infoList_rank = rankInfo.getPerformanceList();
                    for (int i = 0; i != infoList_rank.size(); ++i) {
                        Rank.Performance tem = infoList_rank.get(i);
                        Map<String, Object> listItem = new HashMap<>();
                        listItem.put("header", R.drawable.logo);
                        listItem.put("rank", "Rank  " + tem.rank);
                        listItem.put("account", tem.name);
                        listItem.put("nickName", tem.nickName);
                        listItem.put("solvedNum", tem.solved);
                        ArrayList<Rank.Performance.ProblemStatus> problemStatusList = tem.getProblemStatusList();
                        probCount = problemStatusList.size();
                        Collections.reverse(problemStatusList);
                        int[] solvedDetail = {0, 0, 0};
                        ArrayList<Map<String, Object>> problemStatus = new ArrayList<>();
                        for (int j = 0; j != probCount; ++j) {
                            Map<String, Object> temStatus = new HashMap<>();
                            Rank.Performance.ProblemStatus temProbStatus = problemStatusList.get(j);
                            temStatus.put("probOrder", String.valueOf((char) ('A' + (probCount - j -1))));
                            temStatus.put("solvedTime", TimeFormat.getFormatTime(temProbStatus.solvedTime));
                            temStatus.put("failureNum", temProbStatus.tried);
                            if (temProbStatus.firstBlood) {
                                temStatus.put("isFirstSuccess", "是");
                            }else temStatus.put("isFirstSuccess", "否");
                            problemStatus.add(temStatus);
                            solvedDetail[0] = solvedDetail[0] << 1;
                            solvedDetail[1] = solvedDetail[1] << 1;
                            solvedDetail[2] = solvedDetail[2] << 1;
                            if (temProbStatus.firstBlood){
                                ++solvedDetail[2];
                                temStatus.put("solvedStatus", ContestRank.THEFIRSTSOLVED);
                            }else if (temProbStatus.solved){
                                ++solvedDetail[1];
                                temStatus.put("solvedStatus", ContestRank.SOLVED);
                            }else if (temProbStatus.tried > 0){
                                ++solvedDetail[0];
                                temStatus.put("solvedStatus", ContestRank.TRIED);
                            }else temStatus.put("solvedStatus", ContestRank.NOTHING);
                        }
                        Collections.reverse(problemStatus);
                        listItem.put("problemsStatus", problemStatus);
                        listItem.put("solvedDetail", solvedDetail);
                        listItem.put("detail", problemStatusList);
                        addListItem(listItem);
                        userManager.getAvatar(tem.email, i, this);
                    }
                }
                notifyDataSetChanged();
                return;
            case ViewHandler.AVATAR:
                Object[] dataReceive = (Object[]) data;
                int position = (int) dataReceive[0];
                if (position < listItems.size())
                    listItems.get(position).put("header", dataReceive[1]);
                if (position >= mListView.getFirstVisiblePosition() &&
                        position <= mListView.getLastVisiblePosition()) {
                    ViewGroup viewGroup = (ViewGroup) mListView
                            .getChildAt(position - mListView.getFirstVisiblePosition());
                    ImageView headerImage = null;
                    if (viewGroup != null)
                        headerImage = (ImageView) viewGroup.findViewById(R.id.contestClarification_header);
                    if (headerImage != null) headerImage.setImageBitmap((Bitmap) dataReceive[1]);
                }
        }

    }

    public void refresh() {
        if (contestID == -1) throw new IllegalStateException("Rank's contestId is null");
        listItems.clear();
        NetData.getContestRank(contestID, this);
    }
}
