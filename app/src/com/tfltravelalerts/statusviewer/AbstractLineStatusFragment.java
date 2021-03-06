
package com.tfltravelalerts.statusviewer;

import com.actionbarsherlock.view.MenuItem;
import com.tfltravelalerts.R;
import com.tfltravelalerts.common.CheatSheet;
import com.tfltravelalerts.common.DateStrings;
import com.tfltravelalerts.common.eventbus.EventBusFragment;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Date;

public abstract class AbstractLineStatusFragment extends EventBusFragment {

    private TextView mLastUpdateTime;
    protected ListView mListView;

    protected LineStatusListAdapter mAdapter;
    protected View mRefreshIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.line_status_viewer_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        setupListView();
    }

    private void findViews() {
        mLastUpdateTime = (TextView) getView().findViewById(R.id.update_time);
        mListView = (ListView) getView().findViewById(R.id.status_viewer_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        Object tag = mLastUpdateTime.getTag();
        if (tag != null) {
            Date date = (Date) tag;
            updateTimestamp(date);
        }
    }

    abstract protected void setupListView();

    public void setupRefreshIcon(MenuItem refresh) {
        refresh.setActionView(R.layout.refresh_icon);
        View actionView = refresh.getActionView();
        mRefreshIcon = actionView.findViewById(R.id.refresh_icon);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mRefreshIcon.setTag(anim);
        actionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLineStatus();
            }
        });
        CheatSheet.setup(actionView, R.string.action_refresh);
    }

    abstract protected void updateLineStatus();

    protected void updateTimestamp(Date date) {
        String dateFormat = DateStrings.getElapsedTimeForStatus(getSupportApplication(), date.getTime());
        String updateTime = getString(R.string.last_update_time, dateFormat);
        mLastUpdateTime.setText(updateTime);
        mLastUpdateTime.setTag(date);
    }
}
