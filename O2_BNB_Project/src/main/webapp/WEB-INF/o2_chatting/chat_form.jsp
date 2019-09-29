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

	<!-- 유저 아이디 -->
	<input type="hidden" id="login_id" value="${id}">
	<!-- 현재 유저가 접속한 방이름 -->
	<input type="hidden" id="room" value="${room}">
	<!-- 호스트 아이디 -->
	<input type="hidden" id="host_id" value="${host_id}">
<c:import url="/all_header.do"/>	
<div id="chat_form" class="container" >
	<c:if test="${room_ch==null}">
		<h2>방이름&lt;<b>${room}</b>&gt;</h2>
	</c:if>
	<c:if test="${room_ch!=null}">
			<h2>방이름&lt;<b>${room}</b>&gt;</h2>
			<a href="#" id="chat_List" room_name="${room}">Chatting List</a>
			<a href="#" id="chat_exit" room_name="${room}">Exit</a><br>
	</c:if>
	
	<!-- 채팅방 구현하기 위한 테이블 -->
	<table>
		<tr>
			<td>
				<div style="width: 100%; height: 400px; overflow-y: scroll; overflow-w:inherit;"
				 id = "output">
				 <c:if test="${list!=null}">
				 	<c:forEach var="dto" items="${list}">
				 		<c:if test="${dto.writer==sessionScope.login_id}">
				 			<div style="color: blue; text-align: right;">${dto.content}</div>
				 		</c:if>
				 		<c:if test="${dto.writer!=sessionScope.login_id}">
				 			<div style="text-align: left;">${dto.content}</div>
				 		</c:if>
				 	</c:forEach>
				 </c:if>
				</div>
			</td>
		</tr>
		
		<!-- 입력 창 -->
		<tr>
			<td>
			<div class="form-group" style="display: inline-block;">
				<input type="text" id="textID" size="50" style="width: 80%; height: 22px;
				 margin-left: 0; margin-right: 10px; padding-top: 3px;"
				 name="chatInput" placeholder="Message">
				<input type="button" value="Send" id="buttonMessage" style="width: 10%; height: 30px;">
			</div>
			</td>
		</tr>
	</table>
</div>
	<script type="text/javascript">
	$("#buttonMessage").click(function() {
		if('${room_first}'!= '') {
			$("#textID").val("");
			$("#textID").focus();
		}
	});
	
	$("#textID").keypress(function(event) {
		if(event.which=="13") {
			event.preventDefault();
			if('${room_first}'!= '') {
				$("#textID").val("");
				$("#textID").focus();
			}
		}
	});
	
	if('${room_first}'== '') {
		var sock = null;
		var ip = '${sessionScope.ip}';

		/* 처음 접속시 메세지 입력창에 포커스 시킴 */
		$("#textID").focus();
		var ws = new WebSocket("ws://"+ip+":9001/O2_BNB_Project/echo");
		
		// 서버로 메세지 보낼때
		ws.onopen = function() {
			// 보내기 버튼 눌렀을 때
			$("#buttonMessage").click(function() {
				// 메세지
				var msg = $("input[name=chatInput]").val().trim("!%/");
				// 방이름(전체카톡이면 all)
				var room = $("#room").val().trim("!%/");

				// 소켓에 메세지 전달
				ws.send(msg+"!%/"+room);
				$("#output").append("<div style='color:blue; text-align: right;'>"+msg+"</div>");
				// 글 입력시 무조건 하단으로 보냄
				$("#output").scrollTop(99999999);
				// 입력 후 창 초기화
				$("#textID").val("");
				// 입력 창에 포커스 시킴
				$("#textID").focus();
			});
			
			// 엔터키 입력 처리
			$("#textID").keypress(function(event) {
				if(event.which=="13") {
					event.preventDefault();
					// 메세지
					var msg = $("input[name=chatInput]").val().trim("!%/");
					// 방이름 - 전체단톡이면 all
					var room = $("#room").val().trim("!%/");
					
					// 소켓에 메세지 전달
					ws.send(msg+"!%/"+room);
					$("#output").append("<div style='color:blue; text-align: right;'>"+msg+"</div>");
					// 글 입력시 무조건 하단으로 보냄
					$("#output").scrollTop(99999999);
					// 입력 후 창 초기화
					$("#textID").val("");
					// 입력 창에 포커스 시킴
					$("#textID").focus();
				}
			
			});
		}
		
		// 서버로부터 받은 메세지 보내주기
		ws.onmessage = function(message) {
			// 메세지
			var jsonData = JSON.parse(message.data);
			
			if(jsonData.message!=null) {
				$("#output").append(jsonData.message);
				$("#output").scrollTop(99999999);
			}
		}
		
		// 닫힐때
		ws.onclose = function(event) {};
	}
	</script>
</body>
</html>
