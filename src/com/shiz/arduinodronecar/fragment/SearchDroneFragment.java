package com.shiz.arduinodronecar.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shiz.arduinodronecar.MainActivity;
import com.shiz.arduinodronecar.R;
import com.shiz.arduinodronecar.view.SlidingTabLayout;

public class SearchDroneFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_SECTION_COLOR = "color";

	public static final String TAG = SearchDroneFragment.class.getSimpleName();
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private SlidingTabLayout mSlidingTabLayout;
	private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

	/**
	 * This class represents a tab to be displayed by {@link ViewPager} and it's
	 * associated {@link SlidingTabLayout}.
	 */
	static class SamplePagerItem {
		private final CharSequence mTitle;
		private final int mIndicatorColor;
		private final int mDividerColor;

		SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
			mTitle = title;
			mIndicatorColor = indicatorColor;
			mDividerColor = dividerColor;
		}

		/**
		 * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
		 */
//		Fragment createFragment() {
//			return SearchDroneFragment.newInstance(mTitle, mIndicatorColor,
//					mDividerColor);
//		}

		/**
		 * @return the title which represents this tab. In this sample this is
		 *         used directly by
		 *         {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
		 */
		CharSequence getTitle() {
			return mTitle;
		}

		/**
		 * @return the color to be used for indicator on the
		 *         {@link SlidingTabLayout}
		 */
		int getIndicatorColor() {
			return mIndicatorColor;
		}

		/**
		 * @return the color to be used for right divider on the
		 *         {@link SlidingTabLayout}
		 */
		int getDividerColor() {
			return mDividerColor;
		}
	}

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SearchDroneFragment newInstance(int sectionNumber) {
		Log.d(TAG, "SearchDroneFragment" + sectionNumber);

		SearchDroneFragment fragment = new SearchDroneFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		Log.d(TAG, "SearchDroneFragment setArguments");
		fragment.setArguments(args);
		return fragment;
	}

	public SearchDroneFragment() {
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onAttach(getActivity());
		setRetainInstance(true);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Populate our tab list with tabs. Each item contains a title,
		 * indicator color and divider color, which are used by
		 * {@link SlidingTabLayout}.
		 */
		mTabs.add(new SamplePagerItem(getString(R.string.tab_connect), // Title
				Color.WHITE, // Indicator color
				getResources().getColor(R.color.actionbar_background)// Divider color
		));

		mTabs.add(new SamplePagerItem(getString(R.string.tab_stat), // Title
				Color.WHITE, // Indicator color
				getResources().getColor(R.color.actionbar_background) // Divider color
		));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_search_drone, container, false);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mSlidingTabLayout = (SlidingTabLayout) view
				.findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setViewPager(mViewPager);
		// Set a TabColorizer to customize the indicator and divider colors.
		// Here we just retrieve
		// the tab at the position, and return it's set color
		mSlidingTabLayout
				.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

					@Override
					public int getIndicatorColor(int position) {
						return mTabs.get(position).getIndicatorColor();
					}

					@Override
					public int getDividerColor(int position) {
						return mTabs.get(position).getDividerColor();
					}

				});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position + 1) {
			case 1:
				Fragment fragment = new ConnectingServerFragment();
				Bundle args = new Bundle();
				args.putInt(ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;

			case 2:
				Fragment fragment2 = new StatFragment();
				Bundle args2 = new Bundle();
				args2.putInt(ARG_SECTION_NUMBER,
						position + 1);
				fragment2.setArguments(args2);
				return fragment2;
			}
			return null;
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.tab_connect).toUpperCase(l);
			case 1:
				return getString(R.string.tab_stat).toUpperCase(l);
			}
			return null;
		}
	}
}
