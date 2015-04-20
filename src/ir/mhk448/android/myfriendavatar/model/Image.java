package ir.mhk448.android.myfriendavatar.model;

import java.util.ArrayList;
import java.util.List;

public class Image {

	public List<ImageInfoItem> imageInfoItems;
	
	public Image() {
//		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() ;
		
		imageInfoItems =new ArrayList<ImageInfoItem>(10);
		
//		/storage/sdcard
//		/storage/sdcard0
//		/storage/sdcard1
		
		imageInfoItems.add(new ImageInfoItem("/viber/media/User photos", Contact.IMAGE_TYPE_VIBER));
		imageInfoItems.add(new ImageInfoItem("/watsapp/User photos", Contact.IMAGE_TYPE_VIBER));
		
		
	}
	
	
	
}

class ImageInfoItem {
	
	public String path;
	public String imageType;

	public ImageInfoItem(String path, String imageType) {
		this.path=path;
		this.imageType=imageType;
	}
	
}
