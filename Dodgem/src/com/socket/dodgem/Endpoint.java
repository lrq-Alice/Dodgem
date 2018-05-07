package com.socket.dodgem;


import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//@ServerEndpoint("/play/{param}")
@ServerEndpoint("/play")
public class Endpoint {
	private static final String GUEST_PREFIX = "玩家";
	private int myId;
	private static final AtomicInteger connectionIds = new AtomicInteger(1);
	//保存所有还未配对的用户
	private static final CopyOnWriteArrayList<Endpoint> waitSet= new CopyOnWriteArrayList<Endpoint>();
	// 定义一个集合，用于保存所有接入的WebSocket客户端
	private static final CopyOnWriteArrayList<Endpoint> clientSet = new CopyOnWriteArrayList<Endpoint>();
	// 定义一个成员变量，记录WebSocket客户端的聊天昵称
	private String nickname;
	private int opponent=-1;
	//记录状态
	// 定义一个成员变量，记录与WebSocket之间的会话
	private Session session;
	
	
	public Endpoint() {
		this.myId = connectionIds.getAndIncrement();
	}
	
	
	// 当客户端连接进来时自动激发该方法
	@OnOpen
	public void start(Session session, EndpointConfig config) {
		System.out.print("come");
		this.session = session;
		//,@PathParam("param")String  param
	//	System.out.println(session.getId()+"#############");
		
		// 将WebSocket客户端会话添加到集合中
		clientSet.add(this);
		
		//broadcast("您好！玩家"+myId,myId);
		System.out.println(myId);
		if(waitSet.size()==0){
			//broadcast("当前无可对战玩家，请等待", myId);
			waitSet.add(this);
		}
		else{
			Endpoint oppo=waitSet.get(0);
			oppo.opponent=myId;
			this.opponent=oppo.myId;
			waitSet.remove(0);
			waitSet.remove(this);
			try {
				JSONCreateCouple(myId, oppo.myId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	broadcast("玩家"+oppo.myId+"加入对战", oppo.myId);
		//	broadcast("玩家"+oppo.myId+"加入对战", myId);
		//	broadcast("玩家"+myId+"加入对战", oppo.myId);
		//	broadcast("玩家"+myId+"加入对战", myId);
		}
	}
	
	public void JSONCreateCouple(int myid,  int hisid) throws JSONException{
		JSONObject jsonObject = new JSONObject();  
		jsonObject.put("tag", "pairing success");  
		jsonObject.put("whohost", "youhost");
		broadcast(jsonObject.toString(), myid);
		JSONObject jsonObject2 =new JSONObject();
		jsonObject2.put("tag", "pairing success");  
		jsonObject2.put("whohost", "hehost");
		broadcast(jsonObject2.toString(), hisid);
		
	}
	
	// 当客户端断开连接时自动激发该方法
	@OnClose
	public void end() throws JSONException {
		//一个玩家退出的时候，如果他在等待队列里，就要从队列中退出
		if(waitSet.size()>0&&waitSet.get(0).myId==myId)
			waitSet.remove(this);
		clientSet.remove(this);
	//	String message = new String( "玩家"+myId+"离开了游戏厅！");
		if(opponent!=-1){
			JSONLeave(opponent);
		}else{}
	
	}
	
	public void JSONLeave(int myid) throws JSONException{
		JSONObject jsonObject = new JSONObject();  
		jsonObject.put("tag", "opponent left");  
		broadcast(jsonObject.toString(), myid);
	}
	
	
	// 每当收到客户端消息时自动激发该方法
	@OnMessage
	public void incoming(String message) {
		if(opponent!=-1)
			broadcast(message, opponent);
	}
	
	
	// 当客户端通信出现错误时，激发该方法
	@OnError
	public void onError(Throwable t) throws Throwable {
		connectionIds.getAndDecrement();
		System.out.println("WebSocket服务端错误 " + t);
	}
	
	
	// 实现广播消息的工具方法
	private static void broadcast(String msg,int id) {
	// 遍历服务器关联的所有客户端
		for (Endpoint client : clientSet) {
			if(client.myId==id){
				try {
				
						synchronized (client) {
					// 发送消息
							client.session.getBasicRemote().sendText(msg);
						}
					}
				catch (IOException e) {
						System.out.println("聊天错误，向客户端 " + client.myId + " 发送消息出现错误。");
						clientSet.remove(client);
					try {
						client.session.close();
						} catch (IOException e1) {
						}
						String message =new String( client.nickname+
						"已经被断开了连接。");
							//broadcast(message);
						}
				break;
			}
		}
	}

	
	
	// 定义一个工具方法，用于对字符串中的HTML字符标签进行转义
	private static String filter(String message) {
		if (message == null)
			return null;
		char content[] = new char[message.length()];
		message.getChars(0, message.length(), content, 0);
		StringBuilder result = new StringBuilder(content.length + 50);
		for (int i = 0; i < content.length; i++) {
		// 控制对尖括号等特殊字符进行转义
			switch (content[i]) {
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '&':
					result.append("&amp;");
					break;
				case '"':
					result.append("&quot;");
					break;
				default:
					result.append(content[i]);
			}
		}
		return (result.toString());
	}


}