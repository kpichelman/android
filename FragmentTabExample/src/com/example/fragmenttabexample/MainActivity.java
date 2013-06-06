package com.example.fragmenttabexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
 
public class MainActivity extends FragmentActivity {
 
    FragmentTabHost mTabHost;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create the tab host
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_container);
        
        // Basic tab with a basic tab 
        mTabHost.addTab(mTabHost.newTabSpec("Tab Spec 1").setIndicator("Tab Spec 1"), BasicTabFrag.class, null);
 
        // Tabs that pass arguments as bundles
        final Bundle b1 = new Bundle();
        b1.putString("name", "Tag 1");
        mTabHost.addTab(mTabHost.newTabSpec("Tag 1").setIndicator("Tab 1"), TabRoot.class, b1);
 
        final Bundle b2 = new Bundle();
        b2.putString("name", "Tag 2");
        mTabHost.addTab(mTabHost.newTabSpec("Tag 2") .setIndicator("Tag 2"), TabRoot.class, b2);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
        if (f != null && f instanceof TabRoot) {
            TabRoot tabChild = (TabRoot) f;
            if (tabChild.onBackPressed()) return;
        }
        super.onBackPressed();
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
    
    public static class BasicTabFrag extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            return inflater.inflate(R.layout.tab_basic, container, false);
        }
    }
 
    public static class TabRoot extends Fragment implements OnClickListener {
 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            return inflater.inflate(R.layout.tab_root, container, false);
        }
 
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState == null) {
                getChildFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.fragment_container, createNewChild())
                        .commit();
            }
        }

        @Override
        public void onClick(View v) {
            getChildFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, createNewChild())
                    .commit();
        }

        public boolean onBackPressed() {
            FragmentManager fm = getChildFragmentManager();
            if (fm.getBackStackEntryCount() == 1) {
                return false;
            } else {
                fm.popBackStack();
                return true;
            }
        }

        Fragment createNewChild() {
            FragmentManager fm = getChildFragmentManager();
            Bundle args = getArguments();
            if (args == null) {
                args = new Bundle();
                args.putString("name", "Name unknown");
            } else {
                args = new Bundle(args);
            }
            args.putInt(TabChild.ARGUMENT_CHILD_COUNT, fm.getBackStackEntryCount() + 1);
 
            Fragment f = new TabChild();
            f.setArguments(args);
            return f;
        }
    }

    public static class TabChild extends Fragment {
 
        private static final String ARGUMENT_CHILD_COUNT = "child_count";
 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
 
            View v = inflater.inflate(R.layout.tab_child, container, false);
            Bundle args = getArguments();
            if (args != null
                    && args.containsKey("name")
                    && args.containsKey(ARGUMENT_CHILD_COUNT)) {
                String text = args.getString("name") + "__" + args.getInt(ARGUMENT_CHILD_COUNT);
                Button button = (Button) v.findViewById(R.id.button);
                button.setText(text);
 
                Fragment f = getParentFragment();
                if (f instanceof OnClickListener) {
                    button.setOnClickListener((OnClickListener) f);
                }
            }
            return v;
        }
    }
}