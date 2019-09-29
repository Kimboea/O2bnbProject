<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script src="//code.jquery.com/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script type="text/javascript" src="../js/chat.js"></script>
<link rel="stylesheet" href="../css/chat.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head> 
<body>
	<c:import url="/all_header.do"/>	
<div id="chat_form" class="container" >
	<h2>채팅방 리스트</h2>
	<table style="width: 40%; height: 454px; overflow-y: scroll; overflow-w:inherit;">
		<c:forEach var="room_dto" items="${room_list}">
			<tr class="chat_room_list" room_name="${room_dto.room_name}">
				<td style="text-align: left; width: 38%;">${room_dto.room_name}</td>
				<c:if test="${room_dto.new_content_cnt!='0'}">
				 	<td style="text-align: center; width: 2%; color:red;">
				 	 ${room_dto.new_content_cnt}
				 	</td>
				</c:if>
				<c:if test="${room_dto.new_content_cnt=='0'}">
				 	<td style="text-align: center; width: 2%; color:red;"></td>
				</c:if>
			</tr>
		</c:forEach>
	</table>
	<div id="result"></div>
</div>
<script type="text/javascript">
	var sock = null;
	var ip = '${sessionScope.ip}';
	var ws = new WebSocket("ws://"+ip+":9001/O2_BNB_Project/echo");
	
	// 서버로부터 받은 메세지 보내주기
	ws.onmessage = function(message) {
		// 메세지
		var jsonData = JSON.parse(message.data);
		
		if(jsonData.new_content_cnt!=null) {
			var room_create = jsonData.new_content_cnt.split("!%/")[0];
			var room_name = jsonData.new_content_cnt.split("!%/")[1];
			var new_cnt = jsonData.new_content_cnt.split("!%/")[2];
			console.log("room_create : " + room_create + " room_name : " + room_name + " new_cnt : " + new_cnt);
			if(room_create=='yes') {
				$("table").prepend("<tr class='chat_room_list' room_name='"+room_name+"'>"
								+	"<td style='text-align: left; width: 38%;'>" + room_name + "</td>"
								+	"<td style='text-align: center; width: 2%; color:red;'>"+new_cnt+"</td>"
								+ "</tr>");
			} else if(room_create=='no') {
				$("tr.chat_room_list").each(function(index, item) {
					if($(item).attr("room_name")==room_name) {
						$(item).children("td:eq(1)").html(new_cnt);
					}
				});
			}
		}
	}
	
	ws.onclose = function(event) {};
</script>
</body>
</html>