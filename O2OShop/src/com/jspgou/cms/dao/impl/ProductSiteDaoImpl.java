package com.jspgou.cms.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.jspgou.cms.dao.ProductDao;
import com.jspgou.cms.dao.ProductSiteDao;
import com.jspgou.cms.entity.Gys;
import com.jspgou.cms.entity.Product;
import com.jspgou.cms.entity.ProductSite;
import com.jspgou.cms.entity.ProductTag;
import com.jspgou.cms.lucene.LuceneProduct;
import com.jspgou.common.hibernate3.Finder;
import com.jspgou.common.hibernate3.HibernateBaseDao;
import com.jspgou.common.page.Pagination;

/**
* This class should preserve.
* @preserve
*/
@Repository
public class ProductSiteDaoImpl extends HibernateBaseDao<ProductSite, Long> implements
		ProductSiteDao {
	
	//不能动
	public Pagination getPageByCategory(Long ctgId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForCategory(ctgId, productName,brandName,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f, pageNo, pageSize);
	}
	
	public Pagination getPageByCategoryChannel(Long brandId,Long ctgId,
			Boolean isRecommend,String[] names,String[] values,//栏目页商品分页manager(wangzewu)
			Boolean isSpecial,int orderBy, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForCategoryChannel(brandId, ctgId, isRecommend,
				names,values, isSpecial,orderBy,cacheable);	
		return find(f, pageNo, pageSize);
	}
	public Pagination getPageByCategoryChannel(Long webId,Long brandId,Long ctgId,
			Boolean isRecommend,String[] names,String[] values,//栏目页商品分页manager(wangzewu)
			Boolean isSpecial,int orderBy, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForCategoryChannel(webId,brandId, ctgId, isRecommend,
				names,values, isSpecial,orderBy,cacheable);	
		return find(f, pageNo, pageSize);
	}
	
	public Pagination getPageByStockWarning(Long webId,Integer count,
			Boolean status,int pageNo, int pageSize){
		Finder f = Finder.create("select bean from Product bean");
		f.append(" where bean.website.id=:webId").setParam("webId", webId);
		f.append(" and bean.stockCount <=:count").setParam("count", count);
		f.append(" and bean.lackRemind=:status").setParam("status", status);
		f.append(" order by bean.stockCount asc");
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")//返回历史记录(wang ze wus)
	public List<ProductSite> getHistoryProductSite(HashSet<Long> set,Integer count){
		if(set.size()>0){
			return this.getSession().createQuery("from ProductSite bean where bean.id in (:ids)")
			.setParameterList("ids", set).setMaxResults(count).list();
		}else{
			return new ArrayList();
		}
	}
	@SuppressWarnings("unchecked")
	public List<ProductSite> getChildListByIdAndSiteId(Long id,Long siteId){
		ProductSite bean=this.findById(id);
			return this.getSession().createQuery("from ProductSite bean where bean.product.parent.id in("+bean.getProduct().getId()+")  and bean.website.id=:siteId ")
					.setParameter("siteId", siteId).list();
		
	}

	//不能动
	public Pagination getPageByWebsite(Long webId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForWebsite(webId, productName,brandName,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f, pageNo, pageSize);
	}

	public Pagination getPageByTag(Long tagId, Long ctgId, Boolean isRecommend,
			Boolean isSpecial, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForTag(ctgId, tagId, isRecommend, isSpecial,
				cacheable);
		return find(f, pageNo, pageSize);
	}
	public Pagination getPageByTag(String productName,String area,Long tagId, Long ctgId, Boolean isRecommend,Boolean isSpecial, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinderForTag(productName,area,ctgId, tagId, isRecommend, isSpecial,
				cacheable);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByCategory(Long ctgId, Boolean isRecommend,
			Boolean isSpecial, int firstResult, int maxResults,
			boolean cacheable) {
		Finder f = getFinderForCategory(ctgId, null,null,true,isRecommend, isSpecial,
				null,null,null,null,null,null,null,cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);
	}
	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByCategory(Long webId,Long ctgId, Boolean isRecommend,
			Boolean isSpecial, int firstResult, int maxResults,
			boolean cacheable) {
		Finder f = getFinderForCategory(webId,ctgId, null,null,true,isRecommend, isSpecial,
				null,null,null,null,null,null,null,cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByWebsite(Long webId, Boolean isRecommend,
			Boolean isSpecial, int firstResult, int maxResults,
			boolean cacheable) {
		Finder f = getFinderForWebsite(webId,  null,null,null,isRecommend, isSpecial, 
				null,null,null,null,null,null,null,cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);                                                                                                                                                    
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByWebsite1(Long webId, Boolean isRecommend,
			Boolean isSpecial,Boolean isHostSale,Boolean isNewProduct, int firstResult, int maxResults,
			boolean cacheable) {
		Finder f = getFinderForWebsite1(webId, isRecommend, isSpecial,isHostSale,isNewProduct, cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByTag(Long tagId, Long ctgId,
			Boolean isRecommend, Boolean isSpecial, int firstResult,
			int maxResults, boolean cacheable) {
		Finder f = getFinderForTag(tagId, ctgId, isRecommend, isSpecial,cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);
	}
	@SuppressWarnings("unchecked")
	public List<ProductSite> getListByTag(Long webId,Long tagId, Long ctgId,
			Boolean isRecommend, Boolean isSpecial, int firstResult,
			int maxResults, boolean cacheable) {
		Finder f = getFinderForTag(webId,tagId, ctgId, isRecommend, isSpecial,cacheable);
		f.setFirstResult(firstResult);
		f.setMaxResults(maxResults);
		return find(f);
	}
	
	//不能动
	private Finder getFinderForCategory(Long ctgId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock, boolean cacheable) {
		Finder f = Finder.create("select bean from Product bean");
		f.append(" inner join bean.category node,Category parent");
		f.append(" where node.lft between parent.lft and parent.rgt");
		f.append(" and parent.id=:ctgId");
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("ctgId", ctgId);
		f.setCacheable(cacheable);
		return f;
	}
	//不能动
	private Finder getFinderForCategory(Long webId,Long ctgId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock, boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" inner join bean.product.category node,Category parent");
		f.append(" where node.lft between parent.lft and parent.rgt");
		f.append(" and parent.id=:ctgId");
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.product.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (webId != null) {
			f.append(" and bean.website.id=:webId");
			f.setParam("webId", webId);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("ctgId", ctgId);
		f.setCacheable(cacheable);
		return f;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSite> getList(Long typeId,Long brandId,String productName,boolean cacheable) {
		Finder f = Finder.create("from Product bean where 1=1");
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (brandId != null) {
			f.append(" and bean.brand.id=:brandId");
			f.setParam("brandId",brandId);
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		f.setCacheable(cacheable);
		return find(f);
	}
	
	public Pagination getPage(int orderBy,int pageNo, int pageSize, boolean cacheable) {
		Finder f = Finder.create("from Product bean where 1=1");
		appendOrder(f, orderBy);
		f.setCacheable(cacheable);
		return find(f, pageNo, pageSize);
	}
	
	
	
	
	private Finder getFinderForCategoryChannel(Long brandId,Long ctgId, Boolean isRecommend,
		    String[] names,String[] values,Boolean isSpecial,int orderBy, boolean cacheable) {
		Finder f = Finder.create("select distinct bean from ProductSite bean");
		f.append(" join bean.product.category node,Category parent");
		String[] exendeds = null;
		if(names!=null){
			exendeds=new String[names.length];
	    	for(int i=0,j=names.length;i<j;i++){
	    		 if(!values[i].equals("0")){
	    		String exended="exended"+i;
	    		exendeds[i]=exended;
	    		f.append(" inner join bean.product.exendeds "+exended);
	    		 }
	    	}
		}
//		f.append(" where node.lft between parent.lft and parent.rgt and bean.onSale=true");
		f.append(" where node.lft between parent.lft and parent.rgt ");
		f.append(" and parent.id=:ctgId");
		f.setParam("ctgId", ctgId);
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" and bean.onSale=:isOnSale");
		f.setParam("isOnSale", true);
		if(brandId!=null){//品牌
			f.append(" and bean.product.brand.id=:id");
			f.setParam("id", brandId);
		}
		if(names!=null){
	    	for(int i=0;i<names.length;i++){
	    	  String an="attr_name"+i;
	    	  String av="attr_value"+i;
			  if(!values[i].equals("0")){
			   f.append(" and "+exendeds[i]+".name=:"+an).setParam(an, names[i]);
		   	   f.append(" and "+exendeds[i]+".value=:"+av).setParam(av,values[i]);
		 	  }
		  }
		}
		appendOrder(f, orderBy);
		f.setCacheable(cacheable);
		return f;
	}
	private Finder getFinderForCategoryChannel(Long webId,String productName,String area,Long brandId,Long ctgId, Boolean isRecommend,
			String[] names,String[] values,Boolean isSpecial,int orderBy, boolean cacheable) {
		Finder f = Finder.create("select distinct bean from ProductSite bean");
		f.append(" join bean.product.category node,Category parent");
		String[] exendeds = null;
		if(names!=null){
			exendeds=new String[names.length];
			for(int i=0,j=names.length;i<j;i++){
				if(!values[i].equals("0")){
					String exended="exended"+i;
					exendeds[i]=exended;
					f.append(" inner join bean.product.exendeds "+exended);
				}
			}
		}
//		f.append(" where node.lft between parent.lft and parent.rgt and bean.onSale=true");
		f.append(" where node.lft between parent.lft and parent.rgt ");
		f.append(" and parent.id=:ctgId");
		f.setParam("ctgId", ctgId);
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if(!StringUtils.isBlank(area)){
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
			f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
					+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" and bean.onSale=:isOnSale");
		f.setParam("isOnSale", true);
		if(brandId!=null){//品牌
			f.append(" and bean.product.brand.id=:id");
			f.setParam("id", brandId);
		}
		if (webId != null) {
			f.append(" and bean.website.id=:webId");
			f.setParam("webId", webId);
		}
		if(productName!=null&&productName.length()>0){//品牌
			f.append(" and bean.product.productName like'%"+productName+"%'");
		}
		if(names!=null){
			for(int i=0;i<names.length;i++){
				String an="attr_name"+i;
				String av="attr_value"+i;
				if(!values[i].equals("0")){
					f.append(" and "+exendeds[i]+".name=:"+an).setParam(an, names[i]);
					f.append(" and "+exendeds[i]+".value=:"+av).setParam(av,values[i]);
				}
			}
		}
		appendOrder(f, orderBy);
		f.setCacheable(cacheable);
		return f;
	}
	
	private Finder getFinderForCategoryChannel(Long webId,Long brandId,Long ctgId, Boolean isRecommend,
			String[] names,String[] values,Boolean isSpecial,int orderBy, boolean cacheable) {
		Finder f = Finder.create("select distinct bean from ProductSite bean");
		f.append(" join bean.product.category node,Category parent");
		String[] exendeds = null;
		if(names!=null){
			exendeds=new String[names.length];
			for(int i=0,j=names.length;i<j;i++){
				if(!values[i].equals("0")){
					String exended="exended"+i;
					exendeds[i]=exended;
					f.append(" inner join bean.product.exendeds "+exended);
				}
			}
		}
//		f.append(" where node.lft between parent.lft and parent.rgt and bean.onSale=true");
		f.append(" where node.lft between parent.lft and parent.rgt and bean.onSale= "+true);
		f.append(" and parent.id=:ctgId");
		f.setParam("ctgId", ctgId);
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if (webId != null) {
			f.append(" and bean.website.id=:webId");
			f.setParam("webId", webId);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" and bean.onSale=:isOnSale");
		f.setParam("isOnSale", true);
		if(brandId!=null){//品牌
			f.append(" and bean.product.brand.id=:id");
			f.setParam("id", brandId);
		}
		if(names!=null){
			for(int i=0;i<names.length;i++){
				String an="attr_name"+i;
				String av="attr_value"+i;
				if(!values[i].equals("0")){
					f.append(" and "+exendeds[i]+".name=:"+an).setParam(an, names[i]);
					f.append(" and "+exendeds[i]+".value=:"+av).setParam(av,values[i]);
				}
			}
		}
		appendOrder(f, orderBy);
		f.setCacheable(cacheable);
		return f;
	}
	
	
	private void appendOrder(Finder f, int orderBy) {
		switch (orderBy) {
		case 1:
			// ID升序
			f.append(" order by bean.id asc");
			break;
		case 2:
			// 上架时间降序
			f.append(" order by bean.createTime desc");
			break;
		case 3:
			// 上架时间升序
			f.append(" order by bean.createTime asc");
			break;
		case 4:
			//销售量降序、发布时间降序
			f.append(" order by bean.product.saleCount desc, bean.createTime desc");
			break;
		case 5:
			// 销售量降序、发布时间升序
			f.append(" order by bean.product.saleCount asc, bean.createTime asc");
			break;
		case 6:
			// 销售价降序
			f.append(" order by bean.salePrice desc, bean.id desc");
			break;
		case 7:
			// 销售价升序
			f.append(" order by bean.salePrice asc,bean.id desc");
			break;
		case 8:
			//利润降序
			f.append(" order by bean.liRun desc");
			break;
		case 9:
			//访问
			f.append(" order by bean.viewCount desc");
			break;
		default:
			// 默认： ID降序
			f.append(" order by bean.id desc");
		}
	}
	
	//不能动
	private Finder getFinderForWebsite(Long webId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from Product bean");
		f.append(" where bean.website.id=:webId");
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
	//不能动
	private Finder getFinderForWebsite(String area,Long webId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" where bean.website.id=:webId");
		if(!StringUtils.isBlank(area)){
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
			f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
					+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like '%"+productName+"%'");
//			f.setParam("productName", "");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.product.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		if (area != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
	//不能动
	private Finder getFinderForWebsite(String area,Long webId, String productName,Long brandId,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" where bean.website.id=:webId");
		if(!StringUtils.isBlank(area)){
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
				f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
						+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like '%"+productName+"%'");
//			f.setParam("productName", "");
		}
		if (brandId!=null && brandId>0) {
			f.append(" and bean.product.brand.id=:brandId");
			f.setParam("brandId", brandId);
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		if ( !StringUtils.isBlank(area)) {
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
			f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
					+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
	//不能动
	private Finder getFinderForWebsite(int orderBy,String area,Long webId, String productName,Long brandId,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" where bean.website.id=:webId");
		if(!StringUtils.isBlank(area)){
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
				f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
						+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like '%"+productName+"%'");
//			f.setParam("productName", "");
		}
		if (brandId!=null && brandId>0) {
			f.append(" and bean.product.brand.id=:brandId");
			f.setParam("brandId", brandId);
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		if ( !StringUtils.isBlank(area)) {
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
				f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
						+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		f.setParam("webId", webId);
		appendOrder(f, orderBy);
		f.setCacheable(cacheable);
		return f;
	}
	
	//不能动
	private Finder getFinder(Long webId, Long ctgId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		if (ctgId != null) {
			f.append(" join bean.product.category node,Category parent");
			
		}
		f.append(" where bean.website.id=:webId");
		if (ctgId != null) {
			f.append(" and node.lft between parent.lft and parent.rgt ");
			f.append(" and parent.id=:ctgId");
			f.setParam("ctgId", ctgId);
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.product.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
	//不能动
	private Finder getFinderNotJy(Gys gys,Long webId, Long ctgId, String productName,String brandName,
			Boolean isOnSale,Boolean isRecommend,Boolean isSpecial,Boolean isHotsale,
			Boolean isNewProduct,Long typeId,Double startSalePrice,Double endSalePrice,
			Integer startStock,Integer endStock,boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" where bean.website.id=:webId and bean.id not in(select jysp.productSite.id from Jysp  jysp where jysp.gys.id='"+gys.getId()+"')");
		if (ctgId != null) {
			f.append(" and bean.product.category.id=:ctgId");
			f.setParam("ctgId", ctgId);
		}
		if (!StringUtils.isBlank(productName)) {
			f.append(" and bean.product.name like:productName");
			f.setParam("productName", "%"+productName+"%");
		}
		if (!StringUtils.isBlank(brandName)) {
			f.append(" and bean.product.brand.name like:brandName");
			f.setParam("brandName", "%"+brandName+"%");
		}
		if (isOnSale != null) {
			f.append(" and bean.onSale=:isOnSale");
			f.setParam("isOnSale", isOnSale);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:isRecommend");
			f.setParam("isRecommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:isSpecial");
			f.setParam("isSpecial", isSpecial);
		}
		if (isHotsale != null) {
			f.append(" and bean.hotsale=:isHotsale");
			f.setParam("isHotsale", isHotsale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:isNewProduct");
			f.setParam("isNewProduct", isNewProduct);
		}
		if (typeId != null) {
			f.append(" and bean.product.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (startSalePrice != null) {
			f.append(" and bean.salePrice>:startSalePrice");
			f.setParam("startSalePrice", startSalePrice);
		}
		if (endSalePrice != null) {
			f.append(" and bean.salePrice<:endSalePrice");
			f.setParam("endSalePrice", endSalePrice);
		}
		if (startStock != null) {
			f.append(" and bean.stockCount>:startStock");
			f.setParam("startStock", startStock);
		}
		if (endStock != null) {
			f.append(" and bean.stockCount<:endStock");
			f.setParam("endStock", endStock);
		}
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSite> getIsRecommend(Boolean isRecommend,int count){
		Finder f=Finder.create("from ProductSite bean where bean.recommend=:recommend").setParam("recommend", isRecommend);
		f.setMaxResults(count);
		return this.find(f);
	}
	@SuppressWarnings("unchecked")
	public List<ProductSite> getIsRecommend(Boolean isRecommend,int count,Long webId){
		Finder f=Finder.create("from ProductSite bean where bean.recommend=:recommend").setParam("recommend", isRecommend).append(" and bean.website.id=:webId").setParam("webId", webId);
		f.setMaxResults(count);
		return this.find(f);
	}
	public List<ProductSite> getListBySiteId(Long siteId){
		Finder f = Finder.create("from ProductSite bean where 1=1");
		f.append(" where bean.website.id=:webId ");
		if (siteId != null) {
			f.append(" and bean.website.id=:siteId");
			f.setParam("siteId", siteId);
		}
		f.append(" and bean.onSale=:isOnSale");
		f.setParam("isOnSale", true);
		f.append(" order by bean.id desc");
		return find(f);
	}
	private Finder getFinderForWebsite1(Long webId, Boolean isRecommend,
			Boolean isSpecial,Boolean isHostSale,Boolean isNewProduct, boolean cacheable) {
		Finder f = Finder.create("from ProductSite bean");
		f.append(" where bean.website.id=:webId ");
//		f.append(" where bean.website.id=:webId and bean.onSale=true");
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		if (isHostSale != null) {
			f.append(" and bean.hotsale=:hotsale");
			f.setParam("hotsale", isHostSale);
		}
		if (isNewProduct != null) {
			f.append(" and bean.newProduct=:newProduct");
			f.setParam("newProduct", isNewProduct);
		}
		f.append(" and bean.onSale=:isOnSale");
		f.setParam("isOnSale", true);
		f.append(" order by bean.id desc");
		f.setParam("webId", webId);
		f.setCacheable(cacheable);
		return f;
	}
   //商品标签:tagId,产品类别：ctgid
	private Finder getFinderForTag(Long tagId, Long ctgId, Boolean isRecommend,
			Boolean isSpecial, boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" inner join bean.tags tag with tag.id=:tagId");
//		f.append(" where bean.onSale=true");
		f.append(" where 1=1");
		f.setParam("tagId", tagId);
		if (ctgId != null) {
			f.append(" and bean.category.id=:ctgId");
			f.setParam("ctgId", ctgId);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" order by bean.id desc");
		f.setCacheable(cacheable);
		return f;
	}
	//商品标签:tagId,产品类别：ctgid
	private Finder getFinderForTag(String productName,String area,Long tagId, Long ctgId, Boolean isRecommend,
			Boolean isSpecial, boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" inner join bean.product.tags tag with tag.id=:tagId");
//		f.append(" where bean.onSale=true");
		f.append(" where 1=1");
		f.setParam("tagId", tagId);
		if(!StringUtils.isBlank(area)){
			String[] areas=area.split(",");
			if(areas!=null&&areas.length==4){
			f.append(" and bean.product.id in(select goodsnum.product.id from JxcBldGoodsNum goodsnum , Psqy psqy where "
					+ "goodsnum.ketaUser.id=psqy.bld.ketaUser.id and goodsnum.goodsNum>0 and psqy.province.id= "+areas[0]+" and psqy.city.id= "+areas[1]+" and psqy.country.id= "+areas[2]+" and psqy.street.id= "+areas[3]+" )");
			}
		}
		if (ctgId != null) {
			f.append(" and bean.product.category.id=:ctgId");
			f.setParam("ctgId", ctgId);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if (productName != null) {
			f.append(" and bean.product.name like'%"+productName+"%'");
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" order by bean.id desc");
		f.setCacheable(cacheable);
		return f;
	}
	//商品标签:tagId,产品类别：ctgid
	private Finder getFinderForTag(Long webId,Long tagId, Long ctgId, Boolean isRecommend,
			Boolean isSpecial, boolean cacheable) {
		Finder f = Finder.create("select bean from ProductSite bean");
		f.append(" inner join bean.product.tags tag with tag.id=:tagId");
		f.append(" where bean.onSale=true");
		f.setParam("tagId", tagId);
		if (webId != null) {
			f.append(" and bean.website.id=:webId");
			f.setParam("webId", webId);
		}
		if (ctgId != null) {
			f.append(" and bean.product.category.id=:ctgId");
			f.setParam("ctgId", ctgId);
		}
		if (isRecommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", isRecommend);
		}
		if (isSpecial != null) {
			f.append(" and bean.special=:special");
			f.setParam("special", isSpecial);
		}
		f.append(" order by bean.id desc");
		f.setCacheable(cacheable);
		return f;
	}

	public int luceneWriteIndex(IndexWriter writer, Long webId, Date start,
			Date end) throws CorruptIndexException, IOException {
		Session session = getSession();
		Finder f = Finder.create("from ProductSite bean where 1=1");
		if (webId != null) {
			f.append(" and bean.website.id=:webId");
			f.setParam("webId", webId);
		}
		if (start != null) {
			f.append(" and bean.createTime >= :start");
			f.setParam("start", start);
		}
		if (end != null) {
			f.append(" and bean.createTime >= :end");
			f.setParam("end", end);
		}

		ScrollableResults products = f.createQuery(session).setCacheMode(
				CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
		ProductSite productSite;
		int count = 0;
		while (products.next()) {
			productSite = (ProductSite) products.get(0);
			writer.addDocument(LuceneProduct.createDocument(productSite));
			if (++count % 20 == 0) {
				session.clear();
			}
		}
		return count;
	}

	public int deleteTagAssociation(ProductTag[] tags) {
		Long[] tagIds = new Long[tags.length];
		for (int i = 0, len = tags.length; i < len; i++) {
			tagIds[i] = tags[i].getId();
		}
		Session session = getSession();
		String hql = "from Product bean inner join bean.tags tag where tag.id in (:tagIds)";
		ScrollableResults products = session.createQuery(hql).setParameterList(
				"tagIds", tagIds).setCacheMode(CacheMode.IGNORE).scroll(
				ScrollMode.FORWARD_ONLY);
		int count = 0;
		while (products.next()) {
			Product product = (Product) products.get(0);
			product.getTags().removeAll(Arrays.asList(tags));
			if (++count % 20 == 0) {
				session.flush();
				session.clear();
			}
		}
		return count;
	}
	
	public Integer[] getProductSiteByTag(Long webId){//新品，特价，热卖(wang ze wu)
		Long newProduct=(Long)this.getSession().createQuery("select count(*) from ProductSite bean where bean.website.id=:webId  and bean.newProduct=true")
		.setParameter("webId", webId).uniqueResult();
		Long hotProduct=(Long)this.getSession().createQuery("select count(*) from ProductSite bean where bean.website.id=:webId  and bean.hotsale=true")
		.setParameter("webId", webId).uniqueResult();
		Long speProduct=(Long)this.getSession().createQuery("select count(*) from ProductSite bean where bean.website.id=:webId   and bean.special=true")
		.setParameter("webId", webId).uniqueResult();
		Integer[] p = new Integer[3];
		p[0] = newProduct.intValue();
		p[1] = hotProduct.intValue();
		p[2] = speProduct.intValue();
		return p;
	}
	public ProductSite getProductSiteByProductIdAndWebId(Long productId,Long webId){//
		
		return (ProductSite)this.getSession().createQuery(" from ProductSite bean where bean.website.id=:webId  and bean.product.id=:productId")
				.setParameter("webId", webId).setParameter("productId", productId).uniqueResult();
	}

	public ProductSite findById(Long id) {
		ProductSite entity = get(id);
		return entity;
	}

	public ProductSite save(ProductSite bean) {
		getSession().save(bean);
		return bean;
	}

	public ProductSite deleteById(Long id) {
		ProductSite entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<ProductSite> getEntityClass() {
		return ProductSite.class;
	}

	@Override
	public Pagination getPage(Long webId, Long ctgId, String productName,
			String brandName, Boolean isOnSale, Boolean isRecommend,
			Boolean isSpecial, Boolean isHotsale, Boolean isNewProduct,
			Long typeId, Double startSalePrice, Double endSalePrice,
			Integer startStock, Integer endStock, int pageNo, int pageSize,
			boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinder(webId,ctgId, productName,brandName,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f, pageNo, pageSize);
	}
	@Override
	public Pagination getPageNotJy(Gys gys,Long webId, Long ctgId, String productName,
			String brandName, Boolean isOnSale, Boolean isRecommend,
			Boolean isSpecial, Boolean isHotsale, Boolean isNewProduct,
			Long typeId, Double startSalePrice, Double endSalePrice,
			Integer startStock, Integer endStock, int pageNo, int pageSize,
			boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinderNotJy(gys,webId,ctgId, productName,brandName,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f, pageNo, pageSize);
	}

	@Override
	public Pagination getPageByWebsite(String area, Long webId,
			String productName, String brandName, Boolean isOnSale,
			Boolean isRecommend, Boolean isSpecial, Boolean isHotsale,
			Boolean isNewProduct, Long typeId, Double startSalePrice,
			Double endSalePrice, Integer startStock, Integer endStock,
			int pageNo, int pageSize, boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinderForWebsite(area,webId, productName,brandName,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f,pageNo,pageSize);
	}
	@Override
	public Pagination getPageByWebsite(String area, Long webId,
			String productName, Long brandId, Boolean isOnSale,
			Boolean isRecommend, Boolean isSpecial, Boolean isHotsale,
			Boolean isNewProduct, Long typeId, Double startSalePrice,
			Double endSalePrice, Integer startStock, Integer endStock,
			int pageNo, int pageSize, boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinderForWebsite(area,webId, productName,brandId,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f,pageNo,pageSize);
	}
	@Override
	public Pagination getPageByWebsite(int orderBy,String area, Long webId,
			String productName, Long brandId, Boolean isOnSale,
			Boolean isRecommend, Boolean isSpecial, Boolean isHotsale,
			Boolean isNewProduct, Long typeId, Double startSalePrice,
			Double endSalePrice, Integer startStock, Integer endStock,
			int pageNo, int pageSize, boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinderForWebsite(orderBy,area,webId, productName,brandId,isOnSale,
				isRecommend, isSpecial,isHotsale,isNewProduct,typeId,startSalePrice,endSalePrice,
				startStock,endStock,cacheable);
		return find(f,pageNo,pageSize);
	}


	@Override
	public Pagination getPageByCategoryChannel(String productName,String area, Long webId,
			Long brandId, Long ctgId, Boolean isRecommend, String[] names,
			String[] values, Boolean isSpecial, int orderBy, int pageNo,
			int pageSize, boolean cacheable) {
		// TODO Auto-generated method stub
		Finder f = getFinderForCategoryChannel(webId,productName,area,brandId, ctgId, isRecommend,
				names,values, isSpecial,orderBy,cacheable);	
		return find(f, pageNo, pageSize);
	}



}