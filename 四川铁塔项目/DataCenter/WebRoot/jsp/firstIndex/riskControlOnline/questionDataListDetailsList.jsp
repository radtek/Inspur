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
    	<title>问题数据列表</title>
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
		<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.7/js/dataTables.select.min.js"></script>
		<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
		<link rel="stylesheet" href="https://cdn.datatables.net/select/1.2.7/css/select.dataTables.min.css">
		
		<script type="text/javascript">
			var oTable = null;
			var conditions = [];
			var qu_type="";
			var mouth="";			
			$(document).ready(function(){
				var city='<%=request.getParameter("city")%>';
				city = decodeURI(city);
			    mouth='<%=request.getParameter("mouth")%>';
				qu_type='<%=request.getParameter("qu_type")%>';
				
				//时间选择框赋值
				$("#designedDate").val(mouth);				
				/*开始获取地市下拉框数据*/
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
			                             //if(optionValue.indexOf(cityName)!=-1){
										 //	citySelectObj.options[i].selected = "selected";
		                                 //   break;
			                             //}
			                             
			                             if(optionValue==city){//此处默认选中上一页面传递的值
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
				$.ajax({
					url:"${pageContext.request.contextPath}/riskQuestionDataListDetailsListAction/findRiskDataListDetailsListFirst.ilf",
					async:true,
					type:"POST",
					dataType:"json",
					data:{"qu_id":qu_type,"city":city,"mouth":mouth},
					timeout:10000,
					success:function(data){
						if(data["success"]){
							
							$("#title").html(data["QU_TYPE"]+"数据列表");
							oTable=$('#firstDataList').dataTable({
								"bSortClasses":false,
								"aLengthMenu":[10,20,30],
								"bAutoWidth":false,
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
									"mData":"NUM",
									"sWidth":"12.5%"
								},{
									"mData":"CITY",
									"sWidth":"12.5%"
								},{
									"mData":"COUNTY",
									"sWidth":"12.5%"
								},{
									"mData":"MOUTH",
									"sWidth":"12.5%"
								},{
									"mData":"RISK_TYPE",
									"sWidth":"12.5%"
								},{
									"mData":"RISK_NAME",
									"sWidth":"12.5%"
								},{
									"mData":"QU_TYPE",
									"sWidth":"12.5%"
								},{
									"mData":"ID",
									"sWidth":"12.5%",
									 "mRender": function ( data, type, full ) {
									        return "<button class='btn btn-success' onclick='showDetail("+data+",\""+full.RISK_NAME+"\",\""+full.RISK_TYPE+"\")'>查看详情</button>";
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
										data:"tableparam="+JSON.stringify(aoData)+"&conditions="+JSON.stringify(conditions)+"&qu_id="+qu_type+"&city="+city+"&mouth="+mouth,
										dataType:"json",		
										success:function(data){
											fnCallback(data);
										}
									});
								},
								"sAjaxSource":"${pageContext.request.contextPath}/riskQuestionDataListDetailsListAction/findRiskDataListDetailsListTable.ilf"
							});
						}
					}
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
			}
			function showDetail(id,risk_name,risk_type){
				risk_name=encodeURI(risk_name);
				risk_type=encodeURI(risk_type);
				url = encodeURI("jsp/firstIndex/riskControlOnline/questionDataListDetails.jsp?risk_name="+risk_name+"&id="+id+"&risk_type="+risk_type);
				window.parent.turnToJsp("数据列表详情",url);
				
			}
			/*清除掉查询条件*/
			function searchClear(){
				//下拉框回归到第一个，地市只有一个数据，全省的第一个数据为'--请选择--'
				$("#citySelection option:first").prop("selected", 'selected');
				
				//日期条件直接清除
				$("#designedDate").val("");
			}
			
			/*导出报表*/
			function exportExcel(){
				var date=$("#designedDate").val();
				var city=$("#citySelect").val();
				window.location.href="${pageContext.request.contextPath}/riskQuestionDataListDetailsListAction/exportExcel.ilf?"
						+"qu_id="+qu_type+"&mouth="+mouth+"&date="+date+"&city="+encodeURI(encodeURI(city));//条件为qu_id以及区划和时间
			}
		</script>
		<style type="text/css">
			.badge{
				background-color:blue
			}
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
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font id="title" color="white" size="5">数据列表</font>
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
						<span>月份：</span>
						<input id="designedDate" type="text" placeholder="请选择日期" style="cursor:pointer;border:solid 1px #A3D0E3;width:190px;height:29px;font-size:12px;" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate"></input>
					</div>
				</div>
				<div style="width:100%;height:50px">
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<button class="btn btn-info" style="width:80px;" onclick="searchData()">查询</button>
						<button class="btn btn-warning" style="width:80px;" onclick="searchClear()">清除</button>
						<button class="btn btn-danger" style="width:80px;" onclick="exportExcel()">导出</button>
					</div>
				</div>
				
			 	<div id="amount" class="col-xs-12 col-sm-12 col-md-12"
					style="margin-top:15px;display:block;">
					<table id="firstDataList" class="table table-bordered table-hover"
						style="vertical-align: middle; text-align: center;">
						<thead>
							<tr id="first_th">
								<th>序号</th>
								<th>地市</th>
								<th>区县</th>
								<th>月份</th>
								<th>风险点类型</th>
								<th>风险点名称</th>
								<th>问题类型</th>
								<th>操作</th>
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