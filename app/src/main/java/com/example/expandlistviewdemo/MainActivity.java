package com.example.expandlistviewdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by enid on 2016/3/24.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private Context mContext;
    private ExpandableListView elv;
    private CheckBox cbAll;
    private List<GroupModel> groups;
    private GroupChildAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        cbAll = (CheckBox) findViewById(R.id.cbAll);
        cbAll.setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        elv = (ExpandableListView) findViewById(R.id.elv);
        //设置ExpandListView group item 前默认的展开收起指示图标 不显示
        elv.setGroupIndicator(null);

        groups = new ArrayList<GroupModel>();
        for (int i = 0; i < 3; i++) {
            GroupModel groupModel = new GroupModel();
            groupModel.setId(i);
            groupModel.setGroupName("group" + i);
            List<ChildModel> childModels = new ArrayList<ChildModel>();
            for (int j = 0; j < 3; j++) {
                ChildModel childModel = new ChildModel();
                childModel.setId(j);
                childModel.setChildName("child" + j);
                childModels.add(childModel);
            }
            groupModel.setChilds(childModels);
            groups.add(groupModel);
        }

        adapter = new GroupChildAdapter(this,groups);
        adapter.setOnClickCheckAllListener(new GroupChildAdapter.IClickCheckAllCallback() {
            @Override
            public void checkAll(boolean checkAll) {
                cbAll.setChecked(checkAll);
            }
        });
        elv.setAdapter(adapter);
        //设置所有item展开
        for (int i = 0; i < groups.size(); i++) {
            elv.expandGroup(i);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cbAll:
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).setIsCheck(cbAll.isChecked());
                    for (int j = 0; j < groups.get(i).getChilds().size(); j++) {
                        groups.get(i).getChilds().get(j).setIsCheck(cbAll.isChecked());
                    }

                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnDelete:
                iteratorRemove();
                break;
            default:
                break;
        }
    }

    /**
     * 使用Iterator的方式也可以顺利删除和遍历
     */
    public void iteratorRemove() {
        Iterator<GroupModel> groIter = groups.iterator();
        while (groIter.hasNext()) {
            GroupModel groupModel = groIter.next();
            if (groupModel.isCheck()){
                groIter.remove();//这里要使用Iterator的remove方法移除当前对象，如果使用List的remove方法，则同样会出现ConcurrentModificationException
            }else {
                Iterator<ChildModel> childIter = groupModel.getChilds().iterator();
                while (childIter.hasNext()) {
                    ChildModel childModel = childIter.next();
                    if (childModel.isCheck()) {
                        childIter.remove();
                    }
                }
            }
        }
        adapter.updateData(groups);
        cbAll.setChecked(false);
    }
}
