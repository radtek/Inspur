<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String frameHeight = request.getParameter("frameHeight");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css"></link>
	<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/extensions/layui/css/layui.css" media="all">
	<script type="text/javascript" src="${pageContext.request.contextPath}/extensions/layui/layui.js" charset="utf-8"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			var param01='<%=request.getParameter("param01")%>';
			var param02='<%=request.getParameter("param02")%>';
			param02=decodeURI(param02);
			var param03='<%=request.getParameter("param03")%>';
			var id='<%=request.getParameter("ID")%>';
			var type='<%=request.getParameter("type")%>';
			if(param01!=0 && param02!=0 && param03!=0 && id!=0 && type=="check"){
				$.ajax({
					url:"${pageContext.request.contextPath}/greyListApplyAction/findDataDetail.ilf",
					async:true,
					type:"POST",
					dataType:"JSON",
					data:{"param01":param01,"param02":param02,"param03":param03,"id":id},
					timeout:10000,
					success:function(data){
						if(data["success"]){
							var list=data["list"][0];
							var glRule=data["greyList_Rule"];
							if(param01==1){
								$("#city").val(list["DATA_CITY"]);
								$("#county").val(list["DATA_CITY"]);
								$("#saName").val(list["RESOURCE_NAME"]);
								$("#saCode").val(list["PRIMARY_VALUE"]);
								$("#dataSource").val("SC对比表");
								$("#glType").val(param02);
								$("#glDescribe").val(list["PROBLEM_DESC"]);
								$("#lastId").val(id);
								$("#tableSource").val(data["table_source"]);
							}else if(param01==2){
								$("#city").val(list["CITY"]);
								if(list["COUNTY"]==null || list["COUNTY"]=="" || list["COUNTY"]=="null"){
									$("#county").val(list["CITY"]);
								}else{
									$("#county").val(list["COUNTY"]);
								}
								$("#saName").val(list["NAME1"]);
								$("#saCode").val(list["NUMBER1"]);
								$("#glType").val(data["gl_type"]);
								$("#glDescribe").val(data["gl_describe"]);
								$("#dataSource").val("在线风控");
								$("#lastId").val(id);
								$("#tableSource").val(data["table_source"]);
							}
							var options="";
							for(var i=0;i<glRule.length;i++){
								options+="<option value='"+glRule[i]+"'>"+glRule[i]+"</option>";
							}
							$("#glRule").html(options);
							$("#city").attr("readonly","readonly");
							//$("#county").attr("readonly","readonly");
							$("#saName").attr("readonly","readonly");
							$("#saCode").attr("readonly","readonly");
							$("#glType").attr("readonly","readonly");
							$("#glDescribe").attr("readonly","readonly");
							$("#dataSource").attr("readonly","readonly");
						}
					}
				});
			}else if(type=="write"){
				$.ajax({
					url:"${pageContext.request.contextPath}/greyListApplyAction/findWriteData.ilf",
					async:true,
					type:"POST",
					dataType:"JSON",
					data:{},
					timeout:10000,
					success:function(data){
						if(data["success"]){
							var glRules=data["greyList_Rule"];
							var option="";
							for(var i=0;i<glRules.length;i++){
								option+="<option value='"+glRules[i]+"'>"+glRules[i]+"</option>";
							}
							$("#glRule").html(option);
							$("#tableSource").val(data["table_source"]);
							$("#dataSource").val("手工填写");
							$("#dataSource").attr("readonly","readonly");
							$("#lastId").val(0);
						}
					}
				});
			}
		});
		function apply(){
			
			$.ajax({
				url:"${pageContext.request.contextPath}/greyListApplyAction/apply.ilf",
				async:true,
				type:"POST",
				dataType:"JSON",
				timeout:10000,
				data:{"city":$("#city").val(),"county":$("#county").val(),"saName":$("#saName").val(),
					"saCode":$("#saCode").val(),"glType":$("#glType").val(),"glDescribe":$("#glDescribe").val(),
					"glRule":$("#glRule").val(),"attribution":$("#attribution").val(),"dataSource":$("#dataSource").val(),
					"validTime":$("#validTime").val(),"lastId":$("#lastId").val(),"tableSource":$("#tableSource").val()},
				timeout:10000,
				success:function(data){
					if(data["success"]){
						$("#panel-body").html('<img src="${pageContext.request.contextPath}/img/yes.png" style="width:100px;height:100px;"/><font size="4">申请发起成功！</font>');
					}else{
						$("#panel-body").html('<img src="${pageContext.request.contextPath}/img/no.png" style="width:100px;height:100px;"/><font size="4">申请出现问题，请填写正确信息！</font>');
					}
				}
			});
		}
		function cancel(){
			$("#panel-body").html('<img src="${pageContext.request.contextPath}/img/yes.png" style="width:100px;height:100px;" /><font size="4">申请取消成功！</font>');
		}
	</script>
