<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
  
	<div style="width:600px;height:240px;
	overflow-y:auto;border:1px solid #333;" id="show"></div>


   	<script type="text/javascript">
// 创建WebSocket对象

var webSocket = new WebSocket("ws://localhost:8080/Dodgem/play");


var sendMsg = function()
{
	var inputElement;
		inputElement = document.getElementById('msg');
		webSocket.send(inputElement.value);
	// 发送消息
	// 清空单行文本框
	inputElement.value = "";
}
/*global event listening
 document.onkeydown=function(e){   
 	alert("!");
 }
 */

document.onkeydown=function(event){
	//sendMsg(event.keyCode);
	if(event.keyCode==37||event.keyCode==38||event.keyCode==39||event.keyCode==40)
		webSocket.send(event.keyCode);
}

var send = function(event)
{
	
	if (event.keyCode == 13)
	{
		sendMsg(event.keyCode);
	}
};

webSocket.onopen = function()
{
	// 为onmessage事件绑定监听器，接收消息
	webSocket.onmessage= function(event)
	{
	var show = document.getElementById('show');
	// 接收、并显示消息 event.data
	show.innerHTML += event.data + "<br/>";
	show.scrollTop = show.scrollHeight;
	}
};


</script>

  </body>
</html>
