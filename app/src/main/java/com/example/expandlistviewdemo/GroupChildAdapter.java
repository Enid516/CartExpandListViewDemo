package com.example.expandlistviewdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by enid on 2016/3/24.
 */
public class GroupChildAdapter extends BaseExpandableListAdapter{
    private Context mContext;
    private List<GroupModel> mGroups;
    private IClickCheckAllCallback clickCallback;
    public GroupChildAdapter(Context context,List<GroupModel> groups) {
        this.mContext = context;
        this.mGroups = groups;
    }

    public void setOnClickCheckAllListener(IClickCheckAllCallback clickCallback){
        this.clickCallback = clickCallback;
    }

    public void updateData(List<GroupModel> groups){
        this.mGroups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mGroups.get(i).getChilds() == null ? 0 : mGroups.get(i).getChilds().size();
    }

    @Override
    public Object getGroup(int i) {
        return mGroups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mGroups.get(i).getChilds().get(i1);
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

        GroupModel groupModel = mGroups.get(i);
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

        ChildModel childModel = mGroups.get(i).getChilds().get(i1);
        viewHolderChild.tv.setText(childModel.getChildName());
        viewHolderChild.cb.setChecked(childModel.isCheck());
        viewHolderChild.cb.setOnClickListener(new ChildCheckClickListener(viewHolderChild,i,i1));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
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
            //设置选中组的选中状态
            mGroups.get(gPosition).setIsCheck(gViewHolderGroup.cb.isChecked());
            //将该组的child的选中状态
            for (int i = 0; i < mGroups.get(gPosition).getChilds().size(); i++) {
                mGroups.get(gPosition).getChilds().get(i).setIsCheck(gViewHolderGroup.cb.isChecked());
            }
            //刷新数据
            notifyDataSetChanged();
            //通知Activity 全选是否选中
            if (clickCallback != null)
                clickCallback.checkAll(itemCheckIsCheckAll());
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
            //设置child的选中状态
            mGroups.get(gPosition).getChilds().get(cPosition).setIsCheck(cViewHolderChild.cb.isChecked());
            //设置该child所属group的选中状态
            mGroups.get(gPosition).setIsCheck(childCheckIsGroupCheck(gPosition));
            //刷新数据
            notifyDataSetChanged();
            // 通知Activity 全选是否选中
            if (clickCallback != null)
                clickCallback.checkAll(itemCheckIsCheckAll());
        }
    }

    public static interface IClickCheckAllCallback{
        void checkAll(boolean checkAll);
    }

    /**
     * item 选中改变后，判断是否全选，在child或group选中状态改变后调用此方法
     * @return
     */
    public boolean itemCheckIsCheckAll() {
        for (int i = 0; i < mGroups.size(); i++) {
            if (!mGroups.get(i).isCheck()){
                return false;
            }
            for (int j = 0; j < mGroups.get(i).getChilds().size(); j++) {
                if (!mGroups.get(i).getChilds().get(j).isCheck()) {
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
        for (int i = 0; i < mGroups.get(gPosition).getChilds().size(); i++) {
            if (!mGroups.get(gPosition).getChilds().get(i).isCheck()) {
                return false;
            }
        }
        return true;
    }
}
