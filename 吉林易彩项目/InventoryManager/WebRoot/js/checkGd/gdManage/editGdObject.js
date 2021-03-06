Ext.namespace("com.increase.cen.poleline");
var limit = null;
com.increase.cen.poleline.EditWindow = Ext.extend(Ext.form.FormPanel,{
	bodyStyle:"padding:5px;",
	frame:true,
	bodyBorder:false,
	labelAlign:"right",
	buttonAlign:'center',
	labelWidth:120,
	width:800,
	height:300,
	initComponent:function(config){
		limit = Ext.getDom("limit").value;
		Ext.QuickTips.init();
		Ext.form.Field.prototype.msgTarget = 'qtip';
		var idIsExist = true;
		this.levelStore = new Ext.data.SimpleStore({
    		fields:['value','text'],
    		data:[
    		   ['C001','站点'],
    		   ['C002','机房']
    		]
    	})
    	var firstRow = {
			layout:'column',
			items:[{
				height:30,
				layout:'form',
				style:"margin-top:20px",
				items:[{
					id:"gdTaskMain.resourceType",
					name:"gdTaskMain.resourceType",
					xtype:'combo',
					typeAhead:true,
		        	triggerAction:'all',
					fieldLabel:"资源类型",
					emptyText:"",
					blankText:"",
					forceSelection:true,
		         	mode:'local',
		        	width:330,
		         	store:this.levelStore,
		         	valueField:'value', 
		       		displayField:'text',
		       		editable:false
				}]
			}]
		}
		var secondRow = {
			layout:'column',
			items:[{
				height:30,
				layout:'form',
				items:[{
					id:'gdTaskMain.resourceCode',
					name:"gdTaskMain.resourceCode",
					xtype:'hidden'
				},{
					id:'gdTaskMain.resourceName',
					name:"gdTaskMain.resourceName",
					xtype:'textfield',
					fieldLabel:"选定资源",
					allowBlank:false,
					blankText:"",
					emptyText:"",
					width:330
				}]
			}]
		}
    	var thirdRow = {
			layout:'column',
			items:[{
				height:30,
				layout:'form',
				items:[{
					id:'gdTaskMain.receiveRegionCode',
					name:"gdTaskMain.receiveRegionCode",
					xtype:'hidden'
				},{
					id:'gdTaskMain.receiveRegionName',
					name:"gdTaskMain.receiveRegionName",
					xtype:'textfield',
					fieldLabel:"选定接收组",
					allowBlank:false,
					blankText:"",
					emptyText:"",
					width:330
				}]
			}]
		}
    	var forthRow = {
			layout:'column',
			items:[{
				height:30,
				layout:'form',
				items:[{
					id:'gdTaskMain.taskSubject',
					name:"gdTaskMain.taskSubject",
					xtype:'textfield',
					fieldLabel:"任务主题",
					allowBlank:false,
					blankText:"",
					emptyText:"",
					width:330
				}]
			}]
		}
    	var fiveRow = {
			layout:'column',
			items:[{
				height:30,
				layout:'form',
				items:[{
					id:'gdTaskMain.taskDescribe',
					name:"gdTaskMain.taskDescribe",
					xtype:'textfield',
					fieldLabel:"任务描述",
					allowBlank:false,
					blankText:"",
					emptyText:"",
					width:330
				}]
			}]
		}
		this.items = [{
			xtype:'fieldset',
			title:'工单信息 ',
			collapsible:true,
			height:230,
			items:[
			    firstRow,secondRow,thirdRow,forthRow,fiveRow
			]
		}];
		this.buttons = [{
			id:'addBtnSubmit',
			text:"保存",
			hidden:false,
			icon:"imgs/save_btn.png",
			cls:"x-btn-text-icon",
			tooltip:'保存核查内容',
			tooltipType:'qtip',
			handler:this.addBtnSubmit.createDelegate(this)
		},{
			xtype:'tbspacer'
		},{
			text:"返回",
			icon:"imgs/back.png",
			cls:"x-btn-text-icon",
			tooltip:'关闭此窗口',
			tooltipType:'qtip',
			handler:this.btnClose.createDelegate(this)
		}];
		com.increase.cen.poleline.EditWindow.superclass.initComponent.call(this);
	},
	/*保存*/
	addBtnSubmit:function(){
		if(!this.getForm().isValid()){
			return;
		}
		var form = this.getForm();
		form.doAction("submit",{
			url:'checkConfigAction!insertModel.action',
			method:'post',
			submitEmptyText:false,
			success:function(form,action){
				Ext.Msg.show({
					title:'提示',
					width:300,
					msg:'<img src="imgs/tip_success.png" align="center" hspace="30" />编辑核查信息成功!',
					buttons:{
						ok:"确定"
					}
				});
				Ext.getCmp("thisEditForm").getForm().reset();
				Ext.getCmp('editFormWindow').close();
				Ext.getCmp('configDataGrid').getStore().load({
					params:{
						start:0,
						limit:limit
					}
				});
				Ext.getCmp('configGrid').getView().refresh();
			},
			failure:function(form,action){
				Ext.Msg.show({
					title:'提示',
					width:300,
					msg:'<img src="imgs/tip_error.png" align="center" hspace="30"/>编辑核查信息失败',
					buttons:{
						ok:"确定"
					}
				});
			}
		});
	},
	/*返回按钮*/
	btnClose:function(){
		Ext.getCmp("thisEditForm").getForm().reset();
		Ext.getCmp('editFormWindow').close();
		Ext.getCmp('configDataGrid').getStore().load({
			params:{
				start:0,
				limit:limit
			}
		});
	}
});
Ext.reg("editConfigType", com.increase.cen.poleline.EditWindow);