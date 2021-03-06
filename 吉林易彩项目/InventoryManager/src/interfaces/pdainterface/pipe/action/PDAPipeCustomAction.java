package interfaces.pdainterface.pipe.action;

import java.util.List;
import org.apache.log4j.Logger;
import base.util.MapUtil;
import base.util.TextUtil;
import base.util.functions;
import base.util.pojo.Point;
import base.web.InterfaceAction;
import interfaces.pdainterface.pipe.service.PDAPipeCustomService;
import manage.pipe.pojo.PipeSegCustomInfoBean;
import manage.pipe.pojo.WellCustomInfoBean;

public class PDAPipeCustomAction extends InterfaceAction {
	private static final long serialVersionUID = -2103192041490326189L;
	private static final Logger log = Logger.getLogger(PDAPipeCustomAction.class);
	private PDAPipeCustomService pdaPipeCustomService;

	/**
	 * 查询井信息
	 * 
	 * @return
	 */
	public String getWellCustom() {
		try {
			WellCustomInfoBean well = (WellCustomInfoBean) getRequestObject(WellCustomInfoBean.class);
			if (well != null) {
				if (TextUtil.isNull(well.getInt_id()) && (well.getLatitude() != null)
						&& (!(well.getLatitude().equals(""))) && (well.getLongitude() != null)
						&& (!(well.getLongitude().equals("")))) {
					if (isWGS) {
						Point point = MapUtil.phone_db_encrypt(Double.parseDouble(well.getLatitude()),
								Double.parseDouble(well.getLongitude()));
						well.setLatitude(point.getLat() + "");
						well.setLongitude(point.getLng() + "");
					}
					double[] arr = functions.getAround(Double.parseDouble(well.getLatitude()),
							Double.parseDouble(well.getLongitude()),
							((this.start / this.limit) + 1) * Integer.parseInt(properties.getValueByKey("gisLength")));
					well.setLats(String.valueOf(arr[0]));
					well.setLons(String.valueOf(arr[1]));
					well.setLate(String.valueOf(arr[2]));
					well.setLone(String.valueOf(arr[3]));
				} else {
					well.setStart(this.start);
					well.setLimit(this.limit);
				}
				well.setStateflag(0);
				List<WellCustomInfoBean> list = this.pdaPipeCustomService.getWellCustom(well);
				for (WellCustomInfoBean obj : list) {
					if (isWGS) {
						Point point = MapUtil.db_phone_encrypt(Double.parseDouble(obj.getLatitude()),
								Double.parseDouble(obj.getLongitude()));
						obj.setLatitude(point.getLat() + "");
						obj.setLongitude(point.getLng() + "");
					}
				}
				sendResponse(Integer.valueOf(0), list);
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}

		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAPipeCustom.getWellCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(),
					e);
		}
		return "success";
	}

	/**
	 * 增加井
	 * 
	 * @return
	 */
	public String insertWellCustom() {
		try {
			WellCustomInfoBean well = (WellCustomInfoBean) getRequestObject(WellCustomInfoBean.class);
			if (isWGS) {
				Point point = MapUtil.phone_db_encrypt(Double.parseDouble(well.getLatitude()),
						Double.parseDouble(well.getLongitude()));
				well.setLatitude(point.getLat() + "");
				well.setLongitude(point.getLng() + "");
			}
			well.setCreator(realName);
			int i = this.pdaPipeCustomService.insertWellCustom(well).intValue();
			if (i > 0) {
				well.setInt_id(Integer.valueOf(i));
			
				sendResponse(Integer.valueOf(0), well);
			} else {
				sendResponse(Integer.valueOf(4), "名称重复");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error(
					"PDAPipeCustom.insertWellCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(),
					e);
		}
		return "success";
	}

	/**
	 * 修改井信息
	 * 
	 * @return
	 */
	public String updateWellCustom() {
		try {
			WellCustomInfoBean well = (WellCustomInfoBean) getRequestObject(WellCustomInfoBean.class);
			if (well != null) {
				if (isWGS) {
					Point point = MapUtil.phone_db_encrypt(Double.parseDouble(well.getLatitude()),
							Double.parseDouble(well.getLongitude()));
					well.setLatitude(point.getLat() + "");
					well.setLongitude(point.getLng() + "");
				}
				well.setModifier(realName);
				int i = this.pdaPipeCustomService.updateWellCustom(well).intValue();
				if (i > 0) {
					if (isWGS) {
						Point point = MapUtil.db_phone_encrypt(Double.parseDouble(well.getLatitude()),
								Double.parseDouble(well.getLongitude()));
						well.setLatitude(point.getLat() + "");
						well.setLongitude(point.getLng() + "");
					}
					sendResponse(Integer.valueOf(0), well);
					// 更新段信息
					// new
					// upSegThread(well.getWellId(),well.getWellName()).start();
				} else {
					sendResponse(Integer.valueOf(1), "修改失败");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error(
					"PDAPipeCustom.updateWellCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(),
					e);
		}
		return "success";
	}

	/**
	 * 删除井
	 * 
	 * @return
	 */
	public String deleteWellCustom() {
		try {
			WellCustomInfoBean well = (WellCustomInfoBean) getRequestObject(WellCustomInfoBean.class);
			if (well != null) {
				well.setModifier(realName);
				this.pdaPipeCustomService.deleteWellCustom(well);
				sendResponse(Integer.valueOf(0), "删除成功");
				/*
				 * //删除相应管道数据
				 * this.pdaPipeService.delPipeSeg(well.getWellId()+"");
				 * //删除相应引上数据
				 * this.pdaPipeService.delLeadupStage(well.getWellId()+"");
				 */
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error(
					"PDAPipeCustom.deleteWellCustom ERROR\nJsonRequest:" + getJsonRequest() + "\n" + getJsonResponse(),
					e);
		}
		return "success";
	}
	
	/**
	 * 增加管道段
	 * 
	 * @return
	 */
	public String insertPipesegCustom() {
		try {
			PipeSegCustomInfoBean pipeseg = (PipeSegCustomInfoBean) getRequestObject(PipeSegCustomInfoBean.class);
			if (pipeseg != null) {
				if(TextUtil.isNull(pipeseg.getZh_label()) || TextUtil.isNull(pipeseg.getCity_id()) || TextUtil.isNull(pipeseg.getCounty_id())){
					sendResponse(Integer.valueOf(1), "请填写必填字段!");
				}else{
					if(TextUtil.isNotNull(super.realName)){
						pipeseg.setCreator(super.realName);
					}
					//pipeseg = this.pdaPipeCustomService.setPipeSegLength(pipeseg);
					int i = this.pdaPipeCustomService.insertPipesegCustom(pipeseg).intValue();
					if (i < 0) {
						sendResponse(Integer.valueOf(1), "名称重复!");
					} else {
						pipeseg.setInt_id(Integer.valueOf(i));
						sendResponse(Integer.valueOf(0), pipeseg);
					}
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAPipeCustom.insertPipesegCustom ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 得到管道段
	 * 
	 * @return
	 */
	public String getPipeSegCustom() {
		try {
			PipeSegCustomInfoBean pipeseg = (PipeSegCustomInfoBean) getRequestObject(PipeSegCustomInfoBean.class);
			if (pipeseg != null) {
				pipeseg.setStart(start);
				pipeseg.setLimit(this.limit);
				pipeseg.setStateflag(0);
				List<PipeSegCustomInfoBean> list = this.pdaPipeCustomService.getPipeSegCustom(pipeseg);
				sendResponse(Integer.valueOf(0), list);
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAPipeCustom.getPipeSegCustom ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 更新管道段
	 * @return
	 */
	public String updatePipeSegCustom() {
		try {
			PipeSegCustomInfoBean pipeseg = (PipeSegCustomInfoBean) getRequestObject(PipeSegCustomInfoBean.class);
			if(TextUtil.isNotNull(super.realName)){
				pipeseg.setModifier(super.realName);
			}
			if (pipeseg != null) {
				//pipeseg = this.pdaPipeCustomService.setPipeSegLength(pipeseg);
				int i = this.pdaPipeCustomService.updatePipeSegCustom(pipeseg).intValue();
				if (i > 0) {
					sendResponse(Integer.valueOf(0), pipeseg);
				} else {
					sendResponse(Integer.valueOf(4), "名称重复");
				}
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAPipeCustom.updatePipeSegCustom ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	
	/**
	 * 删除管道段
	 * @return
	 */
	public String deletePipeSegCustom() {
		try {
			PipeSegCustomInfoBean pipeseg = (PipeSegCustomInfoBean) getRequestObject(PipeSegCustomInfoBean.class);
			if (pipeseg != null) {
				if(TextUtil.isNotNull(super.realName)){
					pipeseg.setModifier(super.realName);
				}
				this.pdaPipeCustomService.deletePipeSegCustom(pipeseg);
				sendResponse(Integer.valueOf(0), "删除成功");
			} else {
				sendResponse(Integer.valueOf(2), "请求参数错误");
			}
		} catch (Exception e) {
			this.exception = e;
			sendResponse(Integer.valueOf(3), "应用服务器异常。");
			log.error("PDAPipeCustom.deletePipeSegCustom ERROR\nJsonRequest:"
					+ getJsonRequest() + "\n" + getJsonResponse(), e);
		}
		return "success";
	}
	

	public PDAPipeCustomService getPdaPipeCustomService() {
		return pdaPipeCustomService;
	}

	public void setPdaPipeCustomService(PDAPipeCustomService pdaPipeCustomService) {
		this.pdaPipeCustomService = pdaPipeCustomService;
	}

}