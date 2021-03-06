package interfaces.pdainterface.route.service;

import base.exceptions.DataAccessException;
import interfaces.pdainterface.equt.pojo.EqutCableInfo;

import java.util.List;
import java.util.Map;

import manage.point.pojo.PointInfoBean;
import manage.route.pojo.BatchRackBean;
import manage.route.pojo.CableInfoBean;
import manage.route.pojo.FiberBoxInfoBean;
import manage.route.pojo.FiberInfoBean;
import manage.route.pojo.JointInfoBean;
import manage.route.pojo.RouteInfoBean;

public abstract interface PDARouteService
{
  public abstract List<RouteInfoBean> getRoute(RouteInfoBean paramRouteInfoBean)
    throws DataAccessException;

  public abstract Integer updateRoute(RouteInfoBean paramRouteInfoBean)
    throws DataAccessException;

  public abstract Integer insertRoute(RouteInfoBean paramRouteInfoBean)
    throws DataAccessException;

  public abstract List<CableInfoBean> getCable(CableInfoBean paramCableInfoBean)
    throws DataAccessException;

  public abstract Integer updateCable(CableInfoBean paramCableInfoBean)
    throws DataAccessException;

  public abstract Integer insertCable(CableInfoBean paramCableInfoBean)
    throws DataAccessException;

  public abstract Integer deleteCable(CableInfoBean paramCableInfoBean)
    throws DataAccessException;

  public abstract Integer delteRoute(RouteInfoBean paramRouteInfoBean)
    throws DataAccessException;

  public abstract List<JointInfoBean> getJoint(JointInfoBean paramJointInfoBean)
    throws DataAccessException;

  public abstract Integer insertJoint(JointInfoBean paramJointInfoBean)
    throws DataAccessException;

  public abstract Integer updateJoint(JointInfoBean paramJointInfoBean)
    throws DataAccessException;

  public abstract Integer deleteJoint(JointInfoBean paramJointInfoBean)
    throws DataAccessException;

  public abstract List<FiberBoxInfoBean> getFiberbox(FiberBoxInfoBean paramFiberBoxInfoBean)
    throws DataAccessException;

  public abstract Integer insertFiberbox(FiberBoxInfoBean paramFiberBoxInfoBean)
    throws DataAccessException;

  public abstract Integer updateFiberbox(FiberBoxInfoBean paramFiberBoxInfoBean)
    throws DataAccessException;

  public abstract Integer deleteFiberbox(FiberBoxInfoBean paramFiberBoxInfoBean)
    throws DataAccessException;
  
  /**
   * ?????????????????????
   * @param fiber;
   * @return
   */
  public List<FiberInfoBean> getFiber(FiberInfoBean fiber);
  
  
  /**
   * ??????????????????
   * @param fiber
   * @return
   */
  public Integer updateFiber(FiberInfoBean fiber);
  
  
  /**
   * ??????????????????
   * @param fiber
   * @return
   */
  public Integer insertFiber(FiberInfoBean fiber);
  
  
  /**
   * ???????????????????????????
   * @param cable
   * @return
   */
  public String getCableLength(CableInfoBean cable);
  
  /**
   * ?????????????????????
   * @param cable
   * @return
   */
  public CableInfoBean getCableObj(CableInfoBean cable);
  
  
  /**
   * ????????????????????????
   * @param ecable
   * @return
   */
  public List<CableInfoBean> getLayCable(EqutCableInfo ecable);
  
  
  /**
   * ?????????????????????
   * ????????????????????????
   * @param fiber
   * @return
   */
  public FiberInfoBean getFiberObj(FiberInfoBean fiber);
  
  
  /**
   * ??????????????????
   * @param type
   * @param cable
   */
  public void setCableRack(String type,CableInfoBean cable);
  
  
  /**
   * ??????????????????
   * ???????????????
   * @param fiber
   * @return
   */
  public List<FiberInfoBean> getFiberList(FiberInfoBean fiber);
  
  /**
   * ??????????????????
   * @param cableId
   * @param fiberIds
   * @return
   */
  public List<FiberInfoBean> getFiberList(BatchRackBean obj);
  
  /**
   * ???????????????????????????????????????
   * @param pointList
   */
  public void batchPutPoint(List<PointInfoBean> pointList);
  
  /**
   * ????????????
   * @param pointIds
   */
  public void batchPoint(String pointIds);
  
  /**
   * ??????????????????
   * @param obj
   * @param list
   */
  public List<PointInfoBean> batchFiber(BatchRackBean obj ,List<FiberInfoBean> list);
  
}