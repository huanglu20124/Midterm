package com.lxc.midterm.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lxc.midterm.R;
import com.lxc.midterm.RankItemAdapter;
import com.lxc.midterm.domain.Person;
import com.lxc.midterm.tool.PersonTool;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private RankItemAdapter adapter;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private int pull_times;		//记录上拉刷新的次数
    private List<Person> mPersons = new ArrayList<>();
    //返回按钮
    private ImageView back_btn;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Message message){
        switch (message.what){
            case 0x4:{
                List<Person> list = (List<Person>) message.obj;
                mPersons.addAll(list);
                adapter.notifyDataSetChanged();
            }
            break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册监听
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_rank);
        //设置返回按钮
        back_btn = findViewById(R.id.iv_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initItems();    //初始化任务列表
        adapter = new RankItemAdapter(mPersons, this);
        adapter.setOnItemClickListener(new RankItemAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 传递序列化对象给详情页
/*                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("person", mPersons.get(position));
                startActivityForResult(intent, position);*/
            }
        });
        recyclerView = findViewById(R.id.recycler_view_rank);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout_rank);
        refreshLayout.setEnableRefresh(false);	//取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this)
                .setProgressResource(R.drawable.progress)
                .setArrowResource(R.drawable.arrow));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                PersonTool.getTenRankPerson(pull_times);
                pull_times++;
                refreshlayout.finishLoadmore();
            }
        });
    }

    private void initItems() {
        PersonTool.getTenRankPerson(0);
        pull_times = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听
        EventBus.getDefault().unregister(this);
    }
}
