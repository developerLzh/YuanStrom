package com.lzh.yuanstrom.expandlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.R;

public class MainActivity extends Activity {
	private ArrayList<PromotionInfoBean> promList = new ArrayList<PromotionInfoBean>();
	private List<CollapsableLinearLayout> collapsablelist = new ArrayList<CollapsableLinearLayout>();
	private LayoutInflater inflater;
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_combo_info);
		
		inflater = LayoutInflater.from(this);
		
		initData();
		showPromList();
	}

	private void initData() {
		PromotionInfoBean mPromotionInfoBean1 = new PromotionInfoBean();
		mPromotionInfoBean1.PromName = "奶粉";
	
		PromProductBean mPromProductBean1 = new PromProductBean();
		mPromProductBean1.ProductName = "可瑞康";
		PromProductBean mPromProductBean2 = new PromProductBean();
		mPromProductBean2.ProductName = "启赋";
		PromProductBean mPromProductBean3 = new PromProductBean();
		mPromProductBean3.ProductName = "贝因美";
		mPromotionInfoBean1.PromProducts.add(mPromProductBean1);
		mPromotionInfoBean1.PromProducts.add(mPromProductBean2);
		mPromotionInfoBean1.PromProducts.add(mPromProductBean3);
		
		PromotionInfoBean mPromotionInfoBean2 = new PromotionInfoBean();
		mPromotionInfoBean2.PromName = "宝宝服饰";
	
		PromProductBean mPromProductBean4 = new PromProductBean();
		mPromProductBean4.ProductName = "裤子";
		PromProductBean mPromProductBean5 = new PromProductBean();
		mPromProductBean5.ProductName = "上衣";
		PromProductBean mPromProductBean6 = new PromProductBean();
		mPromProductBean6.ProductName = "长跑";
		mPromotionInfoBean2.PromProducts.add(mPromProductBean4);
		mPromotionInfoBean2.PromProducts.add(mPromProductBean5);
		mPromotionInfoBean2.PromProducts.add(mPromProductBean6);
		
		promList.add(mPromotionInfoBean1);
		promList.add(mPromotionInfoBean2);
	}
	
	private void showPromList(){
		List<View> comboViews = createContentViews(promList);
		LinearLayout comboInfoContainer = (LinearLayout) findViewById(R.id.combo_info_container_linear);
		for (View comboView : comboViews) {
			comboInfoContainer.addView(comboView);
			View dividerView = new View(this);
			dividerView.setBackgroundColor(Color.parseColor("#f8f8f8"));
			comboInfoContainer.addView(dividerView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));//Util.dp2px(this, 30)
		}
	}
	
	private List<View> createContentViews(List<PromotionInfoBean> promInfo) {
		List<View> contentViews = new ArrayList<View>();
		int realIndex = 0;
		if (promInfo != null && promInfo.size() != 0) {
			for (int i = 0; i < promInfo.size(); i++) {
				PromotionInfoBean prom = promInfo.get(i);
				if(prom.PromProducts!=null){
					if(prom.PromProducts.size() != 0){ // 不展示status为4
						View promInfoItem = createPromInfoItem(prom, realIndex);
						contentViews.add(promInfoItem);
						realIndex ++;
					}
				}
			}
		}
		return contentViews;
	}
	
	private View createPromInfoItem(PromotionInfoBean promInfo, int indexs) {
		//@formatter:off
		final View comboItem = inflater.inflate(R.layout.product_combo, null);
		RelativeLayout combo_title_container = (RelativeLayout)comboItem.findViewById(R.id.combo_title_container);
		TextView nickNameView = (TextView) comboItem.findViewById(R.id.combo_nick_name_text);
		TextView comboNumText = (TextView) comboItem.findViewById(R.id.combo_num_text_01);
		final ImageView arrowImage = (ImageView) comboItem.findViewById(R.id.combo_arrow_image);
		final CollapsableLinearLayout childContainer = (CollapsableLinearLayout)comboItem.findViewById(R.id.combo_products_container);
		childContainer.setToggleView(arrowImage);
		// create child product views
		if (promInfo.PromProducts != null && promInfo.PromProducts.size() > 0) {  
			for (PromProductBean childBean : promInfo.PromProducts) {
				View childView = createComboChildItem(childBean);			
				childContainer.addView(childView);
			}
		}
		
		collapsablelist.add(childContainer);

		if(index == indexs)
			childContainer.expand();
		else
			childContainer.collapse();
		nickNameView.setText(promInfo.PromName);
		comboNumText.setText((indexs+1)+"") ;
		combo_title_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				childContainer.toggle();
				//收起其他的view
				for(int i=0;i<collapsablelist.size();i++){
					if(!childContainer.equals(collapsablelist.get(i))){
						if(collapsablelist.get(i).isExpanded()){
							collapsablelist.get(i).collapseOther();
						}
					}
				}
			}
		});
		//@formatter:on
		return comboItem;
	}
	
	private View createComboChildItem(final PromProductBean childBean) {
		//@formatter:off
		View comboChildItem = inflater.inflate(R.layout.product_combo_child, null);
		LinearLayout product_linear = (LinearLayout) comboChildItem.findViewById(R.id.product_linear) ;
		TextView childNameView = (TextView) comboChildItem.findViewById(R.id.combo_product_name);

		childNameView.setText(childBean.ProductName);
		product_linear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(MainActivity.class.getName(), "点击商品");
			}
		});
		return comboChildItem;
	}

}
