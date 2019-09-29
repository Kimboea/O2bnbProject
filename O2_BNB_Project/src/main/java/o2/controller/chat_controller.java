package o2.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import o2.data.chat_room_dto;
import o2.data.chat_content_dto;
import o2.service.chat_service_imple;
import o2.service.host_service_imple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class chat_controller {
	@Autowired
	chat_service_imple service;
	
	@Autowired
	host_service_imple h_service;
	
	//[1]. 채팅 리스트
	@RequestMapping("/chat/ch_list.do")
	public ModelAndView chatList(HttpServletRequest req) throws Exception{
		ModelAndView model = new ModelAndView();
		HttpSession sess = req.getSession();
		
		// 1. 필요 변수(id, user_type) 선언 및 생성
		HttpSession session = req.getSession();
		String id = (String)session.getAttribute("login_id");
		String user_type = (String)session.getAttribute("user_type");
		
		// 2. 채팅방 리스트 가져오기
		//    user_type가 1(user)이면 room_name의 뒤에 해당하는 것만
		//	  user_type가 2(host)이면 room_name의 앞에 해당하는 것만 
		//	  list로 뽑아서 가져오기
		// ** room_name = host_id + "-" + user_id
		List<chat_room_dto> room_list = service.chat_room_list(id, user_type);
		model.addObject("room_list", room_list);
			
		// 3. ip 따기
		req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = req.getRemoteAddr();
            if(ip.equals("0:0:0:0:0:0:0:1")) {
            	// 3-1. local인 경우
            	sess.setAttribute("ip", "localhost");
            } else {
            	// 3-1. local이 아닌 경우
                InetAddress local;
                try {
                    local = InetAddress.getLocalHost();
                    String localip = local.getHostAddress();
                    sess.setAttribute("ip", localip);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        }

        // 4. websocet 연결시 어디서 연결하는지 구분하기 위해 
        //	  chat_websocet_ch : 0 -> chatting list에서 연결
        //    chat_websocet_ch : 1 -> chatting room, creat 하는 경우 연결
        sess.setAttribute("chat_websocet_ch", "0");
        
		// 5. 페이지 이동
		model.setViewName("/o2_chatting/chat_list");
		return model;
	}
	
	//[2]. 방만들기 - 일반 유저만 만들 수 있음
	@RequestMapping("/chat/ch_room.do")
	public ModelAndView chat_room_insert(HttpServletRequest req) throws Exception{
		ModelAndView model = new ModelAndView();
		HttpSession sess = req.getSession();
		
		// 1. 필요 변수(host_id, user_id, room_name) 선언 및 생성
		String host_id = h_service.home_select_data(Integer.parseInt(req.getParameter("num"))).getId();
		String user_id = (String)sess.getAttribute("login_id");
		String room_name = host_id + "-" + user_id;	

		// 2. 채팅방 존재하는지 확인
		//    채팅방이 존재하지 않거나 기존에 존재하던 방이 비활성화 상태인 경우 websocet의 연결 부분에서 
		//	    채팅방 생성 또는 활성화로 업데이트 함
		chat_room_dto ch_room_dto = service.chat_room_dto(room_name);
		if(ch_room_dto!=null && ch_room_dto.getValidity().equals("1")) {
			// 2-1. 채팅방이 아직 활성화 상태인 경우
			if(ch_room_dto.getExit_id()!=null && 
			   ch_room_dto.getExit_id().equals(user_id)) {
				// 2-2. 유저만 나간 경우로 person, exit_id 업데이트
				service.chat_room_per_inc(room_name);
			} else if(ch_room_dto.getExit_id()==null) {
				// 2-2. 둘다 채팅방에 있는 경우 채팅방 내용 불러오기
				List<chat_content_dto> list = service.chat_con_list(room_name, user_id);
				if(list!=null) {
					model.addObject("list", list);
				}
			}
		}
		
        // 3. websocet 연결시 어디서 연결하는지 구분하기 위해 
        //	  chat_websocet_ch : 0 -> chatting list에서 연결
        //    chat_websocet_ch : 1 -> chatting room, creat 하는 경우 연결
		sess.setAttribute("chat_websocet_ch", "1");
		// 3. room_name 세션에 담기 Websocket에서 필요
		sess.setAttribute("room_name", room_name);
			
		// 4. ip 따기
		req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = req.getRemoteAddr();
            if(ip.equals("0:0:0:0:0:0:0:1")) {
            	sess.setAttribute("ip", "localhost");
            } else {
                InetAddress local;
                try {
                    local = InetAddress.getLocalHost();
                    String localip = local.getHostAddress();
                    sess.setAttribute("ip", localip);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        }
		
		// 5. 필요 객체 담아서 보내기
		model.addObject("room", room_name);
		model.addObject("id", user_id);
		
		// 6. 페이지 이동
		model.setViewName("/o2_chatting/chat_form");
		
		return model;
	}
	
	//[3]. 채팅방 입장
	@RequestMapping("/chat/ch_con.do")
	public ModelAndView chat_content(HttpServletRequest req,
									 @RequestParam(value="room_name", required=false) String room_name) throws Exception {
		ModelAndView model = new ModelAndView();
		HttpSession sess = req.getSession();
		
		// 1. 필요 변수(id) 선언 및 생성
		String id = (String)sess.getAttribute("login_id");
		
		// 2. 채팅방 구분
		//	  room_name이 not null인 경우 : list에서 입장
		//	  room_name이 null인 경우 : home 상세정보에서 입장 즉 채팅방 생성
		if(room_name!=null) {
			//2-1. 채팅 내용 읽음으로 업데이트
			service.chat_con_read_ch(id, room_name);
			
			//2-2. 채팅 내용 불러오기
			List<chat_content_dto> list = service.chat_con_list(room_name, id);
			if(list!=null) {
				model.addObject("list", list);
			}
			
			// 2-3. 필요 객체 담아서 보내기
			model.addObject("room", room_name);
			model.addObject("id", id);
		} else if(room_name==null) {
			model.addObject("room_first", "room_first");
		}
		
		// 3. ip 따기
		req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = req.getRemoteAddr();
            if(ip.equals("0:0:0:0:0:0:0:1")) {
            	sess.setAttribute("ip", "localhost");
            } else {
                InetAddress local;
                try {
                    local = InetAddress.getLocalHost();
                    String localip = local.getHostAddress();
                    sess.setAttribute("ip", localip);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
            }
        }
        
        // 4. 필요 객체 담아서 보내기
        //	  room_ch가 null 이면 채팅방 생성 room_ch이면 기존 채팅방에 입장 함을 구분하기 위함
        model.addObject("room_ch", "room_ch");
		sess.setAttribute("room_name", room_name);
		sess.setAttribute("chat_websocet_ch", "1");
	
		// 5. 페이지 이동
		model.setViewName("/o2_chatting/chat_form");
		return model;
	}
	
	//[4]. 채팅방 나가기 아이디 체크
	@RequestMapping("/chat/ch_exit_id.aj")
	public @ResponseBody String ch_exit_id_ch(HttpServletRequest req,
								@RequestParam String room_name) throws Exception {
		HttpSession sess = req.getSession();
		
		// 1. 필요 변수(exit_id, result) 선언 및 생성
		String exit_id = (String)sess.getAttribute("login_id");
		String result = "";
		
		// 2. 확인
		// 2-1. 해당 방이름에서 person이 2인 경우 나가는 사람이 
		//      사용자이면 방나가기 가능 호스트 이면 불가능
		chat_room_dto dto = service.chat_room_dto(room_name);
		if(dto.getPerson().equals("2") && room_name.split("-")[1].equals(exit_id)) {
			//2-2. 나가는 사람이 사용자 임
			result = "1";
		} else if(dto.getPerson().equals("2") && !room_name.split("-")[1].equals(exit_id)) {
			//2-2. 나가는 사람이 호스트인 경우
			result = "0";
		} else if(dto.getPerson().equals("1")) {
			//2-2. 유저가 나가고 나가는 사람이 호스트인 경우
			result = "1";
		}
		
		return result;
	}
	
	//[5]. 채팅방 나가기
	@RequestMapping("/chat/ch_exit.do")
	public String ch_exit(HttpServletRequest req,
						  @RequestParam String room_name) throws Exception {
		HttpSession sess = req.getSession();
		
		// 1. 필요 변수(exit_id) 선언 및 생성
		String exit_id = (String)sess.getAttribute("login_id");
		if(room_name.split("-")[1].equals(exit_id)) {
			// 1-1. 나가는 사람 일반 사용자니까 나가도 됨
			service.chat_room_per_dec(exit_id, room_name);
			service.chat_con_del_ch_update(room_name, exit_id);
		} else {
			// 1-2. 나가는 사람이 호스트니까 사용자가 나간 후 방 삭제 가능
			service.chat_room_val_update(room_name);
			service.chat_con_del_ch_update(room_name, "all");
		}
		return "redirect:ch_list.do";
	}
}
