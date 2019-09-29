package o2.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import o2.data.chat_room_dto;
import o2.data.chat_content_dto;
import o2.service.chat_service_imple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class Websocket extends TextWebSocketHandler{
	@Autowired
	chat_service_imple service;
	
	//1. 필요 변수 생성
	//1-1. 메세지를 날려주기 위한 웹소켓 전용 세션
	private List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();
	//1-1. 실제 세션의 id정보, web 소켓 정보
	private Map<WebSocketSession, String> mapList = new HashMap<WebSocketSession, String>();
	//1-1. 실제 세션의 id정보, room 정보
	private Map<WebSocketSession, String> roomList = new HashMap<WebSocketSession, String>();
	
	//[1]. 연결되었을때
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		
		// 1. 로그인 아이디와 방이름 가져오기
		Map<String, Object> map = session.getAttributes();
		String session_id = map.get("login_id").toString();
		String chat_websocet_ch = map.get("chat_websocet_ch").toString();
		// chat_websocet_ch : 0 -> chatting list에서 연결
        // chat_websocet_ch : 1 -> chatting room, creat 하는 경우 연결
		if(chat_websocet_ch.equals("1")) {
			String room_name = map.get("room_name").toString();
			// 1-1. 맵에 세션, 방이름 담기
			roomList.put(session, room_name);
		}
		
		// 2. 맵에 세션, 아이디 담기
		mapList.put(session, session_id);
		
		// 3. 실제 websocet sesssion 정보 담기
		sessionList.add(session);
	}
	
	//[2]. 서버가 클라이언트로부터 메세지 받은 경우
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 1. 필요 젼수 생성 및 선언
		Map<String, Object> map = session.getAttributes();
		String id = map.get("login_id").toString();
		String user_type = map.get("user_type").toString();
		String room_name = map.get("room_name").toString();
		String ip = map.get("ip").toString();
		String room_create = "no";
		
		// 2. 넘어온 메세지 값 분리하기 
		//	  msgArr[0] : msg, msgArr[1] : room_name
		String msgArr[] = new String[2];
		msgArr = message.getPayload().split("!%/");
		
		// 3. 채팅방 생성 및 활성화 업데이트
		chat_room_dto ch_room_dto = service.chat_room_dto(room_name);
		if(ch_room_dto==null) {
			//3-1. ch_room_dto가 null이면 아예 방을 처음 만드는 것
			service.chat_room_in(room_name);
			room_create = "yes";
		} else if(ch_room_dto!=null && ch_room_dto.getValidity().equals("0")) {
			//3-1. 기존에 방이 있다가 둘 다 나가서 다시 활성화 하는 경우
			service.chat_room_re_in(room_name);
			room_create = "yes";
		}
		
		// 4. 채팅 내용 인설트
		chat_content_dto ch_con_dto = new chat_content_dto();
		ch_con_dto.setContent(msgArr[0]);
		ch_con_dto.setWriter(id);
		ch_con_dto.setIp(ip);
		ch_con_dto.setRoom_name(room_name);
		ch_con_dto.setRead_ch(id);
		service.chat_con_in(ch_con_dto);
		
		// 5. 메세지 보내기
		for(WebSocketSession sess : sessionList) {
			// 5-1. 대화 사용자들이 모두 방에 존재하는 경우
			if(msgArr[1].equals(roomList.get(sess))) {
				// 5-1-1. 나를 제외한 같은방 멤버에게만 보내기
				if(!session.getId().equals(sess.getId())) {
					sess.sendMessage(new TextMessage(JsonData(msgArr[0])));
				}
			}
			
			// 5-1. 대화 사용자들 중 한명은 채팅방에 있고 다른 한명은 채팅리스트에 있는 경우
			if(!msgArr[1].equals(roomList.get(sess))) {
				String recive_id = "";
				if(user_type.equals("1")) {
					recive_id = room_name.split("-")[0];
				} else if(user_type.equals("2")) {
					recive_id = room_name.split("-")[1];
				}
				
				if(recive_id.equals(mapList.get(sess))) {
					sess.sendMessage(new TextMessage(JsonNewMsg(recive_id, room_name, room_create)));
				}
			}
		}
	}
	
	// json 형태로 메세지 변환 - 일반 메세지 보낼때
	public String JsonData(Object msg) {
		JsonObject jsonObject = Json.createObjectBuilder().add("message", 
				"<div style='text-align: left;'>" + msg + "</div>").build();
		StringWriter write = new StringWriter();
		
		try(JsonWriter jsonWriter = Json.createWriter(write)) {
			jsonWriter.write(jsonObject);
		};
		
		return write.toString();
	}
	
	public String JsonNewMsg(String id, String room_name, String room_create) throws Exception {
		String new_content_cnt = service.chat_con_new_cnt(id, room_name);
		
		JsonObject jsonObject = Json.createObjectBuilder().add("new_content_cnt",
				room_create + "!%/" + room_name + "!%/" + new_content_cnt).build();
		
		StringWriter write = new StringWriter();
		
		try(JsonWriter jsonWriter = Json.createWriter(write)) {
			jsonWriter.write(jsonObject);
		};

		return write.toString();
	}
	
	// 통신끊었을떄
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		roomList.remove(session);		// key : 세션 , value : 방이름
		mapList.remove(session);		// key : 세션 , value : 아이디
		sessionList.remove(session); 	// 실제 websocket 세션명
	}
}
