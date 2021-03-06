package interfaces.pdainterface.pipe.service;

import base.exceptions.DataAccessException;

import java.util.List;

import manage.pipe.pojo.FaceInfoBean;
import manage.pipe.pojo.LedupInfoBean;
import manage.pipe.pojo.PipeInfoBean;
import manage.pipe.pojo.PipeSegmentInfoBean;
import manage.pipe.pojo.TubeInfoBean;
import manage.pipe.pojo.WellInfoBean;

public abstract interface PDAPipeService
{
  public abstract List<WellInfoBean> getWell(WellInfoBean paramWellInfoBean)
    throws DataAccessException;

  public abstract Integer insertWell(WellInfoBean paramWellInfoBean)
    throws DataAccessException;

  public abstract Integer updateWell(WellInfoBean paramWellInfoBean)
    throws DataAccessException;

  public abstract Integer deleteWell(WellInfoBean paramWellInfoBean)
    throws DataAccessException;

  public abstract List<PipeInfoBean> getPipe(PipeInfoBean paramPipeInfoBean)
    throws DataAccessException;

  public abstract Integer insertPipe(PipeInfoBean paramPipeInfoBean)
    throws DataAccessException;

  public abstract Integer updatePipe(PipeInfoBean paramPipeInfoBean)
    throws DataAccessException;

  public abstract Integer deletePipe(PipeInfoBean paramPipeInfoBean)
    throws DataAccessException;

  public abstract List<PipeSegmentInfoBean> getPipeseg(PipeSegmentInfoBean paramPipeSegmentInfoBean)
    throws DataAccessException;

  public abstract Integer insertPipeseg(PipeSegmentInfoBean paramPipeSegmentInfoBean)
    throws DataAccessException;

  public abstract Integer updatePipeseg(PipeSegmentInfoBean paramPipeSegmentInfoBean)
    throws DataAccessException;

  public abstract Integer deletePipeseg(PipeSegmentInfoBean paramPipeSegmentInfoBean)
    throws DataAccessException;

  public abstract List<FaceInfoBean> getFace(FaceInfoBean paramFaceInfoBean)
    throws DataAccessException;

  public abstract Integer insertFace(FaceInfoBean paramFaceInfoBean)
    throws DataAccessException;

  public abstract Integer updateFace(FaceInfoBean paramFaceInfoBean)
    throws DataAccessException;

  public abstract Integer deleteFace(FaceInfoBean paramFaceInfoBean)
    throws DataAccessException;

  public abstract List<TubeInfoBean> getTube(TubeInfoBean paramTubeInfoBean)
    throws DataAccessException;

  public abstract Integer insertTube(TubeInfoBean paramTubeInfoBean)
    throws DataAccessException;

  public abstract Integer updateTube(TubeInfoBean paramTubeInfoBean)
    throws DataAccessException;

  public abstract Integer deleteTube(TubeInfoBean paramTubeInfoBean)
    throws DataAccessException;

  public abstract List<LedupInfoBean> getLedup(LedupInfoBean paramLedupInfoBean)
    throws DataAccessException;

  public abstract Integer insertLedup(LedupInfoBean paramLedupInfoBean)
    throws DataAccessException;

  public abstract Integer updateLedup(LedupInfoBean paramLedupInfoBean)
    throws DataAccessException;

  public abstract Integer deleteLedup(LedupInfoBean paramLedupInfoBean)
    throws DataAccessException;
  
  /**
   * ??????????????????
   * @param pipeseg
   * @return
   * @throws Exception
   */
  public PipeSegmentInfoBean getPipeSegObj(PipeSegmentInfoBean pipeseg) throws Exception;
  
  
  /**
   * ?????????????????????
   * @param pipeseg
   * @return
   */
  public PipeSegmentInfoBean setPipeSegLength(PipeSegmentInfoBean pipeseg);
  
  
  /**
   * 
   * @param well
   * @return
   */
  public WellInfoBean getWellObj(WellInfoBean well);
  
  
  /**
   * ??????????????????
   * @param well
   * @return
   */
  public boolean getWellLay(WellInfoBean well);
  
  
  /**
   * ??????id???????????????
   * @param wellId
   * @return
   */
  public WellInfoBean getWellByid(Integer wellId);
  
  
  /**
   * ??????????????????
   * @param tube
   * @return
   */
  public TubeInfoBean getTubeObj(TubeInfoBean tube);
  
  
  /**
   * ??????????????????
   * @param tube
   * @return
   */
  public List<TubeInfoBean> beatchTube(TubeInfoBean tube);
  
  
  /**
   * ??????????????????
   * @param list
   */
  public void beatchSubTube(List<TubeInfoBean> list,String rentFlag);
  
  
  /**
   * ?????????????????????
   * @param wellId
   * @return
   */
  public List<TubeInfoBean> getTubeBywell(String wellId);
  
  /**
   * ??????????????????
   * ????????????
   * @param id
   * @param isFather
   * @return
   */
  public List<TubeInfoBean> getTubeList(String id,String isFather);
  
  
  /**
   * ?????????????????????
   * @param wellId
   */
  public void delPipeSeg(String wellId);
  
  
  /**
   * ???????????????????????????
   * @param segId
   * @return
   */
  public List<TubeInfoBean> getTubeByPipe(String segId);
  
  
  /**
   * ????????????????????????????????????
   * @param wellId
   * @param wellName
   */
  public void upPipeSeg(Integer wellId,String wellName);
  
  
  /**
   * ???????????????
   * ???????????????
   * @param wellId
   */
  public void delLeadupStage(String wellId) ;
}
