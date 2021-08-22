package cn.lianshi.library.mvvmbase.widget.jsbridge;

public class DefaultHandler implements BridgeHandler {

	String TAG = "DefaultHandler";

	@Override
	public void handler(String data, CallBackFunction function) {
//		System.out.println("sendData=" + data);
		if(function != null){
			function.onCallBack("ni hao ma !!!!");
		}

	}

}
