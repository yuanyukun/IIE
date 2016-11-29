package cn.papaxiong.iie.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import cn.papaxiong.iie.R;
import cn.papaxiong.iie.ui.fragment.ExerciseFragment;
import cn.papaxiong.iie.ui.fragment.MainFragment;
import cn.papaxiong.iie.ui.fragment.MineFragment;
import cn.papaxiong.iie.ui.fragment.PracticeFragment;
import cn.papaxiong.iie.ui.fragment.RankingFragment;
import cn.papaxiong.iie.utils.L;

public class MainActivity extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPager        mViewPager;
    private MainFragment     mainPage;
    private ExerciseFragment exercisePage;
    private PracticeFragment practisePage;
    private RankingFragment  rankingPage;
    private MineFragment     minePage;

    private FragmentManager mFragmentManager;
    private FragmentStatePagerAdapter adapter;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);

        mainPage = new MainFragment();
        exercisePage = new ExerciseFragment();
        practisePage = new PracticeFragment();
        rankingPage  = new RankingFragment();
        minePage  = new MineFragment();
//        mFragmentManager = getSupportFragmentManager();
        mFragments = new ArrayList<>();
        mFragments.add(mainPage);
        mFragments.add(exercisePage);
        mFragments.add(practisePage);
        mFragments.add(rankingPage);
        mFragments.add(minePage);
//        adapter = new FragmentStatePagerAdapter() {
//            @Override
//            public Fragment getItem(int position) {
//                return null;
//            }
//
//            @Override
//            public int getCount() {
//                return 0;
//            }
//        }
//        adapter = new FragmentStatePagerAdapter(getgetChildFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return mFragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return mFragments.size();
//            }
//        };
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        L.i("你选择了"+(position + 1)+"页");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
