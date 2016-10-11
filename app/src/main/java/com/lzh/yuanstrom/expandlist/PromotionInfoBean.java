package com.lzh.yuanstrom.expandlist;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 促销活动类型Bean
 * 
 * @author daren
 * 
 */
public class PromotionInfoBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 促销名称 */
	public String PromName;
	
	/** 促销商品集合 */
	public ArrayList<PromProductBean> PromProducts;

	public PromotionInfoBean() {
		PromProducts = new ArrayList<PromProductBean>();
	}
}
