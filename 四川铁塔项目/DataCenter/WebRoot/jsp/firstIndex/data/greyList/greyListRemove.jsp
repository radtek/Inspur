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
    	<title>灰名单发起解除</title>
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
				/*默认日期渲染*/
				var date = new Date();
				var curYears = date.getFullYear();
				var curMonth = date.getMonth();
				if(curMonth==0){
					curMonth = 12;
					curYears = curYears-1;
				}else{
					curMonth++;
				}
				var finalStr = curYears+"-"+(curMonth<10?"0"+curMonth:curMonth);
				$("#designedDate").val(finalStr);
				/*开始获取数据*/
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
			                             if(optionValue.indexOf(cityName)!=-1){
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
				
	
				/*使用datatable插件解析数据并显示*/
				var tableHeight = $("#mainPanel").height()-$("#panelHeading").height()-80;
				var pageNumbers = parseInt(tableHeight/26);
					oTable = $("#dataTable").dataTable({
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
								"sWidth":"10%",
								"mRender": function ( data, type, full ) {
			        					return "<input type=\"checkbox\" name=\"\" value=\""+data+"\"/>"+data;}

							},{
								"mData":"CITY",
								"sWidth":"10%"
							},
							{
								"mData":"LAUNCH_TIME",
								"sWidth":"10%"
							},{
								"mData":"GL_TYPE",
								"sWidth":"10%"
							},{
								"mData":"GL_DESCRIBE",
								"sWidth":"10%"
							},{
								"mData":"ATTRIBUTION",
								"sWidth":"10%"
							},{
								"mData":"DATA_SOURCE",
								"sWidth":"10%"
							},{
								"mData":"GLS_TIME",
								"sWidth":"10%"
							},{
								"mData":"PROCEDURE_SEGMENT",
								"sWidth":"10%"
							},{
								"mData":"EXPIRY_TIME",
								"sWidth":"10%"
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
						"sAjaxSource":"${pageContext.request.contextPath}/greyListRemoveList/showGreyListData.ilf",
							 "oLanguage" : { // 国际化配置
						           "sProcessing" : "正在获取数据，请稍后...",
						           "sLengthMenu" : "每页显示 _MENU_ 条",
						           "sZeroRecords" : "没有找到数据",
						           "sInfo" : "从 _START_ 到  _END_ 条记录 总记录数为 _TOTAL_ 条",
						           "sInfoEmpty" : "记录数为0",
						           "sInfoFiltered" : "(全部记录数 _MAX_ 条)",
						           "sInfoPostFix" : "",
						           "sSearch" : "查询",
						           "sUrl" : "",
						           "oPaginate" : {
						               "sFirst" : "首页",
						               "sPrevious" : "上一页",
						               "sNext" : "下一页",
						               "sLast" : "尾页"
						           }
							 }
				});
					
			});

		  	
			/*通过输入框获取日期和地级市数据*/
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
		
		function turnToGreyListDetail(){
			 var num=$("input:checkbox:checked").val();
		
				url = encodeURI("jsp/firstIndex/data/greyList/greyListRemoveDataDatail.jsp?ID="+num);
				window.parent.turnToJsp("灰名单列表详情",url);
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
			<div   id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div  style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font color="white" size="5">灰名单发起解除</font>
				</div>
			</div>
			
			<div class="panel-body">
				<div style="width:100%;height:50px;">
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>地市：</span>
						<div style="display:inline"  id="citySelection">
								<select style="width:180px;border:solid 1px #A3D0E3;" id="citySelect">
									<option value="-">-</option>
								</select>
						</div>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>申请发起时间：</span>
						<input id="designedDate" type="text" placeholder="请选择日期" style="cursor:pointer;border:solid 1px #A3D0E3;width:190px;height:29px;font-size:12px;" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate"></input>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<a class="btn btn-success" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:searchData();">
								<span class="icon-search icon-white"></span>查询
						</a>
					</div>
				</div>
				<div style="width:100%;">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=2)" width="100%" color=#8F8F8F SIZE=10>
				</div>
			 	<div class="col-xs-12 col-sm-12 col-md-12"
					style="margin-top:0px">
					<div style="width:100%;height:40px;text-align:right;">
						<button onclick="turnToGreyListDetail()" type="button" class="btn btn-danger btn-sm" style="width:150px;height:35px;margin-right:20px;"><span id="">灰名单发起解除</span></button>
					</div>
					<table id="dataTable" class="table table-bordered table-hover display"
						style='vertical-align: middle; text-align: center;margin-top:10px;'>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>地市</th>
								<th>申请发起时间</th>
								<th>灰名单类型</th>
								<th>灰名单说明</th>
								<th>归口专业</th>
								<th>数据来源</th>
								<th>进入灰名单时间</th>
								<th>流程环节</th>
								<th>到期时间</th>
		
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