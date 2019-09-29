package o2.persistence;

import java.util.List;

import o2.data.chat_room_dto;
import o2.data.chat_content_dto;

public interface chat_dao {
	// 채팅방 있는지 확인
	public chat_room_dto chat_room_dto(String room_name) throws Exception;
	
	// 채팅방 만들기
	public void chat_room_in(String room_name) throws Exception;
	
	// 채팅방 리스트 가져오기
	public List<chat_room_dto> chat_room_list(String id, String user_type) throws Exception;
	
	// 채팅방 내용 넣기
	public void chat_con_in(chat_content_dto dto) throws Exception;
	
	// 채팅방 내용 불러오기
	public List<chat_content_dto> chat_con_list(String room_name, String id) throws Exception;
	
	// 채팅방 삭제1
	public void chat_room_per_dec(String id, String room_name) throws Exception;
	
	// 채팅방 삭제2
	public void chat_room_val_update(String room_name) throws Exception;
	
	public void chat_con_del_ch_update(String room_name, String del_ch) throws Exception;
	
	// 채팅방 재생성1
	public void chat_room_per_inc(String room_name) throws Exception;
	
	// 채팅방 재생성2
	public void chat_room_re_in(String room_name) throws Exception;
	
	// 채팅방 내용 확인 여부
	public void chat_con_read_ch(String id, String room_name) throws Exception;
	
	// 채팅방 내용 읽은 확인 갯수
	public String chat_con_new_cnt(String id, String room_name) throws Exception;
}
