$(function() {
	$("table").on("click","tr.chat_room_list",function(){
		var room_name = $(this).attr("room_name");
		location.href = "ch_con.do?room_name="+room_name;
	});
	
	$("#chat_List").click(function() {
		location.href = "ch_list.do";
	});
	
/*	$(".chat_room_list").dblclick(function() {
		var room_name = $(this).attr("room_name");
		
		$.ajax({
			type : "post",
			url : "ch_exit_id.aj",
			data : {
				room_name : room_name
			},
			dataType : "json",
			success : function(data) {
				if(data == "0") {
					alert("게스트가 아직 나가지 않아 채팅방을 나갈 수 없습니다.");
				} else if(data == "1") {
					var re = exit(room_name);
					if(re) {
						location.href = "ch_exit.do?room_name="+room_name;
					}
				}
			},
			error : function(e) {
				alert("에러");
			}
		});
	});*/
	
	$("#chat_exit").click(function(event) {
		event.preventDefault();
		var room_name = $(this).attr("room_name");
		
		$.ajax({
			type : "post",
			url : "ch_exit_id.aj",
			data : {
				room_name : room_name
			},
			dataType : "json",
			success : function(data) {
				if(data == "0") {
					alert("게스트가 아직 나가지 않아 채팅방을 나갈 수 없습니다.");
				} else if(data == "1") {
					var re = exit(room_name);
					if(re) {
						location.href = "ch_exit.do?room_name="+room_name;
					}
				}
			},
			error : function(e) {
				alert("에러");
			}
		});
	});
	
	function exit(room) {
		var ch = confirm("채팅방을 나가시겠습니까??");
		if(ch) {
			return true;
		} else {
			return false;
		}
	}
	
});