package cn.edu.uestc.acm.cdoj.net;
import android.os.Handler;
import android.os.Message;

import java.util.List;

import cn.edu.uestc.acm.cdoj.genaralData.ContentReceived;
import cn.edu.uestc.acm.cdoj.genaralData.ListReceived;
import cn.edu.uestc.acm.cdoj.net.article.Article;
import cn.edu.uestc.acm.cdoj.net.article.ArticleConnection;
import cn.edu.uestc.acm.cdoj.net.article.ArticleListItem;
import cn.edu.uestc.acm.cdoj.net.article.ObtainArticle;
import cn.edu.uestc.acm.cdoj.net.contest.Contest;
import cn.edu.uestc.acm.cdoj.net.contest.ContestConnection;
import cn.edu.uestc.acm.cdoj.net.contest.ContestListItem;
import cn.edu.uestc.acm.cdoj.net.contest.ContestReceived;
import cn.edu.uestc.acm.cdoj.net.contest.ObtainContest;
import cn.edu.uestc.acm.cdoj.net.contest.comment.ContestCommentListItem;
import cn.edu.uestc.acm.cdoj.net.contest.problem.ContestProblem;
import cn.edu.uestc.acm.cdoj.net.contest.rank.RankListOverview;
import cn.edu.uestc.acm.cdoj.net.contest.rank.RankListReceived;
import cn.edu.uestc.acm.cdoj.net.contest.status.ContestStatusListItem;
import cn.edu.uestc.acm.cdoj.net.homePage.HomePageConnection;
import cn.edu.uestc.acm.cdoj.net.homePage.RecentContestListItem;
import cn.edu.uestc.acm.cdoj.net.problem.ObtainProblem;
import cn.edu.uestc.acm.cdoj.net.problem.Problem;
import cn.edu.uestc.acm.cdoj.net.problem.ProblemConnection;
import cn.edu.uestc.acm.cdoj.net.problem.ProblemListItem;
import cn.edu.uestc.acm.cdoj.net.problem.ProblemStatusListItem;
import cn.edu.uestc.acm.cdoj.utils.ThreadUtil;

/**
 * Created by 14779 on 2017-7-21.
 */

public class Connection implements ObtainArticle, ObtainProblem, ObtainContest {

    private static final String TAG = "Connection";
    public static Connection instance = new Connection();

