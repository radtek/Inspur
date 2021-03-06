<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String frameHeight = request.getParameter("frameHeight");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
   		<meta name="viewport" content="width=device-width, initial-scale=1">
    	<base href="<%=basePath%>">
    	<title>在线风控风险点已反馈问题总数</title>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css"></link>
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/todc-bootstrap.css">
    	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/EasyUI/icon.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/jQueryUI/css/jquery-ui.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/datatables/css/jquery.dataTables.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/xcConfirm/css/xcConfirm.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/zTree/css/zTreeStyle.css">	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/font-awesome.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/topMenus/css/style.css">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/jQueryUI/jquery-ui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/datatables/js/jquery.dataTables.js"></script>		
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/xcConfirm/js/xcConfirm.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.core.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/zTree/js/jquery.ztree.excheck.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/my97datepicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins/checkboxes.js"></script>
		<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
		
		<script type="text/javascript">
			var oTable = null;
			var conditions = [];
			$(document).ready(function(){
				var localCity='<%=request.getParameter("city")%>';
				localCity = decodeURI(localCity);
				var localDate='<%=request.getParameter("date")%>';
				
				//添加默认筛选条件
				var conditions = [];							
				conditions.push({
					name:"CITY",
					value:localCity
				});
				
				conditions.push({
					name:"DATE",
					value:localDate
				});
				
				//时间选择框赋值
				$("#designedDate").val(localDate);
				/*首先向后台获取身份（省级/地市）*/
				$.ajax({
					url:"${pageContext.request.contextPath}/feedBackQuestionAmountAction/findFeedBackQuestionFirst.ilf",
					async:true,
					type:"POST",
					data:"name=-1"+"&city="+localCity+"&date="+localDate,
					timout:10000,
					success:function(riskData){
						if(riskData["success"]){
							if(riskData["IS_PROVINCE"]){
								/*省份成员，开始获取下拉框全省地市信息*/
								$.ajax({
									url:"${pageContext.request.contextPath}/myBenchAction/dataCenterPage.ilf",
									async:true,
									type:"POST",
									dataType:"json",
									data:"cityName=-1&thisMonth=-1",
									timeout:10000,
									success:function(textStatus){
										if(textStatus["SUCCESS"]){
											/*地市列表*/
											var cityArray = textStatus["CITY_LIST"];
											var citySelect = "";
											citySelect+="<select style='width:180px;height:29px;border:solid 1px #A3D0E3;' id='citySelect'>";
											for(var i=0;i<cityArray.length;i++){
												var cityName = cityArray[i];
												citySelect+="<option value='"+cityName["CITY_NAME"]+"'>"+cityName["CITY_NAME"]+"</option>";
											}
											citySelect+="</select>";
											document.getElementById("citySelection").innerHTML = citySelect;
											/*
												=== 初始化地市信息   ===
											 */
											var cityName = "";
											if(textStatus["IS_PROVINCE"]){
												cityName = "全省";
											}else{
												cityName = textStatus["MY_CITY"];
											}
											if(cityName!=""){
												if(cityName.length>2){
													cityName = cityName.substring(0,2);
												}
												var citySelectObj = document.getElementById("citySelect");
												if(citySelectObj!=null && citySelectObj.options!=null && citySelectObj.options.length>0){
													for(var i=0;i<citySelectObj.options.length;i++){
							                             var optionValue = citySelectObj.options[i].value;
							                             if(optionValue==localCity){
							                            	 citySelectObj.options[i].selected = "selected";
							                            	 break;
							                             }
													}
												}	
											}
										}
									},
									error:function(){}
								});
							}else{
								/*不是省份，只加载用户当前地市*/
								var city="<option value='"+riskData["BELONG_AREA"]+"'>"+riskData["BELONG_AREA"]+"</option>"
								$("#citySelect").html(city);
							}
							
							/*将风险问题数量填充*/
							$("#question_amount").html(riskData["QUESTION_AMOUNT"]);
	
							/*获取具体表格中的数据*/
							oTable=$('#totalData').dataTable({
								"bSortClasses":false,
								"aLengthMenu":[10,20,30],
								"bAutoWidth":true,
								"bSort":true,
								"bProcessing":true,
								"bServerSide":true,
								"bFilter":false,
								"bLengthChange":true,
								"sPaginationType":"full_numbers",
								"bStateSave":false,
								"bScrollCollapse":true,
								"sScrollX":"100%",
								"aoColumns":[{
									"mData":"ID",
									"sWidth":"20%"
								},{
									"mData":"RISK_TYPE",
									"sWidth":"20%"
								},{
									"mData":"CITY",
									"sWidth":"20%"
								},{
									"mData":"MOUTH",
									"sWidth":"20%"
								},{
									"mData":"AMOUNT",
									"sWidth":"20%",
									 "mRender": function ( data, type, full ) {
									        return "<a href='javaScript:void(0)' onclick='showDetail(\""+full.RISK_TYPE+"\",\""+full.CITY+"\",\""+full.MOUTH+"\",\""+data+"\")'>"+data+"</a>";
									  }
								}],
								"aoColumnDefs":[] ,
								"iDisplayLength":10,
								"sDom":"<'top'>frt<'bottom'ilp><'clear'>",
								"aaSorting":[[0,"desc"]],
								"fnRowCallback":function(nRow,aData,iDisplayIndex){
									//$("td:eq(0)",nRow).html("");
								},
								"fnServerData":function(sSource,aoData,fnCallback){
									$.ajax({
										url:sSource,
										type:"POST",
										data:"tableparam="+JSON.stringify(aoData)+"&conditions="+JSON.stringify(conditions),
										dataType:"json",		
										success:function(data){
											fnCallback(data);
										}
									});
								},
								"sAjaxSource":"${pageContext.request.contextPath}/feedBackQuestionAmountAction/findTableData.ilf"
							});
						}
					},
					error:function(){}
				});
			});
			/*按条件查询*/
			function searchData(){
				var date=$("#designedDate").val();
				if(date!=null && date!=""){
					conditions = [{
						name:"DATE",
						value:date
					}];	
				}else{
					conditions = [];
				}
				conditions.push({
					name:"CITY",
					value:$("#citySelect").val()
				});
				oTable.fnDraw();
				/*查询地级市反馈总数*/
				$.ajax({
					url:"${pageContext.request.contextPath}/feedBackQuestionAmountAction/findAfterSearchNumData.ilf",
					async:true,
					type:"POST",
					dataType:"json",
					data:{"city":$("#citySelect").val(),"date":date},
					timeout:10000,
					success:function(data){
						if(data["success"]){
							var amount=data["AMOUNT"];
							$("#question_amount").html(amount);
						}
					}
				});
			}
			function showDetail(riskType,city,mouth,account){
				city=encodeURI(city);
				riskType=encodeURI(riskType);
				var url = "jsp/firstIndex/riskControlOnline/riskFeedbackQuestionAmountDetail.jsp?riskType="+riskType+"&city="+city+"&mouth="+mouth+"&account="+account;
				url = encodeURI(url);
				window.parent.parent.turnToJsp("已反馈问题详情",url);
			}
		</script>
		<style type="text/css">
			.table th,.table td{
				text-align:center
			}
			.table tbody tr td{
				vertical-align:middle
			}
		</style>
  	</head>
  	<body style="width:100%;height:100%;border:solid 0px red;" id="bodyHeight">
  	<div class="container" style="width:100%;height:100%;margin-top:-20px;">
		<div class="panel panel-primary"  id="mainPanel">
			<!-- 
			<div class="panel-heading" id="panelHeading">
				<h3 class="panel-title">在线风控</h3>
			</div>
			 -->
			 
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font color="white" size="5">在线风控--风险点已反馈问题总数</font>
				</div>
			</div>
			
			<div class="panel-body">
				<div style="width:100%;height:50px">
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>地市：</span>
						<div style="display:inline"  id="citySelection">
								<select style="width:180px;border:solid 1px #A3D0E3;" id="citySelect">
									<option value="-">-</option>
								</select>
						</div>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>日期：</span>
						<input id="designedDate" type="text" placeholder="请选择日期" style="cursor:pointer;border:solid 1px #A3D0E3;width:190px;height:29px;font-size:12px;" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate"></input>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<a class="btn btn-success" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:searchData();">
								<span class="icon-search icon-white"></span>查询
						</a>
					</div>
				</div>
				
			 	<div class="col-xs-12 col-sm-12 col-md-12"
					style="margin-top:15px">
					<div style="width:100%;height:40px;">
						<font size="4">风险点已反馈数量:</font>
						<!--<button type="button" class="btn btn-success btn-sm" style="width:70px;"><span id="question_amount">-</span></button>-->
						<font size="4" color="#32CD32"><span id="question_amount">-</span></font>
					</div>
					<table id="totalData" class="table table-bordered table-hover"
						style='vertical-align: middle; text-align: center;'>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点类型</th>
								<th>地市</th>
								<th>月份</th>
								<th>数量</th>
							</tr>
						</thead>
						<tbody>
							
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>	
	</body>
</html>