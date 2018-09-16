package com.hit.server;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.print.attribute.HashAttributeSet;
import javax.sound.midi.ControllerEventListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hit.dm.DataModel;
import com.hit.services.CacheUnitController;

public class HandleRequest<T> implements Runnable {

	private Socket socket;
	private CacheUnitController<T> cacheUnitController;
	private Gson gson;
	private Type ref;
	private Request<DataModel<T>[]> request;
	private Scanner reader;
	private String action;
	private Map<String, String> headers;
	private DataModel<T>[] body;

	public HandleRequest(Socket s, CacheUnitController<T> controller) {
		this.socket = s;
		this.cacheUnitController = controller;
		this.gson = new Gson();
		this.headers = new HashMap<String, String>();
	}

	@Override
	public void run() {
		try {
			this.reader = new Scanner(new InputStreamReader(socket.getInputStream()));
			this.ref = new TypeToken<Request<DataModel<T>[]>>() {
			}.getType();
			request = new Gson().fromJson(reader.nextLine(), ref);
			this.headers = new HashMap<String, String>(request.getHeaders());
			this.body = request.getBody();
			while (true) {
				if (headers.containsValue("UPDATE")) {
					cacheUnitController.update(body);
				} else if (headers.containsValue("GET")) {
					cacheUnitController.get(body);
				} else if (headers.containsValue("DELETE")) {
					cacheUnitController.delete(body);
				}
				else
					break;
			}

		} catch (Exception e) {

		}
	}

}