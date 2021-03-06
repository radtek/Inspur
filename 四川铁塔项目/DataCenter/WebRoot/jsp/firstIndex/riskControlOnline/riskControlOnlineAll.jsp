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
    	<title>在线风控</title>
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
			$(document).ready(function(){
				
				
				var localCity='<%=request.getParameter("city")%>';//获取上一菜单的搜索条件
				localCity = decodeURI(localCity);
				var localDate='<%=request.getParameter("date")%>';//获取上一菜单的搜索条件
				
				//时间选择框赋值
				$("#designedDate").val(localDate);
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
				
				/*显示风险点管控列表*/
				$.ajax({
					url:"${pageContext.request.contextPath}/RiskPointDetailDisplayAction/showRiskPointControlDetail.ilf", 
					async:true,
					type:"POST",
					dataType:"json",
					data:{"city":localCity,"date":localDate,"Is_Search":1},
					success:function(riskData){
						if(riskData["success"]){
			           		var FundHtml="";
			           		var EngineeringHtml="";
			           		var IncomeHtml="";
			           		var ColocationHtml="";
			           		var BaseelectricHtml="";
			           		
							var fund_question=riskData["FUND_QUESTION_DETAIL"];
							var enginering_type=riskData["ENGINERING_TYPE_DETAIL"];
							var income_question=riskData["INCOME_QUESTION_DETAIL"];
							var colocation_charge=riskData["COLOCATION_CHARGE_DETAIL"];
							var basestation_electric=riskData["BASESTATION_ELECTRIC_DETAIL"];
							/*显示资金问题展示表*/	
							for(var i=0;i<fund_question.length;i++){
								var data=fund_question[i];
								FundHtml+="<tr>";
								FundHtml+="	  <td>"+data["ID"]+"</td>";
								FundHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								FundHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								FundHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								FundHtml+="</tr>";	
							}
							$("#fund_question_detail").html(FundHtml);
							/*显示工程类问题展示表*/
							for(var i=0;i<enginering_type.length;i++){
								var data=enginering_type[i];
								EngineeringHtml+="<tr>";
								EngineeringHtml+="	  <td>"+data["ID"]+"</td>";
								EngineeringHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								EngineeringHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								EngineeringHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								EngineeringHtml+="</tr>";	
							}
							$("#enginering_type_detail").html(EngineeringHtml);
							/*显示收入问题展示表*/
							for(var i=0;i<income_question.length;i++){
								var data=income_question[i];
								IncomeHtml+="<tr>";
								IncomeHtml+="	  <td>"+data["ID"]+"</td>";
								IncomeHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								IncomeHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								IncomeHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								IncomeHtml+="</tr>";	
							}
							$("#income_question_detail").html(IncomeHtml);
							/*显示场租费问题展示表*/
							for(var i=0;i<colocation_charge.length;i++){
								var data=colocation_charge[i];
								ColocationHtml+="<tr>";
								ColocationHtml+="	  <td>"+data["ID"]+"</td>";
								ColocationHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								ColocationHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								ColocationHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								ColocationHtml+="</tr>";	
							}
							$("#colocation_charge_detail").html(ColocationHtml);
							/*显示基站电费问题展示表*/
							for(var i=0;i<basestation_electric.length;i++){
								var data=basestation_electric[i];
								BaseelectricHtml+="<tr>";
								BaseelectricHtml+="	  <td>"+data["ID"]+"</td>";
								BaseelectricHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								BaseelectricHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								BaseelectricHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								BaseelectricHtml+="</tr>";	
							}
							$("#basestation_electric_detail").html(BaseelectricHtml);
						    /*显示代维管理问题展示表*/
						
						
						
						}
					}
				});
				
				
				
			});
			
			
			
			function showRiskQuestion(qu_id){
				var city=$("#citySelect").val();
				city=encodeURI(city);
				var date=$("#designedDate").val();
				url = encodeURI("jsp/firstIndex/riskControlOnline/questionDataList.jsp?quid="+qu_id+"&city="+city+"&date="+date);
				window.parent.parent.turnToJsp("问题数据列表",url);
			}
			function showFeedBackQuestion(qu_id){
				var city=$("#citySelect").val();
				city=encodeURI(city);
				var date=$("#designedDate").val();
				url = encodeURI("jsp/firstIndex/riskControlOnline/feedbackDataList.jsp?quid="+qu_id+"&city="+city+"&date="+date);
				window.parent.parent.turnToJsp("已反馈数据列表",url);
			}

			
			
			
			/*搜索方法*/
            function searchRiskPointDetail()
			 {
				/*查询地级市风险点反馈列表*/
				$.ajax({
					url:"${pageContext.request.contextPath}/RiskPointDetailDisplayAction/showRiskPointControlDetail.ilf",
					async:true,
					type:"POST",
					dataType:"json",
					data:{"city":$("#citySelect").val(),"date":$("#designedDate").val(),"Is_Search":-1},
					timeout:10000,
					success:function(databack){
						if(databack["success"]){
							
				     		var FundHtml="";
			           		var EngineeringHtml="";
			           		var IncomeHtml="";
			           		var ColocationHtml="";
			           		var BaseelectricHtml="";
			           		
							var fund_question=databack["FUND_QUESTION_DETAIL"];
							var enginering_type=databack["ENGINERING_TYPE_DETAIL"];
							var income_question=databack["INCOME_QUESTION_DETAIL"];
							var colocation_charge=databack["COLOCATION_CHARGE_DETAIL"];
							var basestation_electric=databack["BASESTATION_ELECTRIC_DETAIL"];
							/*显示资金问题展示表*/	
							for(var i=0;i<fund_question.length;i++){
								var data=fund_question[i];
								FundHtml+="<tr>";
								FundHtml+="	  <td>"+data["ID"]+"</td>";
								FundHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								FundHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								FundHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								FundHtml+="</tr>";	
							}
							$("#fund_question_detail").html(FundHtml);
							/*显示工程类问题展示表*/
							for(var i=0;i<enginering_type.length;i++){
								var data=enginering_type[i];
								EngineeringHtml+="<tr>";
								EngineeringHtml+="	  <td>"+data["ID"]+"</td>";
								EngineeringHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								EngineeringHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								EngineeringHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								EngineeringHtml+="</tr>";	
							}
							$("#enginering_type_detail").html(EngineeringHtml);
							/*显示收入问题展示表*/
							for(var i=0;i<income_question.length;i++){
								var data=income_question[i];
								IncomeHtml+="<tr>";
								IncomeHtml+="	  <td>"+data["ID"]+"</td>";
								IncomeHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								IncomeHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								IncomeHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								IncomeHtml+="</tr>";	
							}
							$("#income_question_detail").html(IncomeHtml);
							/*显示场租费问题展示表*/
							for(var i=0;i<colocation_charge.length;i++){
								var data=colocation_charge[i];
								ColocationHtml+="<tr>";
								ColocationHtml+="	  <td>"+data["ID"]+"</td>";
								ColocationHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								ColocationHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								ColocationHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								ColocationHtml+="</tr>";	
							}
							$("#colocation_charge_detail").html(ColocationHtml);
							/*显示基站电费问题展示表*/
							for(var i=0;i<basestation_electric.length;i++){
								var data=basestation_electric[i];
								BaseelectricHtml+="<tr>";
								BaseelectricHtml+="	  <td>"+data["ID"]+"</td>";
								BaseelectricHtml+="	  <td>"+data["RISK_NAME"]+"</td>";
								BaseelectricHtml+="	  <td><a href='javascript:void(0)' onclick='showRiskQuestion("+data["RISK_ID"]+")'>"+parseInt(data["QUNUM"])+"</a></td>";
								BaseelectricHtml+="	  <td><a href='javascript:void(0)' onclick='showFeedBackQuestion("+data["RISK_ID"]+")'>"+parseInt(data["FEEDNUM"])+"</a></td>";
								BaseelectricHtml+="</tr>";	
							}
							$("#basestation_electric_detail").html(BaseelectricHtml);
							
						
						}
					}
				});
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
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font color="white" size="5">在线风控</font>
				</div>
			</div>
			
			<div class="panel-body">
				<div style="width:100%;height:50px">
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>地市：</span>
						<div style="display:inline"  id="citySelection">
								<select style="width:180px;border:solid 1px #A3D0E3;" id="citySelect">
									<option value="全省">--</option>
								</select>
						</div>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<span>日期：</span>
						<input id="designedDate" type="text" placeholder="请选择日期" style="cursor:pointer;border:solid 1px #A3D0E3;width:190px;height:29px;font-size:12px;" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate"></input>
					</div>
					<div class="col-xs-6 col-sm-4 col-md-4" style="text-align:center;">
						<a class="btn btn-success" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:searchRiskPointDetail();">
								<span class="icon-search icon-white"></span>查询
						</a>
					</div>
				</div>
				
			 	<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align: center;'>
						<font size="4" color="#228B22">资金问题</font>
						<!--<button class="btn btn-success" type="submit">资金问题</button>  -->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody id="fund_question_detail">

							
						</tbody>
					</table>
				</div>
				
				<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align:right;display:inline-block;width:55%'>
							<font size="4" color="#228B22">工程类</font>
							<!--<button class="btn btn-success" type="submit">工程类</button> -->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody id="enginering_type_detail">
						</tbody>
					</table>
				</div>
				
				<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align: center;'>
							<font size="4" color="#228B22">收入方面</font>
							<!--<button class="btn btn-success" type="submit">收入方面</button> -->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody  id="income_question_detail">
						
							
						</tbody>
					</table>
				</div>
				
				<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align: center;'>
							<font size="4" color="#228B22">场租费</font>
							<!--<button class="btn btn-success" type="submit">场租费</button> -->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody id="colocation_charge_detail">
							
						</tbody>
					</table>
				</div>
				
				<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align: center;'>
							<font size="4" color="#228B22">基站电费</font>
							<!--<button class="btn btn-success" type="submit">基站电费</button> -->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody id="basestation_electric_detail">
							
						</tbody>
					</table>
				</div>
				
				<div class="col-xs-6 col-sm-3 col-md-6"
					style="margin-top:15px">
					<table class="table table-bordered"
						style='vertical-align: middle; text-align: center;'>
						<div class="center-block" style='text-align: center;'>
							<font size="4" color="#228B22">代维管理</font>
							<!--<button class="btn btn-success" type="submit">代维管理</button>-->
						</div>
						<div style="width:100%;height:6px"></div>
						<thead>
							<tr>
								<th>序号</th>
								<th>风险点描述</th>
								<th>问题数量</th>
								<th>已反馈</th>
							</tr>
						</thead>
						<tbody>
							<tr class="active">
								<td>1</td>
								<td>虚假发电套取发电费用</td>
								<td><a>56</a></td>
								<td><a>20</a></td>
							</tr>
							
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>	
	</body>
</html>