package o2.data;

public class chat_room_dto {
	private String room_name;
	private String person;
	private String validity;
	private String exit_id;
	private String new_content_cnt;
	
	public String getRoom_name() {
		return room_name;
	}
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}
	public String getExit_id() {
		return exit_id;
	}
	public void setExit_id(String exit_id) {
		this.exit_id = exit_id;
	}
	public String getNew_content_cnt() {
		return new_content_cnt;
	}
	public void setNew_content_cnt(String new_content_cnt) {
		this.new_content_cnt = new_content_cnt;
	}
	
}