</head>
<body style="width:100%;height:100%;border:solid 0px red;" id="bodyHeight">
  	<div class="container" style="width:100%;height:100%;margin-top:0px;">
		<div class="panel panel-primary"  id="mainPanel">
			<div id="panelHeading" style="width:100%;height:50px;background-color:#337ab7;display:table;padding-left:10px">
				<div style="background-color:#337ab7;display:table-cell;vertical-align:middle;padding-left:10px">
					<font id="title" color="white" size="5">灰名单申请编辑</font>
				</div>
			</div>
			<div class="panel-body" style="text-align:center;" id="panel-body">
			
				<div class="layui-form layui-form-pane">
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">地市</label>
					    <div class="layui-input-block">
					      <input type="text" name="city" id="city" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" value="" required="required">
					    </div>
					  </div>
					</div>
				  	<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">区县</label>
					    <div class="layui-input-block">
					      <input type="text" name="county" id="county" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" required="required">
					    </div>
					  </div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">资源名称</label>
					    <div class="layui-input-block">
					      <input type="text" name="saName" id="saName" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" required="required">
					    </div>
					  </div>
					</div>
				  	<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">资源编号</label>
					    <div class="layui-input-block">
					      <input type="text" name="saCode" id="saCode" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" required="required">
					    </div>
					  </div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">灰名单类型</label>
					    <div class="layui-input-block">
					      <input type="text" name="glType" id="glType" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" required="required">
					    </div>
					  </div>
					</div>
				  	<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">灰名单说明</label>
					    <div class="layui-input-block">
					      <input type="text" name="glDescribe" id="glDescribe" style="color:blue;height:38px;" autocomplete="off"  class="layui-input" required="required">
					    </div>
					  </div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">灰名单规则</label>
					    <div class="layui-input-block">
					      <select style="display:block;width:100%;height:38px;" name="glRule" id="glRule">
					        
					      </select>
					    </div>
					  </div>
					</div>
				  	<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">归口专业</label>
					    <div class="layui-input-block">
					      <select style="display:block;width:100%;height:38px;" name="attribution" id="attribution">
					      	<option value="市场">市场</option>
					      	<option value="维护">维护</option>
					      	<option value="财务">财务</option>
					      </select>
					    </div>
					  </div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">数据来源</label>
					    <div class="layui-input-block">
					      <input type="text" name="dataSource" id="dataSource" style="color:blue;height:38px;" autocomplete="off"  class="layui-input">
					    </div>
					  </div>
					</div>
					<div class="col-xs-6 col-sm-6 col-md-6">
					  <div class="layui-form-item">
					    <label class="layui-form-label">有效期</label>
					    <div class="layui-input-block">
					      <select style="display:block;width:100%;height:38px;" name="validTime" id="validTime">
					      	<option value="1">1个月</option>
					      	<option value="2">2个月</option>
					      	<option value="3">3个月</option>
					      	<option value="4">4个月</option>
					      	<option value="5">5个月</option>
					      	<option value="6">6个月</option>
					      	<option value="7">7个月</option>
					      	<option value="8">8个月</option>
					      	<option value="9">9个月</option>
					      	<option value="10">10个月</option>
					      	<option value="11">11个月</option>
					      	<option value="12">12个月</option>
					      </select>
					    </div>
					  </div>
					</div>
					<input type="hidden" name="lastId" id="lastId" value=""/>
					<input type="hidden" name="tableSource" id="tableSource" value=""/>
					<div class="col-xs-12 col-sm-12 col-md-12" style="width:100%;text-align:center;">
						<button onclick="apply()" class="btn btn-success" style="width:180px;height:40px;">确认申请</button>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<button onclick="cancel()" class="btn btn-danger" style="width:180px;height:40px;">取消申请</button>
					</div>
				</div>
			</div>
		</div>
	</div>	
</body>
</html>