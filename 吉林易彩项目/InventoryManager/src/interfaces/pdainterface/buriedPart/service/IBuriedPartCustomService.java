package interfaces.pdainterface.buriedPart.service;

import java.util.List;
import interfaces.pdainterface.buriedPart.pojo.BuriedPartInfoBean;

public interface IBuriedPartCustomService {
	

	/**
	 * 获取所有直埋段
	 * @param object
	 * @return
	 */
	public List<BuriedPartInfoBean> getBuriedPartGridCustom(BuriedPartInfoBean object);
	
	/**
	 * 更改直埋段信息
	 * @param obj
	 * @return
	 */
	public int updateBuriedPartCustom(BuriedPartInfoBean obj);
	
	
	/**
	 * 增加直埋段信息
	 * @param obj
	 * @return
	 */
	public int insertBuriedPartCustom(BuriedPartInfoBean obj);
	
	/**
	 * 删除直埋段
	 * @param obj
	 * @return
	 */
	public int deleteBuriedPartCustom(BuriedPartInfoBean obj);
	
	/**
	 * 设置光缆段计算长度
	 * @param obj
	 * @return
	 */
	public BuriedPartInfoBean setBuriedPartLength(BuriedPartInfoBean obj);
}
