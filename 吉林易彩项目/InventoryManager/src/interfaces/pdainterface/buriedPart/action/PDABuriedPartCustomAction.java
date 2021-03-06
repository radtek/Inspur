package interfaces.pdainterface.buriedPart.action;

import interfaces.pdainterface.buriedPart.pojo.BuriedPartInfoBean;
import interfaces.pdainterface.buriedPart.service.IBuriedPartCustomService;
import java.util.List;
import org.apache.log4j.Logger;
import base.util.TextUtil;
import base.web.InterfaceAction;

public class PDABuriedPartCustomAction extends InterfaceAction {
	private static final long serialVersionUID = 2661639336786125723L;

	private static final Logger log = Logger.getLogger(PDABuriedPartCustomAction.class);
	private IBuriedPartCustomService buriedPartCustomServie;

	/**
	 * 得到所有的直埋段
	 * 
	 * @return
	 */
	public String getBuriedPartCustom() {
		try {
			BuriedPartInfoBean obj = (BuriedPartInfoBean) getRequestObject(BuriedPartInfoBean.class);
			obj.setStart(this.start);
			obj.setLimit(this.limit);
			obj.setStateflag(0);
			List<BuriedPartInfoBean> list = buriedPartCustomServie.getBuriedPartGridCustom(obj);
			if (list != null) {
				sendResponse(Integer.valueOf(0), list);
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDABuriedPartCustom.getBuriedPartCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n"
					+ getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 修改直埋段
	 * 
	 * @return
	 */
	public String updateBuriedPartCustom() {
		try {
			BuriedPartInfoBean obj = (BuriedPartInfoBean) getRequestObject(BuriedPartInfoBean.class);
			// obj = this.buriedPartCustomServie.setBuriedPartLength(obj);
			if (TextUtil.isNull(obj.getInt_id())) {
				obj.setModifier(realName);
				int result = buriedPartCustomServie.insertBuriedPartCustom(obj);
				obj.setInt_id(result);
				sendResponse(Integer.valueOf(0), "修改成功。");
			} else {
				if (TextUtil.isNotNull(super.realName)) {
					obj.setModifier(super.realName);
				}
				buriedPartCustomServie.updateBuriedPartCustom(obj);
				sendResponse(Integer.valueOf(0), "修改成功。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDABuriedPartCustom.updateBuriedPartCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n"
					+ getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 删除直埋段
	 * 
	 * @return
	 */
	public String deleteBuriedPartCustom() {
		try {
			BuriedPartInfoBean obj = (BuriedPartInfoBean) getRequestObject(BuriedPartInfoBean.class);
			if (TextUtil.isNotNull(obj.getInt_id())) {
				obj.setModifier(areaName);
				buriedPartCustomServie.deleteBuriedPartCustom(obj);
				sendResponse(Integer.valueOf(0), "删除成功。");
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误。");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDABuriedPartCustom.deleteBuriedPart ERROR\nJsonRequest:" + getJsonRequest() + "\n"
					+ getJsonResponse(), e);
		}
		return "success";
	}

	/**
	 * 新增直埋段
	 * 
	 * @return
	 */
	public String insertBuriedPartCustom() {
		try {
			BuriedPartInfoBean obj = (BuriedPartInfoBean) getRequestObject(BuriedPartInfoBean.class);
			if (this.checkBuriedPart(obj.getZh_label()) > 0) {
				sendResponse(Integer.valueOf(2), "直埋段名称被占用。");
			} else {
				if (TextUtil.isNull(obj.getZh_label()) || TextUtil.isNull(obj.getStart_ponit_id())
						|| TextUtil.isNull(obj.getEnd_ponit_id())) {
					sendResponse(Integer.valueOf(2), "请填写必填字段。");
				} else {
					// obj =
					// this.buriedPartCustomServie.setBuriedPartLength(obj);
					if (TextUtil.isNotNull(super.realName)) {
						obj.setCreator(super.realName);
					}
					int result = buriedPartCustomServie.insertBuriedPartCustom(obj);
					obj.setInt_id(result);
					sendResponse(Integer.valueOf(0), obj);
				}
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDABuriedPartCustom.insertBuriedPartCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n"
					+ getJsonResponse(), e);
		}
		return "success";
	}

	public int checkBuriedPart(String name) {
		String sql = "select count(*) from rms_buried_seg where zh_label='" + name + "' and stateflag='0'";
		int size = this.getJdbcTemplate().queryForInt(sql);
		return size;
	}

	public static Logger getLog() {
		return log;
	}

	public IBuriedPartCustomService getBuriedPartCustomServie() {
		return buriedPartCustomServie;
	}

	public void setBuriedPartCustomServie(IBuriedPartCustomService buriedPartCustomServie) {
		this.buriedPartCustomServie = buriedPartCustomServie;
	}

}
