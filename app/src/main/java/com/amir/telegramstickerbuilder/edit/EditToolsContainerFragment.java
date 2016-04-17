//package com.amir.telegramstickerbuilder.edit;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.amir.telegramstickerbuilder.R;
//import com.amir.telegramstickerbuilder.base.BaseFragment;
//import com.amir.telegramstickerbuilder.fonts.EnglishFontsFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EditToolsContainerFragment extends BaseFragment {
//    FirstEditFragment firstEditFragment;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View mainView = inflater.inflate(R.layout.fragment_edit_tools_container, container, false);
//
//        ViewPager viewPager = (ViewPager) mainView.findViewById(R.id.edit_tools_container_view_pager);
//        setupViewPager(viewPager);
//
//        TabLayout tabLayout = (TabLayout) mainView.findViewById(R.id.edit_tools_container_tabs);
//
////        tabLayout.addView(getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_buttons,null,false));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.setupWithViewPager(viewPager);
//
//        return mainView;
//    }
//
//    private void setupViewPager(ViewPager viewPager) {
////        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
//        firstEditFragment = new FirstEditFragment();
//        adapter.addFragment(firstEditFragment, "TWO");
//        adapter.addFragment(new EnglishFontsFragment(), "One");
////        adapter.addFragment(new ThreeFragment(), "THREE");
//        viewPager.setAdapter(adapter);
//    }
//
//    public void deactivateButtons(boolean deactivate){
//        firstEditFragment.deactivateButtons(deactivate);
//    }
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<BaseFragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public BaseFragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(BaseFragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//
//    }
//
//}
