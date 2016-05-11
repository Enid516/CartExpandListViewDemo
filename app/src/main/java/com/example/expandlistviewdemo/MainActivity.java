package com.example.expandlistviewdemo;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Context mContext;
    private BaseExpandableListAdapter adapter;
    private ExpandableListView elv;
    private CheckBox cbAll;
    private List<GroupModel> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        cbAll = (CheckBox) findViewById(R.id.cbAll);
        cbAll.setOnClickListener(this);
        elv = (ExpandableListView) findViewById(R.id.elv);
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

        adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return groups == null ? 0 : groups.size();
            }

            @Override
            public int getChildrenCount(int i) {
                return groups.get(i).getChilds() == null ? 0 : groups.get(i).getChilds().size();
            }

            @Override
            public Object getGroup(int i) {
                return groups.get(i);
            }

            @Override
            public Object getChild(int i, int i1) {
                return groups.get(i).getChilds().get(i1);
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i1;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                ViewHolderGroup viewHolderGroup = null;
                if(view == null){
                    view = LayoutInflater.from(mContext).inflate(R.layout.elv_adapter_group,null);
                    viewHolderGroup = new ViewHolderGroup();
                    viewHolderGroup.cb = (CheckBox) view.findViewById(R.id.cbGroup);
                    viewHolderGroup.tv = (TextView) view.findViewById(R.id.tvGroup);
                    view.setTag(viewHolderGroup);
                }else{
                    viewHolderGroup = (ViewHolderGroup)view.getTag();
                }

                GroupModel groupModel = groups.get(i);
                viewHolderGroup.tv.setText(groupModel.getGroupName());
                viewHolderGroup.cb.setChecked(groupModel.isCheck());
                viewHolderGroup.cb.setOnClickListener(new GroupCheckClickListener(viewHolderGroup,i));
                return view;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                ViewHolderChild viewHolderChild = null;
                if (view == null) {
                    view = LayoutInflater.from(mContext).inflate(R.layout.elv_adapter_child, null);
                    viewHolderChild = new ViewHolderChild();
                    viewHolderChild.cb = (CheckBox) view.findViewById(R.id.cbChild);
                    viewHolderChild.tv = (TextView) view.findViewById(R.id.tvChild);
                    view.setTag(viewHolderChild);
                }else{
                    viewHolderChild = (ViewHolderChild) view.getTag();
                }

                ChildModel childModel = groups.get(i).getChilds().get(i1);
                viewHolderChild.tv.setText(childModel.getChildName());
                viewHolderChild.cb.setChecked(childModel.isCheck());
                viewHolderChild.cb.setOnClickListener(new ChildCheckClickListener(viewHolderChild,i,i1));
                return view;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return false;
            }
        };

        elv.setAdapter(adapter);
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
            default:
                break;
        }
    }

    public class ViewHolderGroup{
        TextView tv;
        CheckBox cb;
    }

    public class ViewHolderChild{
        TextView tv;
        CheckBox cb;
    }

    public class GroupCheckClickListener implements View.OnClickListener {
        ViewHolderGroup gViewHolderGroup;
        int gPosition;
        public GroupCheckClickListener(ViewHolderGroup gViewHolderGroup, int gPosition) {
            this.gViewHolderGroup = gViewHolderGroup;
            this.gPosition = gPosition;
        }

        @Override
        public void onClick(View view) {
            groups.get(gPosition).setIsCheck(gViewHolderGroup.cb.isChecked());
            for (int i = 0; i < groups.get(gPosition).getChilds().size(); i++) {
                groups.get(gPosition).getChilds().get(i).setIsCheck(gViewHolderGroup.cb.isChecked());
            }
            adapter.notifyDataSetChanged();
            cbAll.setChecked(itemCheckIsCheckAll());
        }
    }

    public class ChildCheckClickListener implements View.OnClickListener{
        ViewHolderChild cViewHolderChild;
        int gPosition;
        int cPosition;

        public ChildCheckClickListener(ViewHolderChild cViewHolderChild,int gPosition, int cPosition) {
            this.cViewHolderChild = cViewHolderChild;
            this.gPosition = gPosition;
            this.cPosition = cPosition;
        }

        @Override
        public void onClick(View view) {
            groups.get(gPosition).getChilds().get(cPosition).setIsCheck(cViewHolderChild.cb.isChecked());
            groups.get(gPosition).getChilds().get(cPosition).setIsCheck(cViewHolderChild.cb.isChecked());
            groups.get(gPosition).setIsCheck(childCheckIsGroupCheck(gPosition));
            adapter.notifyDataSetChanged();
            cbAll.setChecked(itemCheckIsCheckAll());
        }
    }

    /**
     * item 选中改变后，判断是否全选，在child或group选中状态改变后调用此方法
     * @return
     */
    public boolean itemCheckIsCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            if (!groups.get(i).isCheck()){
                return false;
            }
            for (int j = 0; j < groups.get(i).getChilds().size(); j++) {
                if (!groups.get(i).getChilds().get(j).isCheck()) {
                   return false;
                }
            }
        }
        return true;
    }

    /**
     * child 选中改变后，判断其group是否选中， 在child选中状态改变后调用此方法
     * @param gPosition
     * @return
     */
    public boolean childCheckIsGroupCheck(int gPosition) {
        for (int i = 0; i < groups.get(gPosition).getChilds().size(); i++) {
            if (!groups.get(gPosition).getChilds().get(i).isCheck()) {
                return false;
            }
        }
        return true;
    }
}
