<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script type="text/javascript" src="../js/main.js"></script>
<link rel="stylesheet" href="../css/main.css">
</head> 
<body>
	<input type="hidden" id="main_addr" value="${adr}">
	<div id="on_header_div" style="height: 50px;">
		<div id="subject" onclick="location.href='index.jsp'" 
		 style="display: inline-block; padding-top: 10px;  float: left; margin-left: 5%;">
			<img src="/image/wind_white.png" id="on_header_img">&nbsp;&nbsp;
			<b id="title_no">B&B</b>
		</div>
		<c:if test="${sessionScope.login_id == null}">
			<div class="on_header_not_login">
				<a href="../member/login.do">Login</a> 
				&nbsp;&nbsp;&nbsp;
				<a href="../member/signform.do">sign</a>
			</div>
		</c:if>
		<c:if test="${sessionScope.login_id != null }">
			<div class="on_header_login">
				<img src="../image/menu.png" id="on_header_menu">&nbsp;&nbsp;
				${sessionScope.login_id} 님
				<c:if test="${sessionScope.thumb_nail=='noimg.png'}">
					<img src="../image/user.png" id="noimg">
				</c:if>
				<c:if test="${sessionScope.thumb_nail!='noimg.png'}">
					<img src="../save/${sessionScope.thumb_nail}" id="thumb_nail">
				</c:if>
				<div id="real"></div>
				<div id="on_header_menu_bar" style="display: none;">
					<a href="../member/logout.do">Log out</a><br>
					<c:if test="${sessionScope.user_type!='3'}">
						<br><a href="../member/mypage.do">My Page</a><br>
					</c:if>
					<c:if test="${sessionScope.user_type=='2'}">
						<a href="../host/roomform.do">Room Form</a>
					</c:if>
					
					<c:if test="${sessionScope.user_type=='3'}">
						<br><a href="../member/adminPage.do">Admin Page</a>
					</c:if>
				</div>
			</div>
		</c:if>
		<div onclick="location.href='../index.jsp'" style="position:absolute; left:46%; margin-bottom: 50px; top:2%;">
		<img src="../image/wind.png" id="all_header_img">&nbsp;&nbsp;
		<b id="title">B&B</b>
	</div>
	</div>
	<hr id="on_header_hr">
	<div id="main2" style="display: none;">
		<c:import url="/main2.do"/>
	</div>
</body>
</html>