    //通过handler将callback传递出去
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x01012013:
                    Object[] data1 = (Object[]) msg.obj;
                    ReceivedCallback callback1 = (ReceivedCallback) data1[0];
                    Object result1 = data1[1];
                    callback1.onDataReceived(result1);
                    break;
                case 0x01012014:
                    Object[] data2 = (Object[]) msg.obj;
                    ReceivedCallback callback2 = (ReceivedCallback) data2[0];
                    Object result2 = data2[1];
                    callback2.onLoginDataReceived((ContentReceived) result2);
                    break;
            }
        }
    };

    @Override
    public void getArticleContent(final int id, final ReceivedCallback<Article> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Article result = ArticleConnection.getInstance().getContent(id);
                Message message = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                message.obj = obj;
                message.what = 0x01012013;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void getArticleList(int page, ReceivedCallback<ListReceived<ArticleListItem>> callback) {
        searchArticle(page, "time", callback);
    }

    @Override
    public void searchArticle(int page, String orderFields, ReceivedCallback<ListReceived<ArticleListItem>> callback) {
        searchArticle(page, orderFields, 0, callback);
    }

    @Override
    public void searchArticle(int page, String orderFields, int type, ReceivedCallback<ListReceived<ArticleListItem>> callback) {
        searchArticle(page, orderFields, type, false, callback);
    }

    @Override
    public void searchArticle(final int page, final String orderFields, final int type, final boolean orderAsc, final ReceivedCallback<ListReceived<ArticleListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ArticleListItem> result = ArticleConnection.getInstance().getSearch(page, type, orderFields, orderAsc);
//                List<ArticleListItem> result = new ArrayList<>();
                Message message = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                message.obj = obj;
                message.what = 0x01012013;
                handler.sendMessage(message);
            }
        });
    }


    @Override
    public void getProblemContent(final int id, final ReceivedCallback<Problem> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Problem result = ProblemConnection.getInstance().getContent(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.what = 0x01012013;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getProblemList(int page, ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        searchProblem(page, "id", callback);
    }

    @Override
    public void searchProblem(int page, ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        searchProblem(page, "id", callback);
    }

    @Override
    public void searchProblem(int page, String orderFields, ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        searchProblem(page, orderFields, true, callback);
    }

    @Override
    public void searchProblem(int page, String orderFields, boolean orderAsc, ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        searchProblem(page, orderFields, orderAsc, "", callback);
    }

    @Override
    public void searchProblem(int page, String orderFields, boolean orderAsc, String keyword, ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        searchProblem(page, orderFields, orderAsc, keyword, 0, callback);
    }

    @Override
    public void searchProblem(final int page, final String orderFields, final boolean orderAsc, final String keyword, final int startId, final ReceivedCallback<ListReceived<ProblemListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ProblemListItem> result = ProblemConnection.getInstance().getSearch(page, orderFields, orderAsc, keyword, startId);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getProblemStatus(int problemId, int currentPage, ReceivedCallback<ListReceived<ProblemStatusListItem>> callback) {
        getProblemStatus(problemId, currentPage, "time", callback);
    }

    @Override
    public void getProblemStatus(int problemId, int currentPage, String orderFields, ReceivedCallback<ListReceived<ProblemStatusListItem>> callback) {
        getProblemStatus(problemId, currentPage, orderFields, false, callback);
    }

    @Override
    public void getProblemStatus(int problemId, int currentPage, String orderFields, boolean orderAsc, ReceivedCallback<ListReceived<ProblemStatusListItem>> callback) {
        getProblemStatus(problemId, currentPage, orderFields, orderAsc, -1, callback);
    }

    @Override
    public void getProblemStatus(int problemId, int currentPage, String orderFields, boolean orderAsc, int contestId, ReceivedCallback<ListReceived<ProblemStatusListItem>> callback) {
        getProblemStatus(problemId, currentPage, orderFields, orderAsc, contestId, 0, callback);
    }

    @Override
    public void getProblemStatus(final int problemId, final int currentPage, final String orderFields, final boolean orderAsc, final int contestId, final int result, final ReceivedCallback<ListReceived<ProblemStatusListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ProblemStatusListItem> received = ProblemConnection.getInstance().getStatus(problemId, currentPage, orderFields, orderAsc, contestId, result);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = received;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getContestContent(final int id, final ReceivedCallback<Contest> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Contest result = ContestConnection.getInstance().getContent(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getContestReceived(final int id, final ReceivedCallback<ContestReceived> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ContestReceived result = ContestConnection.getInstance().getContestReceived(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getContestProblemList(final int id, final ReceivedCallback<List<ContestProblem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<ContestProblem> result = ContestConnection.getInstance().getContestProblemList(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
     }

    @Override
    public void getContestList(int page, ReceivedCallback<ListReceived<ContestListItem>> callback) {
        searchContest(page, callback);
    }

    @Override
    public void searchContest(int page, ReceivedCallback<ListReceived<ContestListItem>> callback) {
        searchContest(page, "time", callback);
    }

    @Override
    public void searchContest(int page, String orderFields, ReceivedCallback<ListReceived<ContestListItem>> callback) {
        searchContest(page, orderFields, false, callback);
    }

    @Override
    public void searchContest(int page, String orderFields, boolean orderAsc, ReceivedCallback<ListReceived<ContestListItem>> callback) {
        searchContest(page, orderFields, orderAsc, "", callback);
    }

    @Override
    public void searchContest(int page, String orderFields, boolean orderAsc, String keyword, ReceivedCallback<ListReceived<ContestListItem>> callback) {
        searchContest(page, orderFields, orderAsc, keyword, 1, callback);
    }

    @Override
    public void searchContest(final int page, final String orderFields, final boolean orderAsc, final String keyword, final int startId, final ReceivedCallback<ListReceived<ContestListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ContestListItem> result = ContestConnection.getInstance().getSearch(page, orderFields, orderAsc, keyword, startId);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getContestLogin(final int id, final String password, final ReceivedCallback<ContentReceived> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ContentReceived result = ContestConnection.getInstance().getLogin(id, password);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012014;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getContestComment(final int page, final int contestId, final ReceivedCallback<ListReceived<ContestCommentListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ContestCommentListItem> result = ContestConnection.getInstance().getComment(page, contestId);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }


    @Override
    public void getContestStatus(int page, int contestId, ReceivedCallback<ListReceived<ContestStatusListItem>> callback) {
        getContestStatus(page, contestId, "time", callback);
    }

    @Override
    public void getContestStatus(int page, int contestID, String orderFields, ReceivedCallback<ListReceived<ContestStatusListItem>> callback) {
        getContestStatus(page, contestID, orderFields, false, callback);
    }

    @Override
    public void getContestStatus(final int page, final int contestId, final String orderFields, final boolean orderAsc, final ReceivedCallback<ListReceived<ContestStatusListItem>> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ListReceived<ContestStatusListItem> result = ContestConnection.getInstance().getStatus(page, contestId, orderFields, orderAsc);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getRankReceived(final int id, final ReceivedCallback<RankListReceived> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                RankListReceived result = ContestConnection.getInstance().getRankReceived(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void getRank(final int id, final ReceivedCallback<RankListOverview> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                RankListOverview result = ContestConnection.getInstance().getRank(id);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void submitProblemCode(final int problemId, final String codeContent, final int languageId, final ReceivedCallback<ContentReceived> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ContentReceived result = ProblemConnection.getInstance().submitProblemCode(problemId, codeContent, languageId);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void submitContestCode(final int contestId, final String codeContent, final int languageId, final ReceivedCallback<ContentReceived> callback) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ContentReceived result = ContestConnection.getInstance().submitContestCode(contestId, codeContent, languageId);
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }

    public void getRecentContest(final ReceivedCallback<List<RecentContestListItem>> callback){
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                List<RecentContestListItem> result = HomePageConnection.getInstance().getRecentContest();
                Message msg = new Message();
                Object[] obj = new Object[2];
                obj[0] = callback;
                obj[1] = result;
                msg.obj = obj;
                msg.what = 0x01012013;
                handler.sendMessage(msg);
            }
        });
    }
}
